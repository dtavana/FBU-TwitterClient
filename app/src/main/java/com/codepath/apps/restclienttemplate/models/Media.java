package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Media {

    public static final String DEFAULT_SIZE = "small";
    public static final String INCREASED_SIZE = "large";

    String mediaUrl;

    public Media() {}

    public static Media fromJson(JSONObject obj) throws JSONException {
        Media media = new Media();

        media.setMediaUrl(obj.getString("media_url_https"));

        return media;
    }

    public String getLargeMediaUrl() {
        return String.format("%s:%s", mediaUrl, INCREASED_SIZE);
    }

    public String getMediaUrl() {
        return String.format("%s:%s", mediaUrl, DEFAULT_SIZE);
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
