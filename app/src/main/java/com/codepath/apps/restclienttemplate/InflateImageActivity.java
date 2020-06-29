package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.codepath.apps.restclienttemplate.models.Media;
import com.dtavana.flixter.GlideApp;

import org.parceler.Parcels;

public class InflateImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inflate_image);

        ImageView ivInflatedImage = findViewById(R.id.ivInflatedImage);

        Media media = Parcels.unwrap(getIntent().getParcelableExtra(Media.class.getSimpleName()));

        GlideApp.with(this).load(media.getLargeMediaUrl()).into(ivInflatedImage);


    }
}