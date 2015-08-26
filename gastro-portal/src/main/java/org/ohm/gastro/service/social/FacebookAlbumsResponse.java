package org.ohm.gastro.service.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by ezhulkov on 09.01.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookAlbumsResponse {

    @JsonProperty("data")
    private List<Item> response = Lists.newArrayList();

    public List<Item> getResponse() {
        return response;
    }

    public void setResponse(List<Item> response) {
        this.response = response;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("id")
        private String id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("count")
        private int count;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

}
