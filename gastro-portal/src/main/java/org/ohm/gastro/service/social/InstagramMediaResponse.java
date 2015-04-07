package org.ohm.gastro.service.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ezhulkov on 09.01.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstagramMediaResponse {

    @JsonProperty("pagination")
    private Pagination pagination;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Pagination {

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
