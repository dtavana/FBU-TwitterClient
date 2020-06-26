package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {
    String body;
    String createdAt;
    User user;

    public static Tweet fromJson(JSONObject obj) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.setBody(obj.getString("text"));
        tweet.setCreatedAt(obj.getString("created_at"));
        tweet.setUser(User.fromJson(obj.getJSONObject("user")));

        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray arr) throws JSONException {
        List<Tweet> res = new ArrayList<>();

        for(int i = 0; i < arr.length(); i++) {
            res.add(fromJson(arr.getJSONObject(i)));
        }

        return res;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
