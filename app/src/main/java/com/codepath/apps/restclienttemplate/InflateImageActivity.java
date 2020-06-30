package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.Objects;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class InflateImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inflate_image);

        Objects.requireNonNull(getSupportActionBar()).hide();

        ImageView ivInflatedImage = findViewById(R.id.ivInflatedImage);
        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        TextView tvBody = findViewById(R.id.tvBody);
        TextView tvScreenName = findViewById(R.id.tvScreenName);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvTimestamp = findViewById(R.id.tvTimestamp);

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        tvBody.setText(tweet.getBody());
        tvScreenName.setText(tweet.getUser().getScreenName());
        tvName.setText(tweet.getUser().getName());
        tvTimestamp.setText(tweet.getRelativeTimeAgo());
        GlideApp.with(this)
                .load(tweet.getUser().getImageUrl())
                .transform(new RoundedCornersTransformation(30, 10))
                .into(ivAvatar);
        GlideApp.with(this)
                .load(tweet.getMedia().getLargeMediaUrl())
                .into(ivInflatedImage);
    }
}