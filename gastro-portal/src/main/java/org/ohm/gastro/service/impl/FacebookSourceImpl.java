package org.ohm.gastro.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.MediaImportService;
import org.ohm.gastro.service.social.FacebookAlbumsResponse;
import org.ohm.gastro.service.social.FacebookImagesResponse;
import org.ohm.gastro.service.social.FacebookUserProfile;
import org.ohm.gastro.service.social.MediaAlbum;
import org.ohm.gastro.service.social.MediaElement;
import org.ohm.gastro.service.social.MediaResponse;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 08.01.15.
 */
@Component("fb")
public final class FacebookSourceImpl extends OAuthSocialSourceImpl<FacebookApi> implements MediaImportService {

    private final static String REST_AUTH_URL = "https://graph.facebook.com/me";
    private final static String REST_ALBUM_URL = "https://graph.facebook.com/v2.3/%s/albums?fields=id,name,count&limit=1000";
    private final static String REST_IMAGE_URL = "https://graph.facebook.com/v2.3/%s/photos?fields=images,link,name&limit=1000";
    private final static String AVATAR_SMALL = "https://graph.facebook.com/%s/picture";
    private final static String AVATAR_BIG = "https://graph.facebook.com/%s/picture?type=large";

    @Autowired
    public FacebookSourceImpl(@Value("${fb.api.key}") String apiKey,
                              @Value("${fb.api.secret}") String apiSecret,
                              @Value("${fb.api.scope}") String scope,
                              @Value("${fb.api.callback}") String callback) {
        super(apiKey, apiSecret, scope, callback, FacebookApi.class);
    }

    @Override
    public String getSocialSourceName() {
        return "Facebook";
    }

    @Override
    public UserEntity getUserProfile(Token token) {
        Response response = null;
        try {
            OAuthRequest request = new OAuthRequest(Verb.GET, REST_AUTH_URL);
            getAuthService().signRequest(token, request);
            response = request.send();
            FacebookUserProfile profile = mapper.readValue(response.getBody(), FacebookUserProfile.class);
            UserEntity user = new UserEntity();
            user.setFullName(profile.getName());
            user.setEmail(profile.getEmail());
            user.setAvatarUrl(String.format(AVATAR_BIG, profile.getId()));
            user.setAvatarUrlMedium(String.format(AVATAR_BIG, profile.getId()));
            user.setAvatarUrlSmall(String.format(AVATAR_SMALL, profile.getId()));
            return user;
        } catch (Exception e) {
            logger.error("Error parsing raw {}, response {}", token == null ? null : token.getRawResponse(), response == null ? null : response.getBody());
            logger.error("", e);
        }
        return null;
    }

    @Nonnull
    @Override
    public List<MediaAlbum> getAlbums(@Nonnull Token token) {
        Response response = null;
        try {
            OAuthRequest request = new OAuthRequest(Verb.GET, String.format(REST_ALBUM_URL, "me"));
            getAuthService().signRequest(token, request);
            response = request.send();
            final FacebookAlbumsResponse albums = mapper.readValue(response.getBody(), FacebookAlbumsResponse.class);
            return albums.getResponse().stream()
                    .map(t -> new MediaAlbum("", t.getName(), t.getId(), t.getCount()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error parsing response {}", response == null ? null : response.getBody());
            logger.error("", e);
        }
        return Lists.newArrayList();
    }

    @Nonnull
    @Override
    public MediaResponse getImages(@Nonnull Token token, @Nullable String albumId, @Nullable Object context) {
        if (StringUtils.isNotEmpty(albumId)) {
            Response response = null;
            try {
                OAuthRequest request = new OAuthRequest(Verb.GET, String.format(REST_IMAGE_URL, albumId));
                getAuthService().signRequest(token, request);
                response = request.send();
                final FacebookImagesResponse images = mapper.readValue(response.getBody(), FacebookImagesResponse.class);
                return new MediaResponse(null, images.getResponse().stream()
                        .map(t -> new MediaElement(t.getLink(), t.getImages().get(0).getSource(), t.getName()))
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                logger.error("Error parsing response {}", response == null ? null : response.getBody());
                logger.error("", e);
            }
        }
        return new MediaResponse(null, Lists.newArrayList());
    }

}
