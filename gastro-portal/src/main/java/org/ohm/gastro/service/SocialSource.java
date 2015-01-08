package org.ohm.gastro.service;

import org.ohm.gastro.domain.UserEntity;
import org.scribe.oauth.OAuthService;

/**
 * Created by ezhulkov on 08.01.15.
 */
public interface SocialSource {

    boolean isConnected(UserEntity socialInfo);

    UserEntity getUserProfile(String token, String uid);

    String getImageURL(String token, String uid);

    String getSmallImageURL(String token, String uid);

    OAuthService getAuthService();

}
