package org.ohm.gastro.service.social;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth20ServiceImpl;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/**
 * Created by ezhulkov on 05.04.15.
 */
public class InstagramApi extends DefaultApi20 {

    private static final String AUTHORIZE_URL = "https://api.instagram.com/oauth/authorize/?client_id=%s&redirect_uri=%s&scope=%s&response_type=code";

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.instagram.com/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        Preconditions.checkValidUrl(config.getCallback(), "Valid url is required for a callback. Instagram does not support OOB");
        return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(config.getScope()));
    }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new JsonTokenExtractor();
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public OAuthService createService(final OAuthConfig config) {
        return new OAuth20ServiceImpl(this, config) {
            public Token getAccessToken(Token requestToken, Verifier verifier) {
                OAuthRequest request = new OAuthRequest(getAccessTokenVerb(),
                                                        getAccessTokenEndpoint());
                request.addBodyParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
                request.addBodyParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
                request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
                request.addBodyParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
                request.addBodyParameter("grant_type", "authorization_code");
                request.addBodyParameter(OAuthConstants.SCOPE, config.getScope());
                Response response = request.send();
                return getAccessTokenExtractor().extract(response.getBody());
            }
        };
    }
}