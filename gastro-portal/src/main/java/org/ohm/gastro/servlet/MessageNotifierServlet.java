package org.ohm.gastro.servlet;

import com.google.common.collect.Maps;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.UserService;
import org.ohm.gastro.service.impl.ApplicationContextHolder;
import org.ohm.gastro.trait.Logging;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Created by ezhulkov on 22.11.15.
 */
@SuppressWarnings("deprecation")
public class MessageNotifierServlet extends WebSocketServlet implements Logging {

    private static Map<String, Optional<MessageNotifierInbound>> peerList = Maps.newHashMap();
    private static Map<String, Optional<String>> indexList = Maps.newHashMap();

    @Override
    protected StreamInbound createWebSocketInbound(final String s, final HttpServletRequest httpServletRequest) {
        final UserService userService = ApplicationContextHolder.getBean(UserService.class);
        return new MessageNotifierInbound(httpServletRequest, userService);
    }

    public static void removePeer(String sessionId) {
        synchronized (MessageNotifierServlet.class) {
            peerList.remove(sessionId);
            final Iterator<Entry<String, Optional<String>>> it = indexList.entrySet().iterator();
            while (it.hasNext()) {
                final Entry<String, Optional<String>> entry = it.next();
                if (entry.getValue().get().equals(sessionId)) it.remove();
            }
        }
    }

    public static void sendUnreadCount(String email, int count) {
        indexList.getOrDefault(email, Optional.empty()).flatMap(t -> peerList.getOrDefault(t, Optional.empty())).ifPresent(peer -> {
            logger.info("Sending unread message count {} to peer {}", count, peer);
            peer.sendMessage(count);
        });
    }

    private class MessageNotifierInbound extends MessageInbound {

        private final String sessionId;
        private final Optional<UserEntity> userOpt;

        private WsOutbound peer;

        public MessageNotifierInbound(HttpServletRequest request, UserService userService) {
            this.sessionId = request.getSession().getId();
            this.userOpt = BaseComponent.getAuthenticatedUser(userService);
        }

        @Override
        public void onOpen(WsOutbound outbound) {
            userOpt.ifPresent(user -> {
                logger.info("Opening chat client for user {}", userOpt);
                this.peer = outbound;
                synchronized (MessageNotifierServlet.class) {
                    peerList.put(sessionId, Optional.of(this));
                    indexList.put(user.getEmail(), Optional.of(sessionId));
                }
            });
        }

        @Override
        protected void onBinaryMessage(final ByteBuffer message) throws IOException {
            logger.error("Accepting binary message - should not happen");
        }

        @Override
        protected void onTextMessage(final CharBuffer message) throws IOException {
            logger.error("Accepting text message - should not happen");
        }

        public void sendMessage(int count) {
            try {
                peer.writeTextMessage(CharBuffer.wrap(String.format("{\"unread\":\"%s\"}", count)));
            } catch (IOException e) {
                logger.error("", e);
            }
        }

    }

}
