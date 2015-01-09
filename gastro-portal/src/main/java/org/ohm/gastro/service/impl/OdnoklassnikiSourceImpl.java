package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.social.OdnoklassnikiApi;
import org.scribe.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by ezhulkov on 08.01.15.
 */
@Component("ok")
public final class OdnoklassnikiSourceImpl extends OAuthSocialSourceImpl<OdnoklassnikiApi> {

    @Autowired
    public OdnoklassnikiSourceImpl(@Value("${ok.api.key}") String apiKey,
                                   @Value("${ok.api.secret}") String apiSecret,
                                   @Value("${ok.api.scope}") String scope,
                                   @Value("${ok.api.callback}") String callback) {
        super(apiKey, apiSecret, scope, callback, OdnoklassnikiApi.class);
    }

    @Override
    public UserEntity getUserProfile(Token token) {
        return null;
    }

}
