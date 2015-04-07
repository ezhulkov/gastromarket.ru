package org.ohm.gastro.service.impl;

import com.google.common.collect.Lists;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.MediaImportService;
import org.ohm.gastro.service.social.InstagramApi;
import org.ohm.gastro.service.social.InstagramMediaResponse;
import org.ohm.gastro.service.social.MediaAlbum;
import org.ohm.gastro.service.social.MediaElement;
import org.ohm.gastro.service.social.MediaResponse;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 08.01.15.
 */
@Component("in")
public final class InstagramSourceImpl extends OAuthSocialSourceImpl<InstagramApi> implements MediaImportService {

    private final static String USER_ENDPOINT = "https://api.instagram.com/v1/users/%s";
    private final static String MEDIA_ENDPOINT = "https://api.instagram.com/v1/users/%s/media/recent";

    @Autowired
    public InstagramSourceImpl(@Value("${in.api.key}") String apiKey,
                               @Value("${in.api.secret}") String apiSecret,
                               @Value("${in.api.scope}") String scope,
                               @Value("${in.api.callback}") String callback) {
        super(apiKey, apiSecret, scope, callback, InstagramApi.class);
    }

    @Override
    public String getSocialSourceName() {
        return "Instagram";
    }

    @Override
    public UserEntity getUserProfile(Token token) {
        Response response = null;
        try {
            final OAuthRequest request = new OAuthRequest(Verb.GET, String.format(USER_ENDPOINT, extractUserId(token)));
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
    public List<MediaAlbum> getAlbums(Token token) {
        return Lists.newArrayList();
    }

    @Override
    public MediaResponse getElements(Token token) {
        Response response = null;
        try {
            final OAuthRequest request = new OAuthRequest(Verb.GET, String.format(MEDIA_ENDPOINT, extractUserId(token)));
            getAuthService().signRequest(token, request);
            response = request.send();
            final InstagramMediaResponse medias = mapper.readValue(response.getBody(), InstagramMediaResponse.class);
            final MediaResponse mediaResponse = new MediaResponse(medias.getPagination().getNextUrl(),
                                                                  medias.getData().stream()
                                                                          .filter(t -> "image".equals(t.getType()))
                                                                          .map(t -> new MediaElement(t.getLink(),
                                                                                                     t.getImages().getStandardResolution().getUrl(),
                                                                                                     t.getCaption() == null ? "" : t.getCaption().getText()))
                                                                          .collect(Collectors.toList()));
            logger.debug("Instagram parsed response {}", mediaResponse);
            return mediaResponse;
        } catch (Exception e) {
            logger.error("Error parsing response {}", response == null ? null : response.getBody());
            logger.error("", e);
        }
        return null;
    }

    private String extractUserId(Token token) throws IOException {
        final Map map = mapper.readValue(token.getRawResponse(), Map.class);
        return ((Map) map.get("user")).get("id").toString();
    }

}
