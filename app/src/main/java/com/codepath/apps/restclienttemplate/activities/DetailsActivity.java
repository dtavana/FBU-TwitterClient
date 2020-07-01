
package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.network.TwitterApp;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.GlideApp;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.Objects;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";

    ImageView ivInflatedImage;
    ImageView ivAvatar;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvName;
    TextView tvTimestamp;
    ImageView ivReply;
    ImageView ivRetweet;
    ImageView ivFavorite;

    Tweet tweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Objects.requireNonNull(getSupportActionBar()).hide();

        client = TwitterApp.getRestClient(this);

        ivInflatedImage = findViewById(R.id.ivInflatedImage);
        ivAvatar = findViewById(R.id.ivAvatar);
        tvBody = findViewById(R.id.tvBody);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvName = findViewById(R.id.tvName);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        ivReply = findViewById(R.id.ivReply);
        ivRetweet = findViewById(R.id.ivRetweet);
        ivFavorite = findViewById(R.id.ivFavorite);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        updateFavoriteColor();
        updateRetweetColor();
        setupClickListeners();

        tvBody.setText(tweet.getBody());
        tvScreenName.setText(tweet.getUser().getScreenName());
        tvName.setText(tweet.getUser().getName());
        tvTimestamp.setText(tweet.getRelativeTimeAgo());
        GlideApp.with(this)
                .load(tweet.getUser().getImageUrl())
                .transform(new RoundedCornersTransformation(30, 10))
                .into(ivAvatar);
        if(tweet.getMedia() != null) {
            GlideApp.with(this)
                    .load(tweet.getMedia().getLargeMediaUrl())
                    .transform(new RoundedCornersTransformation(30, 10))
                    .into(ivInflatedImage);
        }

    }

    private void updateFavoriteColor() {
        if(tweet.isFavorited()) {
            ivFavorite.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_light)));
        }
        else {
            ivFavorite.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.twitter_secondary_white)));
        }
    }

    private void updateRetweetColor() {
        if(tweet.isRetweeted()) {
            ivRetweet.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_green_light)));
        }
        else {
            ivRetweet.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.twitter_secondary_white)));
        }
    }

    private void setupClickListeners() {
        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean retweeted = tweet.isRetweeted();
                if(retweeted) {
                    // API call
                    client.postUnretweet(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess: Succesfully unretweeted");
                            // Change object value
                            tweet.setRetweeted(false);
                            // Update button colors
                            updateRetweetColor();
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure: Failed to unretweet", throwable);
                        }
                    });
                }
                else {
                    // API call
                    client.postRetweet(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess: Succesfully retweeted");
                            // Change object value
                            tweet.setRetweeted(true);
                            // Update button colors
                            updateRetweetColor();
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure: Failed to retweet", throwable);
                        }
                    });
                }
            }
        });

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean favorited = tweet.isFavorited();
                if(favorited) {
                    // API call
                    client.postDestroyFavorite(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess: Succesfully unfavorited");
                            // Change object value
                            tweet.setFavorited(false);
                            // Update button colors
                            updateFavoriteColor();
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure: Failed to unfavorite", throwable);
                        }
                    });
                }
                else {
                    // API call
                    client.postCreateFavorite(tweet.getId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess: Succesfully favorited");
                            // Change object value
                            tweet.setFavorited(true);
                            // Update button colors
                            updateFavoriteColor();
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure: Failed to favorite", throwable);
                        }
                    });
                }
            }
        });
    }
}