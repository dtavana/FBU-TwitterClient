package com.codepath.apps.restclienttemplate.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.network.TwitterApp;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;

    ActivityTimelineBinding binding;
    MenuItem miActionProgress;


    List<Tweet> tweets;

    TwitterClient client;

    EndlessRecyclerViewScrollListener scrollListener;

    long maxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        client = TwitterApp.getRestClient(this);

        maxId = 0;

        tweets = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvTweets.setLayoutManager(layoutManager);
        binding.rvTweets.setAdapter(new TweetsAdapter(this, tweets));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.rvTweets.addItemDecoration(dividerItemDecoration);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                fetchTimelineAsync();
            }
        };

        binding.rvTweets.addOnScrollListener(scrollListener);

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh: Starting refresh");
                populateHomeTimeline(true);
            }
        });

        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateHomeTimeline(false);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgress = menu.findItem(R.id.miActionProgress);
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgress.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgress.setVisible(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.compose) {
            startActivityForResult(new Intent(this, ComposeActivity.class), REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            Objects.requireNonNull(binding.rvTweets.getAdapter()).notifyItemInserted(0);
            binding.rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline(final boolean isRefreshing) {
        // Pass in a maxId of 0 so that we get a full refresh
        client.getHomeTimeline(0, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess: " + json.toString());
                ((TweetsAdapter) Objects.requireNonNull(binding.rvTweets.getAdapter())).clear();
                JSONArray arr = json.jsonArray;
                try {
                    for(Tweet tweet : Tweet.fromJsonArray(arr)) {
                        if(tweet.getId() < maxId || maxId <= 0) {
                            maxId = tweet.getId();
                            Log.d(TAG, "onSuccessPagination: New maxId " + maxId);
                        }
                        tweets.add(tweet);
                    }
                    binding.rvTweets.getAdapter().notifyDataSetChanged();
                    if(isRefreshing) {
                        binding.swipeContainer.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onSuccess: JSON Exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: " + response, throwable);
            }
        });
    }

    public void fetchTimelineAsync() {
        showProgressBar();
        // Decrement maxId so we only get new tweets
        maxId--;
        Log.d(TAG, "fetchTimelineAsync: Decremented maxId " + maxId);
        final int startPosition = tweets.size();
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccessPagination: " + json.toString());
                JSONArray arr = json.jsonArray;
                try {
                    int insertedItemCnt = 0;
                    for(Tweet tweet : Tweet.fromJsonArray(arr)) {
                        if(tweet.getId() < maxId || maxId <= 0) {
                            maxId = tweet.getId();
                            Log.d(TAG, "onSuccessPagination: New maxId " + maxId);
                        }
                        tweets.add(tweet);
                        insertedItemCnt++;
                    }
                    Objects.requireNonNull(binding.rvTweets.getAdapter()).notifyItemRangeInserted(startPosition, insertedItemCnt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailurePagination: Error refreshing", throwable);
                hideProgressBar();
            }
        });
    }
}