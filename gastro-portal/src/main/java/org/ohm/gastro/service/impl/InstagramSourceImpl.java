package org.ohm.gastro.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.misc.Throwables;
import org.ohm.gastro.service.MediaImportService;
import org.ohm.gastro.service.social.InstagramApi;
import org.ohm.gastro.service.social.InstagramMediaResponse;
import org.ohm.gastro.service.social.MediaAlbum;
import org.ohm.gastro.service.social.MediaElement;
import org.ohm.gastro.service.social.MediaResponse;
import org.scribe.model.Token;
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
@Component("in")
public final class InstagramSourceImpl extends OAuthSocialSourceImpl<InstagramApi> implements MediaImportService {

    private final static String REST_AUTH_URL = "https://api.instagram.com/v1/users/%s";
    private final static String REST_IMAGES_URL = "https://api.instagram.com/v1/users/%s/media/recent";

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
        return callEndpoint(String.format(REST_AUTH_URL, extractUserId(token)),
                            token,
                            body -> {
                                final Map userMap = mapper.readValue(body, Map.class);
                                UserEntity user = new UserEntity();
                                user.setFullName(((Map) userMap.get("data")).get("username").toString());
                                return user;
                            },
                            () -> null);
    }

    @Override
    public boolean isAlbumsRequired() {
        return false;
    }

    @Nonnull
    @Override
    public List<MediaAlbum> getAlbums(@Nonnull Token token) {
        return Lists.newArrayList();
    }

    @Nonnull
    @Override
    public MediaResponse getImages(@Nonnull Token token, @Nullable String albumId, @Nullable Object context) {
        final String paging = context == null ? null : context.toString();
        final InstagramMediaResponse medias = callEndpoint(StringUtils.isEmpty(paging) ? String.format(REST_IMAGES_URL, extractUserId(token)) : paging,
                                                           token,
                                                           body -> mapper.readValue(body, InstagramMediaResponse.class),
                                                           () -> null);
        return new MediaResponse(medias.getPagination().getNextUrl(),
                                 medias.getData().stream()
                                         .filter(t -> "image".equals(t.getType()))
                                         .map(t -> new MediaElement(t.getId(), t.getLink(),
                                                                    t.getCaption() == null ? "" : t.getCaption().getText(),
                                                                    t.getImages().getStandardResolution().getUrl(),
                                                                    t.getImages().getLowResolution().getUrl()))
                                         .collect(Collectors.toList()));
    }

    private String extractUserId(Token token) {
        final Map map = Throwables.propagate(() -> mapper.readValue(token.getRawResponse(), Map.class));
        return ((Map) map.get("user")).get("id").toString();
    }

}
