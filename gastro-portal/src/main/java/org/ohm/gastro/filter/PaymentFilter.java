package org.ohm.gastro.filter;

import org.ohm.gastro.service.BillService;
import org.ohm.gastro.service.impl.ApplicationContextHolder;
import org.ohm.gastro.trait.Logging;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ezhulkov on 08.01.15.
 */
public class PaymentFilter extends BaseApplicationFilter implements Logging {

    private final static String CHECK_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n<checkOrderResponse performedDatetime=\"%s\" code=\"%s\" invoiceId=\"%s\" shopId=\"105591\"/>";
    private final static String AVISO_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n<paymentAvisoResponse performedDatetime =\"%s\" code=\"%s\" invoiceId=\"%s\" shopId=\"105591\"/>";
    private final static ThreadLocal<DateFormat> DATE_LONG = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000+03:00'");
        }
    };


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            if (httpServletRequest.getMethod().equals("POST")) {

                final BillService billService = ApplicationContextHolder.getApplicationContext().getBean(BillService.class);
                final String url = httpServletRequest.getRequestURI();
                final String response;
                httpServletRequest.getParameterMap().entrySet().forEach(t -> Logging.logger.info("{} - {}", t.getKey(), t.getValue()));
                if (url.equals("/payment/check")) {
                    Logging.logger.info("Checking payment request");
                    final boolean res = billService.checkPayment(
                            Long.parseLong(httpServletRequest.getParameter("orderNumber")),
                            Float.parseFloat(httpServletRequest.getParameter("orderSumAmount"))
                    );
                    response = String.format(CHECK_RESPONSE, DATE_LONG.get().format(new Date()), res ? "0" : "1", httpServletRequest.getParameter("invoiceId"));
                } else if (url.equals("/payment/success")) {
                    Logging.logger.info("Confirming successful payment");
                    final boolean res = billService.proceedPayment(Long.parseLong(httpServletRequest.getParameter("orderNumber")));
                    response = String.format(AVISO_RESPONSE, DATE_LONG.get().format(new Date()), res ? "0" : "1", httpServletRequest.getParameter("invoiceId"));
                } else {
                    httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                Logging.logger.info("Response {}", response);

                httpServletResponse.setContentType("application/xml");
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setHeader("Cache-Control", "no-cache");
                httpServletResponse.getWriter().write(response);
                httpServletResponse.getWriter().flush();

            }
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            Logging.logger.error("", e);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

}
