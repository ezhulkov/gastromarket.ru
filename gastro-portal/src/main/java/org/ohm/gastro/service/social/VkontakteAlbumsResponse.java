package org.ohm.gastro.service.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by ezhulkov on 09.01.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkontakteAlbumsResponse {

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

        @JsonProperty("aid")
        private String id;
        @JsonProperty("title")
        private String title;
        @JsonProperty("size")
        private int size;
        @JsonProperty("thumb_src")
        private String thumbSrc;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getThumbSrc() {
            return thumbSrc;
        }

        public void setThumbSrc(String thumbSrc) {
            this.thumbSrc = thumbSrc;
        }

    }

}
