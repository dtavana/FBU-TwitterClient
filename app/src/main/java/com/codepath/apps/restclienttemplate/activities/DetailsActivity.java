
package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailsBinding;
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

    ActivityDetailsBinding binding;

    Tweet tweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Objects.requireNonNull(getSupportActionBar()).hide();

        client = TwitterApp.getRestClient(this);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        updateFavoriteColor();
        updateRetweetColor();
        setupClickListeners();

        binding.tvBody.setText(tweet.getBody());
        binding.tvScreenName.setText(tweet.getUser().getScreenName());
        binding.tvName.setText(tweet.getUser().getName());
        binding.tvTimestamp.setText(tweet.getRelativeTimeAgo());
        GlideApp.with(this)
                .load(tweet.getUser().getImageUrl())
                .transform(new RoundedCornersTransformation(30, 10))
                .into(binding.ivAvatar);
        if(tweet.getMedia() != null) {
            GlideApp.with(this)
                    .load(tweet.getMedia().getLargeMediaUrl())
                    .transform(new RoundedCornersTransformation(30, 10))
                    .into(binding.ivInflatedImage);
        }

    }

    private void updateFavoriteColor() {
        if(tweet.isFavorited()) {
            binding.ivFavorite.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_light)));
        }
        else {
            binding.ivFavorite.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.twitter_secondary_white)));
        }
    }

    private void updateRetweetColor() {
        if(tweet.isRetweeted()) {
            binding.ivRetweet.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_green_light)));
        }
        else {
            binding.ivRetweet.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.twitter_secondary_white)));
        }
    }

    private void setupClickListeners() {
        binding.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailsActivity.this, ComposeActivity.class);
                i.putExtra(ComposeActivity.KEY_STARTTEXT, tweet.getUser().getScreenName() + " ");
                startActivity(i);
            }
        });

        binding.ivRetweet.setOnClickListener(new View.OnClickListener() {
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

        binding.ivFavorite.setOnClickListener(new View.OnClickListener() {
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