package org.ohm.gastro.service.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ezhulkov on 09.01.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VKontakteUserProfile {

    @JsonProperty("first_name")
    private String firstName = "";

    @JsonProperty("last_name")
    private String lastName = "";

    @JsonProperty("photo")
    private String photo = "";

    @JsonProperty("photo_big")
    private String photoBig = "";

    public String getPhotoBig() {
        return photoBig;
    }

    public void setPhotoBig(String photoBig) {
        this.photoBig = photoBig;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
