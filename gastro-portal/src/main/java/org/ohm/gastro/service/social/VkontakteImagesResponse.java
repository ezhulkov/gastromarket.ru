package org.ohm.gastro.service.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by ezhulkov on 09.01.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkontakteImagesResponse {

    @JsonProperty("response")
    private List<Item> response;

    public List<Item> getResponse() {
        return response;
    }

    public void setResponse(List<Item> response) {
        this.response = response;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("pid")
        private String pid;
        @JsonProperty("owner_id")
        private String ownerId;
        @JsonProperty("text")
        private String text;
        @JsonProperty("src_xxbig")
        private String imageUrl;
        @JsonProperty("src_big")
        private String imageUrlSmall;

        public String getPid() {
            return pid;
        }

        public void setPid(final String pid) {
            this.pid = pid;
        }

        public String getImageUrlSmall() {
            return imageUrlSmall;
        }

        public void setImageUrlSmall(final String imageUrlSmall) {
            this.imageUrlSmall = imageUrlSmall;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getLink() {
            return String.format("http://vk.com/photo%s_%s", ownerId, pid);
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}
