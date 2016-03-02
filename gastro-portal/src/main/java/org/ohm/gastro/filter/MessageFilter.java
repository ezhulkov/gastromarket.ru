package org.ohm.gastro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.UserEntity;
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
import java.util.stream.Collectors;

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
                if (httpServletRequest.getMethod().equals("GET")) {
                    final Long cid = Long.parseLong(httpServletRequest.getParameter("cid"));
                    final Integer from = Integer.parseInt(ObjectUtils.defaultIfNull(httpServletRequest.getParameter("from"), "0"));
                    final Integer to = Integer.parseInt(ObjectUtils.defaultIfNull(httpServletRequest.getParameter("to"), "50"));
                    final ConversationEntity conversation = conversationService.find(cid);
                    if (conversation == null) {
                        httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    final Converstation response = new Converstation(conversationService.findAllComments(conversation, from, to), conversation);
                    final byte[] bytes = objectMapper.writeValueAsBytes(response);
                    write(httpServletResponse, bytes);
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

        public Converstation(List<CommentEntity> messages, ConversationEntity conversation) {
            this.messages = messages.stream().map(Message::new).collect(Collectors.toList());
            this.conversation = conversation;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public Long getId() {
            return conversation.getId();
        }

        public User getSender() {
            return new User(conversation.getSender());
        }

        public User getRecipient() {
            return new User(conversation.getRecipient());
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

    }

    private class Message {

        private final CommentEntity comment;

        public Message(CommentEntity comment) {
            this.comment = comment;
        }

        public Long getId() {
            return comment.getId();
        }

        public String getText() {
            return comment.getText();
        }

        public User getAuthor() {
            return new User(comment.getAuthor());
        }

        public Date getDate() {
            return comment.getDate();
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

        public String getPhoto() {
            return user.getLinkAvatar();
        }

        public String getUrl() {
            return user.getLinkUrl();
        }

    }

}