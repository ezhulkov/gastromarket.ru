package org.ohm.gastro.service.impl;

import org.ohm.gastro.domain.UserEntity;
import org.ohm.gastro.service.MediaImportService;
import org.ohm.gastro.service.social.FacebookAccountResponse;
import org.ohm.gastro.service.social.FacebookAlbumsResponse;
import org.ohm.gastro.service.social.FacebookImagesResponse;
import org.ohm.gastro.service.social.FacebookUserProfile;
import org.ohm.gastro.service.social.MediaAlbum;
import org.ohm.gastro.service.social.MediaElement;
import org.ohm.gastro.service.social.MediaResponse;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ezhulkov on 08.01.15.
 */
@Component("fb")
public final class FacebookSourceImpl extends OAuthSocialSourceImpl<FacebookApi> implements MediaImportService {

    private final static String REST_AUTH_URL = "https://graph.facebook.com/me";
    private final static String REST_ACCOUNTS_URL = "https://graph.facebook.com/v2.4/me/accounts?fields=name,id";
    private final static String REST_ALBUMS_URL = "https://graph.facebook.com/v2.4/%s/albums?fields=id,name,count&limit=1000";
    private final static String REST_IMAGES_URL = "https://graph.facebook.com/v2.4/%s/photos?fields=images,link,name&limit=1000";
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
        return callEndpoint(REST_AUTH_URL,
                            token,
                            body -> {
                                FacebookUserProfile profile = mapper.readValue(body, FacebookUserProfile.class);
                                UserEntity user = new UserEntity();
                                user.setFullName(profile.getName());
                                user.setEmail(profile.getEmail());
                                user.setAvatarUrl(String.format(AVATAR_BIG, profile.getId()));
                                user.setAvatarUrlMedium(String.format(AVATAR_BIG, profile.getId()));
                                user.setAvatarUrlSmall(String.format(AVATAR_SMALL, profile.getId()));
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
        return new MediaResponse(null, callEndpoint(String.format(REST_IMAGES_URL, albumId),
                                                    token,
                                                    body -> mapper.readValue(body, FacebookImagesResponse.class).getResponse().stream()
                                                            .map(t -> new MediaElement(t.getId(), t.getLink(), t.getName(),
                                                                                       t.getImages().get(0).getSource(),
                                                                                       t.getImages().get(t.getImages().size() - 1).getSource()))
                                                            .peek(t -> logger.info("Image from fb {}", t))
                                                            .collect(Collectors.toList())
        ));
    }

    private class UserAlbumGetterTask extends RecursiveTask<List<MediaAlbum>> {
        private final Token token;

        public UserAlbumGetterTask(final Token token) {
            this.token = token;
        }

        @Override
        protected List<MediaAlbum> compute() {
            final ForkJoinTask<List<MediaAlbum>> mainAlbums = new AlbumGetterTask(String.format(REST_ALBUMS_URL, "me"), MediaAlbum.DEFAULT_PAGE_NAME, token).fork();
            final ForkJoinTask<List<MediaAlbum>> accountAlbums = new RecursiveTask<List<MediaAlbum>>() {
                @Override
                protected List<MediaAlbum> compute() {
                    return callEndpoint(REST_ACCOUNTS_URL,
                                        token,
                                        body -> mapper.readValue(body, FacebookAccountResponse.class).getResponse().stream()
                                                .peek(t -> logger.info("Account from fb {}", t))
                                                .map(t -> new AlbumGetterTask(String.format(REST_ALBUMS_URL, t.getId()), t.getName(), token).fork())
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
        private final String page;
        private final Token token;

        public AlbumGetterTask(final String url, final String page, final Token token) {
            this.url = url;
            this.page = page;
            this.token = token;
        }

        @Override
        protected List<MediaAlbum> compute() {
            return callEndpoint(url,
                                token,
                                body -> mapper.readValue(body, FacebookAlbumsResponse.class).getResponse().stream()
                                        .map(t -> new MediaAlbum(t.getId(), t.getName(), page))
                                        .peek(t -> logger.info("Album from fb {}", t))
                                        .collect(Collectors.toList())
            );
        }
    }


}
