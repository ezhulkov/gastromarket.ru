package org.ohm.gastro.filter;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.SocialSource;
import org.ohm.gastro.service.UserService;
import org.ohm.gastro.service.impl.ApplicationContextHolder;
import org.ohm.gastro.trait.Logging;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ezhulkov on 08.01.15.
 */
public class SocialFilter extends BaseApplicationFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String rqType = (String) ObjectUtils.defaultIfNull(httpServletRequest.getParameter("type"), "direct");
            String socialNetwork = httpServletRequest.getParameter("social");
            Logging.logger.info("Calling SocialFilter with parameters rqType: {}, socialNetwork {}", rqType, socialNetwork);

            SocialSource socialSource = ApplicationContextHolder.getBean(SocialSource.class, socialNetwork);
            OAuthService authService = socialSource.getAuthService();
            if ("direct".equals(rqType)) {
                String authUrl = authService.getAuthorizationUrl(null);
                httpServletResponse.sendRedirect(authUrl);
            } else if ("callback".equals(rqType)) {
                String oAuth2Code = httpServletRequest.getParameter("code");
                if (oAuth2Code == null) {
                    Logging.logger.error("Empty oAuth2Code from callback call");
                    httpServletResponse.sendRedirect("/");
                    return;
                }
                Verifier verifier = new Verifier(oAuth2Code);
                Token token = authService.getAccessToken(null, verifier);
                Logging.logger.info("Got access token {}", token);
                UserEntity userProfile = socialSource.getUserProfile(token);
                if (userProfile == null || StringUtils.isEmpty(userProfile.getEmail())) {
                    Logging.logger.error("Empty UserProfile from social source call");
                    httpServletResponse.sendRedirect("/");
                    return;
                }
                Logging.logger.info("UserProfile from social source {}", userProfile);
                ApplicationContextHolder.getBean(UserService.class).signupSocial(userProfile);
                httpServletResponse.sendRedirect("/");
            }
        } catch (Exception e) {
            Logging.logger.error("", e);
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

}
