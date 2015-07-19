package org.ohm.gastro.service.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by ezhulkov on 09.01.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkontakteGroupsResponse {

    @JsonProperty("response")
    private Response response = new Response();

    public Response getResponse() {
        return response;
    }

    public void setResponse(final Response response) {
        this.response = response;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {

        @JsonProperty("items")
        private List<Item> items = Lists.newArrayList();

        public List<Item> getItems() {
            return items;
        }

        public void setItems(final List<Item> items) {
            this.items = items;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "items=" + items +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("id")
        private String id;
        @JsonProperty("name")
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }

    }

    @Override
    public String toString() {
        return "VkontakteGroupsResponse{" +
                "response=" + response +
                '}';
    }
}
