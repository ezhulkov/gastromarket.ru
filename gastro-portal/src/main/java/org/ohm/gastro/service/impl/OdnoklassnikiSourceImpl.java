package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.UserEntity;
import org.scribe.builder.api.FacebookApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by ezhulkov on 08.01.15.
 */
@Component("ok")
public final class OdnoklassnikiSourceImpl extends OAuthSocialSourceImpl<FacebookApi> {

    @Autowired
    public OdnoklassnikiSourceImpl(@Value("${ok.api.key}") String apiKey,
                                   @Value("${ok.api.secret}") String apiSecret,
                                   @Value("${ok.api.scope}") String scope,
                                   @Value("${ok.api.callback}") String callback) {
        super(apiKey, apiSecret, scope, callback, FacebookApi.class);
    }

    @Override
    public boolean isConnected(UserEntity socialInfo) {
        return false;
    }

    @Override
    public UserEntity getUserProfile(String token, String uid) {
        return null;
    }

    @Override
    public String getImageURL(String token, String uid) {
        return null;
    }

    @Override
    public String getSmallImageURL(String token, String uid) {
        return null;
    }

}
