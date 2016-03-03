package org.ohm.gastro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.dto.ConversationDTO;
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
import java.util.HashMap;
import java.util.Optional;

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
                    final String type = httpServletRequest.getParameter("type");
                    if (httpServletRequest.getMethod().equals("GET")) {
                        if ("unread".equals(type)) {
                            final HashMap<String, Integer> response = Maps.newHashMap();
                            response.put("unread", conversationService.getUnreadMessagesCount(user));
                            final byte[] bytes = objectMapper.writeValueAsBytes(response);
                            write(httpServletResponse, bytes);
                        } else {
                            final Long oid = Long.parseLong(httpServletRequest.getParameter("oid"));
                            final Integer from = Integer.parseInt(defaultIfNull(httpServletRequest.getParameter("from"), "0"));
                            final Integer to = Integer.parseInt(defaultIfNull(httpServletRequest.getParameter("to"), "50"));
                            final UserEntity opponent = userService.findUser(oid);
                            if (opponent == null) {
                                httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                return;
                            }
                            final ConversationEntity conversation = conversationService.findConversation(user, opponent);
                            if (conversation == null) {
                                httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
                                return;
                            }
                            final ConversationDTO response = new ConversationDTO(conversationService.findAllComments(conversation, from, to), conversation, user);
                            final byte[] bytes = objectMapper.writeValueAsBytes(response);
                            write(httpServletResponse, bytes);
                            conversation.setLastSeenDate(new Date());
                            conversationService.save(conversation);
                        }
                    } else if (httpServletRequest.getMethod().equals("POST")) {
                        final Long oid = Long.parseLong(httpServletRequest.getParameter("oid"));
                        final String text = IOUtils.toString(httpServletRequest.getInputStream());
                        final UserEntity opponent = userService.findUser(oid);
                        if (opponent == null) {
                            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            return;
                        }
                        final ConversationEntity conversation = conversationService.findOrCreateConversation(user, opponent);
                        if ("text".equals(type)) {
                            final CommentEntity comment = new CommentEntity();
                            comment.setText(text);
                            conversationService.placeComment(conversation, comment, user);
                            final ConversationDTO response = new ConversationDTO(conversationService.findAllComments(conversation, 0, 50), conversation, user);
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

}