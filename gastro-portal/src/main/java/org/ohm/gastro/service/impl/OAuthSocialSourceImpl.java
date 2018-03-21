package org.ohm.gastro.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.ohm.gastro.misc.EndpointCallback;
import org.ohm.gastro.service.SocialSource;
import org.ohm.gastro.trait.Logging;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

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
        final ServiceBuilder builder = new ServiceBuilder().provider(authServiceClass).apiKey(apiKey).apiSecret(apiSecret).callback(callback);
        if (StringUtils.isNotEmpty(scope)) builder.scope(scope);
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    protected <T> T callEndpoint(@Nonnull final String url, @Nonnull final Token token, @Nonnull EndpointCallback<String, T> onSuccess) {
        return callEndpoint(url, token, onSuccess, () -> (T) Lists.newArrayList());
    }

    protected <T> T callEndpoint(@Nonnull final String url, @Nonnull final Token token, @Nonnull EndpointCallback<String, T> onSuccess, @Nonnull Supplier<T> onError) {
        Response response = null;
        try {
            logger.info("Getting endpoint url: {}", url);
            final OAuthRequest request = new OAuthRequest(Verb.GET, url);
            getAuthService().signRequest(token, request);
            response = request.send();
            final String body = response.getBody();
            return body == null ? null : onSuccess.applyThrowing(body);
        } catch (Exception e) {
            logger.error("Error parsing response {}", response == null ? null : response.getBody());
            logger.error("", e);
        }
        return onError.get();
    }

}
