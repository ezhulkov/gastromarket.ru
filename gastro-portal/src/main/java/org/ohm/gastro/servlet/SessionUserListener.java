package org.ohm.gastro.servlet;

import org.ohm.gastro.trait.Logging;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by ezhulkov on 22.11.15.
 */
public class SessionUserListener implements HttpSessionListener, Logging {

    @Override
    public void sessionCreated(final HttpSessionEvent se) {

    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
        logger.info("Session {} destroyed", se.getSession().getId());
        MessageNotifierServlet.removePeer(se.getSession().getId());
    }

}
