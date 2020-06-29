package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    String name;
    String screenName;
    String imageUrl;

    public User() {}

    public static User fromJson(JSONObject obj) throws JSONException {
        User user = new User();

        user.setName(obj.getString("name"));
        user.setScreenName(obj.getString("screen_name"));
        user.setImageUrl(obj.getString("profile_image_url_https"));

        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return "@" + screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
