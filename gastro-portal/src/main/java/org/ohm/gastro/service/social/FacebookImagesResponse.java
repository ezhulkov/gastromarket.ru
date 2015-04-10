package org.ohm.gastro.service.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by ezhulkov on 09.01.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookImagesResponse {

    @JsonProperty("data")
    private List<Item> response;

    public List<Item> getResponse() {
        return response;
    }

    public void setResponse(List<Item> response) {
        this.response = response;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("link")
        private String link;
        @JsonProperty("name")
        private String name;
        @JsonProperty("images")
        private List<Image> images;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {

        @JsonProperty("source")
        private String source;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

    }

}
