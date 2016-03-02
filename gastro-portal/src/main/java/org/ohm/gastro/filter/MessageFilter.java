package org.ohm.gastro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.ohm.gastro.domain.CommentEntity;
import org.ohm.gastro.domain.ConversationEntity;
import org.ohm.gastro.service.ConversationService;
import org.ohm.gastro.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by ezhulkov on 23.08.14.
 */
@Component
public class MessageFilter extends BaseApplicationFilter {

    private UserService userService;
    private ConversationService conversationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public UserService getUserService() {
        return userService;
    }

    @Autowired
    public ConversationService getConversationService() {
        return conversationService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
                                    final HttpServletResponse httpServletResponse,
                                    final FilterChain filterChain) throws ServletException, IOException {
        final boolean needToLog = !isStaticResource(httpServletRequest);
        if (needToLog) {
            if (httpServletRequest.getMethod().equals("GET")) {
                final Long cid = Long.parseLong(httpServletRequest.getParameter("cid"));
                final Integer from = Integer.parseInt(ObjectUtils.defaultIfNull(httpServletRequest.getParameter("from"), "0"));
                final Integer to = Integer.parseInt(ObjectUtils.defaultIfNull(httpServletRequest.getParameter("to"), "50"));
                final ConversationEntity conversation = conversationService.find(cid);
                if (conversation == null) {
                    httpServletResponse.setStatus(404);
                    return;
                }
                final Converstation model = new Converstation(conversationService.findAllComments(conversation, from, to), conversation);
                try (
                        final OutputStream os = httpServletResponse.getOutputStream();
                ) {
                    os.write(objectMapper.writeValueAsBytes(model));
                    os.flush();
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private class Converstation {

        private final List<CommentEntity> messages;
        private final ConversationEntity conversation;

        public Converstation(List<CommentEntity> messages, ConversationEntity origin) {
            this.messages = messages;
            this.conversation = new ConversationEntity();
            BeanUtils.copyProperties(origin, conversation);
        }

        public List<CommentEntity> getMessages() {
            return messages;
        }

        public ConversationEntity getConversation() {
            return conversation;
        }
    }

}