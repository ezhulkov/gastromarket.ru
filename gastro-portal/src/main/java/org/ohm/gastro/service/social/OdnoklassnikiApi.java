package org.ohm.gastro.service.social;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;

/**
 * Created by ezhulkov on 09.01.15.
 */
public class OdnoklassnikiApi extends DefaultApi20 {

    @Override
    public String getAccessTokenEndpoint() {
        return null;
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        return null;
    }
}
