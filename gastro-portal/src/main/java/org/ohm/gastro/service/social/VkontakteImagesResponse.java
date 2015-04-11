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
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(final Response response) {
        this.response = response;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {

        @JsonProperty("items")
        private List<Item> items;

        public List<Item> getItems() {
            return items;
        }

        public void setItems(final List<Item> items) {
            this.items = items;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("id")
        private String id;
        @JsonProperty("owner_id")
        private String ownerId;
        @JsonProperty("text")
        private String text;
        @JsonProperty("photo_604")
        private String imageUrl;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(final String ownerId) {
            this.ownerId = ownerId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getLink() {
            return String.format("http://vk.com/photo%s_%s", ownerId, id);
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}
