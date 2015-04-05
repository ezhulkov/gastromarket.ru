package org.ohm.gastro.service.impl;

import com.beust.jcommander.internal.Lists;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.MediaImportService;
import org.ohm.gastro.service.social.InstagramApi;
import org.ohm.gastro.service.social.MediaAlbum;
import org.ohm.gastro.service.social.MediaElement;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by ezhulkov on 08.01.15.
 */
@Component("in")
public final class InstagramSourceImpl extends OAuthSocialSourceImpl<InstagramApi> implements MediaImportService {

    private final static String USER_ENDPOINT = "https://api.instagram.com/v1/users/%s";
    private final static String MEDIA_ENDPOINT = "https://api.instagram.com/v1/users/%s/media/recent/?access_token=%s";

    @Autowired
    public InstagramSourceImpl(@Value("${in.api.key}") String apiKey,
                               @Value("${in.api.secret}") String apiSecret,
                               @Value("${in.api.scope}") String scope,
                               @Value("${in.api.callback}") String callback) {
        super(apiKey, apiSecret, scope, callback, InstagramApi.class);
    }

    @Override
    public UserEntity getUserProfile(Token token) {
        Response response = null;
        try {
            final Map map = mapper.readValue(token.getRawResponse(), Map.class);
            final OAuthRequest request = new OAuthRequest(Verb.GET, String.format(USER_ENDPOINT, ((Map) map.get("user")).get("id").toString()));
            getAuthService().signRequest(token, request);
            response = request.send();
            final Map userMap = mapper.readValue(response.getBody(), Map.class);
            UserEntity user = new UserEntity();
            user.setFullName(((Map) userMap.get("data")).get("username").toString());
            return user;
        } catch (Exception e) {
            logger.error("Error parsing raw {}, response {}", token == null ? null : token.getRawResponse(), response == null ? null : response.getBody());
            logger.error("", e);
        }
        return null;
    }


    @Override
    public List<MediaAlbum> getAlbums() {
        return Lists.newArrayList(new MediaAlbum("Instagram", ""));
    }

    @Override
    public List<MediaElement> getElements() {
        return null;
    }

}
