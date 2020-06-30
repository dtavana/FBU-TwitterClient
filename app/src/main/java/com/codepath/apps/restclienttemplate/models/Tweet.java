package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    public static final String TAG = "TweetModel";

    String body;
    String createdAt;
    User user;
    Media media;
    long id;

    public Tweet() {}

    public static Tweet fromJson(JSONObject obj) throws JSONException {
        Tweet tweet = new Tweet();

        // Log.d(TAG, "fromJson: Original object " + obj.toString());

        tweet.setBody(obj.getString("text"));
        tweet.setCreatedAt(obj.getString("created_at"));
        tweet.setUser(User.fromJson(obj.getJSONObject("user")));
        tweet.setId(obj.getLong("id"));

        JSONObject entities = obj.getJSONObject("entities");
        if(entities.has("media") && entities.getJSONArray("media").length() != 0) {
            JSONObject selectedMedia = entities.getJSONArray("media").getJSONObject(0);
            tweet.setMedia(Media.fromJson(selectedMedia));
        }

        // Log.d(TAG, "fromJson: Constructed tweet + " + tweet.toString());

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

    public String getRelativeTimeAgo() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(getCreatedAt()).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "· " + relativeDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotNull
    @Override
    public String toString() {
        return "Tweet{" +
                "id=" + id +
                '}';
    }
}
