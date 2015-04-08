package org.ohm.gastro.service.impl;

import com.google.common.collect.Lists;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.MediaImportService;
import org.ohm.gastro.service.social.MediaAlbum;
import org.ohm.gastro.service.social.MediaResponse;
import org.ohm.gastro.service.social.VKontakteUserProfile;
import org.ohm.gastro.service.social.VKontakteUserProfileResponse;
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

/**
 * Created by ezhulkov on 08.01.15.
 */
@Component("vk")
public final class VkontakteSourceImpl extends OAuthSocialSourceImpl<VkontakteApi> implements MediaImportService {

    private final static String REST_API_URL = "https://api.vk.com/method/users.get?uids=%s&fields=uid,first_name,last_name,photo,photo_big";

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
            OAuthRequest request = new OAuthRequest(Verb.GET, String.format(REST_API_URL, map.get("user_id").toString()));
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
        return Lists.newArrayList();
    }

    @Nonnull
    @Override
    public MediaResponse getElements(@Nonnull Token token, @Nullable Object context) {
        return new MediaResponse(null, null);
    }

}
