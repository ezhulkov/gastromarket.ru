package org.ohm.gastro.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.MediaImportService;
import org.ohm.gastro.service.social.MediaAlbum;
import org.ohm.gastro.service.social.MediaElement;
import org.ohm.gastro.service.social.MediaResponse;
import org.ohm.gastro.service.social.VKontakteUserProfile;
import org.ohm.gastro.service.social.VKontakteUserProfileResponse;
import org.ohm.gastro.service.social.VkontakteAlbumsResponse;
import org.ohm.gastro.service.social.VkontakteImagesResponse;
import org.scribe.builder.api.VkontakteApi;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ezhulkov on 08.01.15.
 */
@Component("vk")
public final class VkontakteSourceImpl extends OAuthSocialSourceImpl<VkontakteApi> implements MediaImportService {

    private final static String REST_AUTH_URL = "https://api.vk.com/method/users.get?uids=%s&fields=uid,first_name,last_name,photo,photo_big";
    private final static String REST_ALBUM_URL = "https://api.vk.com/method/photos.getAlbums?owner_id=%s&need_covers=1";
    private final static String REST_IMAGE_URL = "https://api.vk.com/method/photos.get?owner_id=%s&album_id=%s";

    @Autowired
    public VkontakteSourceImpl(@Value("${vk.api.key}") String apiKey,
                               @Value("${vk.api.secret}") String apiSecret,
                               @Value("${vk.api.scope}") String scope,
                               @Value("${vk.api.callback}") String callback) {
        super(apiKey, apiSecret, scope, callback, VkontakteApi.class);
    }

    @Override
    public String getSocialSourceName() {
        return "Вконтакте";
    }

    @Override
    public UserEntity getUserProfile(Token token) {
        Response response = null;
        try {
            Map map = mapper.readValue(token.getRawResponse(), Map.class);
            OAuthRequest request = new OAuthRequest(Verb.GET, String.format(REST_AUTH_URL, map.get("user_id").toString()));
            getAuthService().signRequest(token, request);
            response = request.send();
            VKontakteUserProfile profile = mapper.readValue(response.getBody(), VKontakteUserProfileResponse.class).getResponse().get(0);
            UserEntity user = new UserEntity();
            user.setFullName(profile.getFirstName() + " " + profile.getLastName());
            user.setEmail(map.get("email").toString());
            user.setAvatarUrl(profile.getPhotoBig());
            user.setAvatarUrlMedium(profile.getPhotoBig());
            user.setAvatarUrlSmall(profile.getPhoto());
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
            Map map = mapper.readValue(token.getRawResponse(), Map.class);
            OAuthRequest request = new OAuthRequest(Verb.GET, String.format(REST_ALBUM_URL, map.get("user_id").toString()));
            response = request.send();
            final VkontakteAlbumsResponse albums = mapper.readValue(response.getBody(), VkontakteAlbumsResponse.class);
            return albums.getResponse().stream()
                    .map(t -> new MediaAlbum(t.getThumbSrc(), t.getTitle(), t.getId(), t.getSize()))
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
                Map map = mapper.readValue(token.getRawResponse(), Map.class);
                OAuthRequest request = new OAuthRequest(Verb.GET, String.format(REST_IMAGE_URL, map.get("user_id").toString(), albumId));
                response = request.send();
                final VkontakteImagesResponse images = mapper.readValue(response.getBody(), VkontakteImagesResponse.class);
                return new MediaResponse(null, images.getResponse().stream()
                        .map(t -> new MediaElement(t.getPid(), t.getLink(), t.getText(), t.getImageUrl(), t.getImageUrlSmall()))
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                logger.error("Error parsing response {}", response == null ? null : response.getBody());
                logger.error("", e);
            }
        }
        return new MediaResponse(null, Lists.newArrayList());
    }

}
