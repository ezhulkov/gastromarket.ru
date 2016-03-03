package org.ohm.gastro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.ConversationService;
import org.ohm.gastro.service.UserService;
import org.ohm.gastro.service.impl.ApplicationContextHolder;
import org.ohm.gastro.trait.Logging;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * Created by ezhulkov on 23.08.14.
 */
public class MessageFilter extends BaseApplicationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private UserService userService;
    private ConversationService conversationService;

    @Override
    protected void initFilterBean() throws ServletException {
        conversationService = ApplicationContextHolder.getBean(ConversationService.class);
        userService = ApplicationContextHolder.getBean(UserService.class);
    }


    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
                                    final HttpServletResponse httpServletResponse,
                                    final FilterChain filterChain) throws ServletException, IOException {
        final boolean needToLog = !isStaticResource(httpServletRequest);
        try {
            if (needToLog) {
                final Optional<UserEntity> authenticatedUserOpt = BaseComponent.getAuthenticatedUser(userService);
                if (authenticatedUserOpt.isPresent()) {
                    final UserEntity user = authenticatedUserOpt.get();
                    final Long oid = Long.parseLong(httpServletRequest.getParameter("oid"));
                    final UserEntity opponent = userService.findUser(oid);
                    if (opponent == null) {
                        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                    if (httpServletRequest.getMethod().equals("GET")) {
                        final Integer from = Integer.parseInt(defaultIfNull(httpServletRequest.getParameter("from"), "0"));
                        final Integer to = Integer.parseInt(defaultIfNull(httpServletRequest.getParameter("to"), "50"));
                        final ConversationEntity conversation = conversationService.findConversation(user, opponent);
                        if (conversation == null) {
                            httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
                            return;
                        }
                        final Converstation response = new Converstation(conversationService.findAllComments(conversation, from, to), conversation, user);
                        final byte[] bytes = objectMapper.writeValueAsBytes(response);
                        write(httpServletResponse, bytes);
                        conversation.setLastSeenDate(new Date());
                        conversationService.save(conversation);
                    } else if (httpServletRequest.getMethod().equals("POST")) {
                        final String type = httpServletRequest.getParameter("type");
                        final String text = IOUtils.toString(httpServletRequest.getInputStream());
                        final ConversationEntity conversation = conversationService.findOrCreateConversation(user, opponent);
                        if ("text".equals(type)) {
                            final CommentEntity comment = new CommentEntity();
                            comment.setText(text);
                            conversationService.placeComment(conversation, comment, user);
                            final Converstation response = new Converstation(conversationService.findAllComments(conversation, 0, 50), conversation, user);
                            final byte[] bytes = objectMapper.writeValueAsBytes(response);
                            write(httpServletResponse, bytes);
                        }
                    }
                } else {
                    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
            }
        } catch (Exception e) {
            Logging.logger.error("", e);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void write(HttpServletResponse response, byte[] bytes) throws IOException {
        try (
                final OutputStream os = response.getOutputStream();
        ) {
            response.setHeader("Content-Type", "text/json");
            os.write(bytes);
            os.flush();
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private class Converstation {

        private final List<Message> messages;
        private final ConversationEntity conversation;
        private final UserEntity author;

        public Converstation(List<CommentEntity> messages, ConversationEntity conversation, UserEntity author) {
            this.messages = messages == null ? null : messages.stream().map(t -> new Message(t, author)).collect(Collectors.toList());
            this.conversation = conversation;
            this.author = author;
        }

        public Long getId() {
            return conversation.getId();
        }

        public User getOpponent() {
            return new User(conversation.getOpponent(author).get());
        }

        public Date getLastSeenDate() {
            return conversation.getLastSeenDate();
        }

        public Date getDate() {
            return conversation.getDate();
        }

        public Date getLastActionDate() {
            return conversation.getLastActionDate();
        }

        public List<Message> getMessages() {
            return messages;
        }

    }

    private class Message {

        private final CommentEntity comment;
        private final UserEntity author;

        public Message(CommentEntity comment, UserEntity author) {
            this.comment = comment;
            this.author = author;
        }

        public Long getId() {
            return comment.getId();
        }

        public String getText() {
            return comment.getTextRaw();
        }

        public User getAuthor() {
            return new User(comment.getAuthor());
        }

        public Date getDate() {
            return comment.getDate();
        }

        public String getDatePrintable() {
            return comment.getDatePrintable();
        }

        public String getRead() {
            final Date lastSeenDate = ((ConversationEntity) comment.getEntity()).getLastSeenDate();
            return lastSeenDate == null || !comment.getAuthor().equals(author) && lastSeenDate.before(comment.getDate()) ? "unread" : "read";
        }

    }

    private class User {
        private final UserEntity user;

        public User(UserEntity user) {
            this.user = user;
        }

        public Long getId() {
            return user.getId();
        }

        public String getName() {
            return user.getLinkName();
        }

        public String getAvatar() {
            return user.getLinkAvatar();
        }

        public String getUrl() {
            return user.getLinkUrl();
        }

    }

}