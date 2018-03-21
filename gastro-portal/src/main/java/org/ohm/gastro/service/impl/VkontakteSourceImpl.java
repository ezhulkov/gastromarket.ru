package org.ohm.gastro.service.impl;

import com.google.common.collect.Lists;
import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.misc.Throwables;
import org.ohm.gastro.service.MediaImportService;
import org.ohm.gastro.service.social.MediaAlbum;
import org.ohm.gastro.service.social.MediaElement;
import org.ohm.gastro.service.social.MediaResponse;
import org.ohm.gastro.service.social.VKontakteUserProfile;
import org.ohm.gastro.service.social.VKontakteUserProfileResponse;
import org.ohm.gastro.service.social.VkontakteAlbumsResponse;
import org.ohm.gastro.service.social.VkontakteAlbumsResponse.Response;
import org.ohm.gastro.service.social.VkontakteGroupsResponse;
import org.ohm.gastro.service.social.VkontakteImagesResponse;
import org.scribe.builder.api.VkontakteApi;
import org.scribe.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ezhulkov on 08.01.15.
 */
@Component("vk")
public final class VkontakteSourceImpl extends OAuthSocialSourceImpl<VkontakteApi> implements MediaImportService {

    private final static String REST_AUTH_URL = "https://api.vk.com/method/users.get?uids=%s&fields=uid,first_name,last_name,photo,photo_big";
    private final static String REST_ACCOUNTS_URL = "https://api.vk.com/method/groups.get?v=5.29&uid=%s&extended=true&count=1000";
    private final static String REST_ALBUMS_URL = "https://api.vk.com/method/photos.getAlbums?v=5.29&owner_id=%s";
    private final static String REST_IMAGES_URL = "https://api.vk.com/method/photos.get?v=5.29&owner_id=%s&album_id=%s";

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
        final Map map = Throwables.propagate(() -> mapper.readValue(token.getRawResponse(), Map.class));
        if (map.get("email") == null) return null;
        return callEndpoint(String.format(REST_AUTH_URL, map.get("user_id").toString()),
                            token,
                            body -> {
                                final VKontakteUserProfile profile = mapper.readValue(body, VKontakteUserProfileResponse.class).getResponse().get(0);
                                UserEntity user = new UserEntity();
                                user.setFullName(profile.getFirstName() + " " + profile.getLastName());
                                user.setEmail(map.get("email").toString());
                                user.setAvatarUrl(profile.getPhotoBig());
                                user.setAvatarUrlMedium(profile.getPhotoBig());
                                user.setAvatarUrlSmall(profile.getPhoto());
                                return user;
                            },
                            () -> null);
    }

    @Override
    public boolean isAlbumsRequired() {
        return true;
    }

    @Nonnull
    @Override
    public List<MediaAlbum> getAlbums(@Nonnull Token token) {
        return new ForkJoinPool(4).invoke(new UserAlbumGetterTask(token));
    }

    @Nonnull
    @Override
    public MediaResponse getImages(@Nonnull Token token, @Nullable String albumId, @Nullable Object context) {
        if (albumId == null) return new MediaResponse();
        final String[] ids = albumId.split("_");
        return new MediaResponse(null, callEndpoint(String.format(REST_IMAGES_URL, ids[0], ids[1]),
                                                    token,
                                                    body -> mapper.readValue(body, VkontakteImagesResponse.class).getResponse().getItems().stream()
                                                            .map(t -> new MediaElement(t.getId(), t.getLink(), t.getText(), t.getImageUrl(), t.getImageUrl()))
                                                            .peek(t -> logger.info("Image from fb {}", t))
                                                            .collect(Collectors.toList())
        ));
    }

    private class UserAlbumGetterTask extends RecursiveTask<List<MediaAlbum>> {
        private final Token token;
        private final Map map;

        public UserAlbumGetterTask(final Token token) {
            this.token = token;
            this.map = Throwables.propagate(() -> mapper.readValue(token.getRawResponse(), Map.class));
        }

        @Override
        protected List<MediaAlbum> compute() {
            final ForkJoinTask<List<MediaAlbum>> mainAlbums = new AlbumGetterTask(String.format(REST_ALBUMS_URL, map.get("user_id").toString()),
                                                                                  map.get("user_id").toString(), MediaAlbum.DEFAULT_PAGE_NAME,
                                                                                  token).fork();
            final ForkJoinTask<List<MediaAlbum>> accountAlbums = new RecursiveTask<List<MediaAlbum>>() {
                @Override
                protected List<MediaAlbum> compute() {
                    return callEndpoint(String.format(REST_ACCOUNTS_URL, map.get("user_id").toString()),
                                        token,
                                        body -> mapper.readValue(body, VkontakteGroupsResponse.class).getResponse().getItems().stream()
                                                .peek(t -> logger.info("Account from vk {}", t))
                                                .map(t -> new AlbumGetterTask(String.format(REST_ALBUMS_URL, "-" + t.getId()), "-" + t.getId(), t.getName(), token).fork())
                                                .flatMap(t -> t.join().stream())
                                                .collect(Collectors.toList())
                    );
                }
            }.fork();
            return Stream.of(mainAlbums.join(), accountAlbums.join())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }

    }

    private class AlbumGetterTask extends RecursiveTask<List<MediaAlbum>> {

        private final String url;
        private final String pageName;
        private final String pageId;
        private final Token token;

        public AlbumGetterTask(final String url, final String pageId, final String pageName, final Token token) {
            this.url = url;
            this.pageName = pageName;
            this.pageId = pageId;
            this.token = token;
        }

        @Override
        protected List<MediaAlbum> compute() {
            return callEndpoint(url,
                                token,
                                body -> mapper.readValue(body, VkontakteAlbumsResponse.class)
                                        .getResponseOpt()
                                        .map(Response::getItems)
                                        .map(items -> items.stream()
                                                .map(t -> new MediaAlbum(pageId + "_" + t.getId(), t.getTitle(), pageName))
                                                .peek(t -> logger.info("Album from vk {}", t))
                                                .collect(Collectors.toList()))
                                        .orElse(Lists.newArrayList())
            );
        }
    }

}
