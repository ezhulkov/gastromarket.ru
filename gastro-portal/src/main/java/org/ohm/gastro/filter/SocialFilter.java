package org.ohm.gastro.filter;

import com.google.common.collect.Maps;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.gui.mixins.BaseComponent;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ezhulkov on 08.01.15.
 */
public class SocialFilter extends BaseApplicationFilter {

    public final static String TOKENS = "TOKENS";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final String rqType = (String) ObjectUtils.defaultIfNull(httpServletRequest.getParameter("type"), "direct");
            final String socialNetwork = httpServletRequest.getParameter("social");
            Logging.logger.debug("Calling SocialFilter with parameters rqType: {}, socialNetwork {}", rqType, socialNetwork);

            final SocialSource socialSource = ApplicationContextHolder.getBean(SocialSource.class, socialNetwork);
            final OAuthService authService = socialSource.getAuthService();
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
                if (userProfile == null) {
                    Logging.logger.error("Empty UserProfile from social source call");
                    httpServletResponse.sendRedirect("/");
                    return;
                }
                Logging.logger.info("UserProfile from social source {}", userProfile);
                final UserService userService = ApplicationContextHolder.getBean(UserService.class);
                if (!BaseComponent.getAuthenticatedUser(userService).isPresent()) {
                    if (StringUtils.isEmpty(userProfile.getEmail())) {
                        Logging.logger.error("Cannot login - empty email from social source");
                        httpServletResponse.sendRedirect("/");
                        return;
                    }
                    userService.signupSocial(userProfile);
                }
                final HttpSession session = httpServletRequest.getSession();
                if (session != null) {
                    Map<String, Token> tokens = (Map<String, Token>) session.getAttribute(TOKENS);
                    if (tokens == null) {
                        tokens = Maps.newHashMap();
                        session.setAttribute(TOKENS, tokens);
                    }
                    tokens.put(socialNetwork, token);
                }
                httpServletResponse.sendRedirect((String) ObjectUtils.defaultIfNull(httpServletRequest.getHeader("referer"), ""));
            }
        } catch (Exception e) {
            Logging.logger.error("", e);
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }


}
