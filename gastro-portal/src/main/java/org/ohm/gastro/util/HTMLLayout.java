package org.ohm.gastro.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.pattern.Converter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static ch.qos.logback.core.CoreConstants.LINE_SEPARATOR;

/**
 * Created by ezhulkov on 20.03.15.
 */
public class HTMLLayout extends ch.qos.logback.classic.html.HTMLLayout {

    private static final String HOST_NAME;

    static {
        String hostName = "unknown";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } finally {
            HOST_NAME = hostName;
        }
    }

    public HTMLLayout() {
        super();
        title = title + " From " + HOST_NAME;
        setThrowableRenderer(new DefaultThrowableRenderer());
    }

    @Override
    public String getFileHeader() {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"" +
                " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
                "<html>" +
                "  <head>" +
                "    <title>" + title + "</title>" +
                "  </head>" +
                "<body>";
    }

    @Override
    public String getPresentationHeader() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("<h3>Log session start time: ").append(new java.util.Date()).append("</h3>");
        sbuf.append("<h3>Hostname: ").append(HOST_NAME).append("</h3>");
        sbuf.append("<table cellspacing=\"0\" style=\"font-family: 'Courier New', Monospace, serif;width: 100%;font-size: 12px;border: 1px solid #000;\">");
        buildHeaderRowForTable(sbuf);
        return sbuf.toString();
    }

    protected void startNewTableIfLimitReached(StringBuilder sbuf) {
        if (this.counter >= CoreConstants.TABLE_ROW_LIMIT) {
            counter = 0;
            sbuf.append("</table>");
            sbuf.append("<br/>");
            sbuf.append("<table cellspacing=\"0\" style=\"font-family: 'Courier New', Monospace, serif;width: 100%;font-size: 12px;border: 1px solid #000;\">");
            buildHeaderRowForTable(sbuf);
        }
    }

    @Override
    public String getPresentationFooter() {
        return "</table>";
    }

    public String doLayout(ILoggingEvent event) {
        final StringBuilder buf = new StringBuilder();
        startNewTableIfLimitReached(buf);
        boolean odd = true;
        if (((counter++) & 1) == 0) {
            odd = false;
        }
        buf.append("<tr ");
        if (odd) {
            buf.append("style=\"background: #fff;\"");
        } else {
            buf.append("style=\"background: #eee;\"");
        }
        buf.append("/>");

        Converter<ILoggingEvent> c = head;
        while (c != null) {
            appendEventToBuffer(buf, c, event);
            c = c.getNext();
        }
        buf.append("</tr>");
        if (event.getThrowableProxy() != null) {
            getThrowableRenderer().render(buf, event);
        }
        return buf.toString();
    }

    private void appendEventToBuffer(StringBuilder buf,
                                     Converter<ILoggingEvent> c, ILoggingEvent event) {
        buf.append("<td>");
        c.write(buf, event);
        buf.append("</td>");
        buf.append(LINE_SEPARATOR);
    }

    private void buildHeaderRowForTable(StringBuilder sbuf) {
        Converter c = head;
        String name;
        sbuf.append("<tr style=\"background: #596ed5;color:#fff;font-weight: bold;font-size: 14px;\">");
        while (c != null) {
            name = computeConverterName(c);
            if (name == null) {
                c = c.getNext();
                continue;
            }
            sbuf.append("<td>");
            sbuf.append(computeConverterName(c));
            sbuf.append("</td>");
            c = c.getNext();
        }
        sbuf.append("</tr>");
    }

    @Override
    public String getFileFooter() {
        return "</body></html>";
    }

    private static class DefaultThrowableRenderer extends ch.qos.logback.classic.html.DefaultThrowableRenderer {
        private static final String TRACE_PREFIX = "<br />&nbsp;&nbsp;&nbsp;&nbsp;";

        @Override
        public void render(StringBuilder sbuf, ILoggingEvent event) {
            IThrowableProxy tp = event.getThrowableProxy();
            sbuf.append("<tr><td style=\"background: #a2aee8;\" colspan=\"6\">");
            while (tp != null) {
                render(sbuf, tp);
                tp = tp.getCause();
            }
            sbuf.append("</td></tr>");
        }

        void render(StringBuilder sbuf, IThrowableProxy tp) {
            printFirstLine(sbuf, tp);

            int commonFrames = tp.getCommonFrames();
            StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();

            for (int i = 0; i < stepArray.length - commonFrames; i++) {
                StackTraceElementProxy step = stepArray[i];
                sbuf.append(TRACE_PREFIX);
                sbuf.append(Transform.escapeTags(step.toString()));
                sbuf.append(CoreConstants.LINE_SEPARATOR);
            }

            if (commonFrames > 0) {
                sbuf.append(TRACE_PREFIX);
                sbuf.append("\t... ").append(commonFrames).append(" common frames omitted")
                        .append(CoreConstants.LINE_SEPARATOR);
            }
        }
    }

}
