package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.network.TwitterApp;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.Objects;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = ComposeActivity.class.getSimpleName();
    public static final String KEY_STARTTEXT = "startText";

    public static final int MAX_TWEET_LENGTH = 280;

    ActivityComposeBinding binding;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComposeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        client = TwitterApp.getRestClient(this);

        String etComposeStartText = getIntent().getStringExtra(KEY_STARTTEXT);
        if(etComposeStartText != null) {
            binding.etCompose.setText(etComposeStartText);
        }

        binding.etCompose.requestFocus();

        binding.btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tweetContent = binding.etCompose.getText().toString();
                if(tweetContent.isEmpty()) {
                    //TODO Android Snackbar
                    Toast.makeText(ComposeActivity.this, "Your tweet can not be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LENGTH) {
                    //TODO Android Snackbar
                    Toast.makeText(ComposeActivity.this, "Your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                client.postTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess: Published tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "onSuccess: Parsed tweet  " + tweet.getBody());
                            setResult(RESULT_OK, new Intent().putExtra("tweet", Parcels.wrap(tweet)));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure: Failed to publish tweet", throwable);
                    }
                });

            }
        });

    }
}