package org.ohm.gastro.service;

import org.ohm.gastro.domain.UserEntity;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

/**
 * Created by ezhulkov on 08.01.15.
 */
public interface SocialSource {

    String getSocialSourceName();

    UserEntity getUserProfile(Token token);

    OAuthService getAuthService();

}
