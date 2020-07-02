package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.activities.DetailsActivity;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.GlideApp;

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
        ItemTweetBinding binding = ItemTweetBinding.inflate(LayoutInflater.from(ctx), parent, false);
        return new ViewHolder(binding);
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

        ItemTweetBinding binding;

        public ViewHolder(ItemTweetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setupDetailsClick(final Tweet tweet, View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ctx, DetailsActivity.class);
                    i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    ctx.startActivity(i);
                }
            });
        }

        public void bind(final Tweet tweet) {
            // Fix for a ViewHolder showing the wrong image after scrolling
            binding.ivMedia.setImageResource(android.R.color.transparent);

            setupDetailsClick(tweet, itemView);

            GlideApp.with(ctx)
                    .load(tweet.getUser().getImageUrl())
                    .transform(new RoundedCornersTransformation(30, 10))
                    .into(binding.ivAvatar);
            if(tweet.getMedia() != null) {
                GlideApp.with(ctx)
                        .load(tweet.getMedia().getMediaUrl())
                        .override(800, 600)
                        .transform(new RoundedCornersTransformation(30, 10))
                        .into(binding.ivMedia);
            }
            binding.tvBody.setText(tweet.getBody());
            binding.tvScreenName.setText(tweet.getUser().getScreenName());
            binding.tvName.setText(tweet.getUser().getName());
            binding.tvTimestamp.setText(tweet.getRelativeTimeAgo());
        }
    }

}
