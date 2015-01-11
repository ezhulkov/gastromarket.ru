package org.ohm.gastro.service.social;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;

/**
 * Created by ezhulkov on 09.01.15.
 */
public class OdnoklassnikiApi extends DefaultApi20 {

    private static final String AUTHORIZE_URL = "http://www.odnoklassniki.ru/oauth/authorize?client_id=%s&scope=%s&response_type=code&redirect_uri=%s";

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.odnoklassniki.ru/oauth/token.do";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getScope()), OAuthEncoder.encode(config.getCallback()));
    }

}
