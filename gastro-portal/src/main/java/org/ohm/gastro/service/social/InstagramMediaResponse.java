package org.ohm.gastro.service.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by ezhulkov on 09.01.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstagramMediaResponse {

    @JsonProperty("pagination")
    private Pagination pagination;

    @JsonProperty("data")
    private List<Media> data;

    public List<Media> getData() {
        return data;
    }

    public void setData(final List<Media> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Media {

        @JsonProperty("type")
        private String type;
        @JsonProperty("link")
        private String link;
        @JsonProperty("images")
        private Images images;
        @JsonProperty("caption")
        private Caption caption;

        public String getType() {
            return type;
        }

        public void setType(final String type) {
            this.type = type;
        }

        public String getLink() {
            return link;
        }

        public void setLink(final String link) {
            this.link = link;
        }

        public Images getImages() {
            return images;
        }

        public void setImages(final Images images) {
            this.images = images;
        }

        public Caption getCaption() {
            return caption;
        }

        public void setCaption(final Caption caption) {
            this.caption = caption;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Caption {

        @JsonProperty("text")
        private String text;

        public String getText() {
            return text;
        }

        public void setText(final String text) {
            this.text = text;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Images {

        @JsonProperty("low_resolution")
        private Image lowResolution;
        @JsonProperty("thumbnail")
        private Image thumbnail;
        @JsonProperty("standard_resolution")
        private Image standardResolution;

        public Image getLowResolution() {
            return lowResolution;
        }

        public void setLowResolution(final Image lowResolution) {
            this.lowResolution = lowResolution;
        }

        public Image getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(final Image thumbnail) {
            this.thumbnail = thumbnail;
        }

        public Image getStandardResolution() {
            return standardResolution;
        }

        public void setStandardResolution(final Image standardResolution) {
            this.standardResolution = standardResolution;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {

        @JsonProperty("url")
        private String url;
        @JsonProperty("width")
        private int width;
        @JsonProperty("height")
        private int height;

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(final int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(final int height) {
            this.height = height;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pagination {

        @JsonProperty("next_url")
        private String nextUrl;
        @JsonProperty("next_max_id")
        private String nextMaxId;

        public void setNextUrl(String nextUrl) {
            this.nextUrl = nextUrl;
        }

        public void setNextMaxId(String nextMaxId) {
            this.nextMaxId = nextMaxId;
        }

        public String getNextUrl() {
            return nextUrl;
        }

        public String getNextMaxId() {
            return nextMaxId;
        }
    }

}
