package org.ohm.gastro.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ohm.gastro.service.SocialSource;
import org.ohm.gastro.trait.Logging;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.oauth.OAuthService;

/**
 * Created by ezhulkov on 08.01.15.
 */
public abstract class OAuthSocialSourceImpl<ST extends Api> implements SocialSource, Logging {

    protected final static ObjectMapper mapper = new ObjectMapper();

    private final String apiKey;
    private final String apiSecret;
    private final String scope;
    private final String callback;
    private final Class<ST> authServiceClass;

    public OAuthSocialSourceImpl(String apiKey, String apiSecret, String scope, String callback, Class<ST> authServiceClass) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.scope = scope;
        this.callback = callback;
        this.authServiceClass = authServiceClass;
    }

    @Override
    public OAuthService getAuthService() {
        ServiceBuilder builder = new ServiceBuilder();
        builder.provider(authServiceClass).apiKey(apiKey).apiSecret(apiSecret);
        if (callback != null) {
            builder.callback(callback);
        }
        if (scope != null) {
            builder.scope(scope);
        }
        return builder.build();
    }

}
