package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.GlideApp;
import com.codepath.apps.restclienttemplate.InflateImageActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context ctx;
    List<Tweet> tweets;

    public TweetsAdapter(Context ctx, List<Tweet> tweets) {
        this.ctx = ctx;
        this.tweets = tweets;
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweets) {
        this.tweets.addAll(tweets);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;
        ImageView ivMedia;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        public void bind(final Tweet tweet) {
            GlideApp.with(ctx)
                    .load(tweet.getUser().getImageUrl())
                    .transform(new RoundedCornersTransformation(30, 10))
                    .into(ivAvatar);
            if(tweet.getMedia() != null) {
                GlideApp.with(ctx)
                        .load(tweet.getMedia().getMediaUrl())
                        .override(800, 600)
                        .transform(new RoundedCornersTransformation(30, 10))
                        .into(ivMedia);
                ivMedia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(ctx, InflateImageActivity.class);
                        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                        ctx.startActivity(i);
                    }
                });
            }
            tvBody.setText(tweet.getBody());
            tvScreenName.setText(tweet.getUser().getScreenName());
            tvName.setText(tweet.getUser().getName());
            tvTimestamp.setText(tweet.getRelativeTimeAgo());
        }
    }

}
