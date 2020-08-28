/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.droidjump.leaderboards_data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.droidjump.R;
import java.util.List;

/**
 * Extracts a scores data and styles it for showing in RecyclerView.
 */
public class LeaderboardsScoresAdapter extends RecyclerView.Adapter<LeaderboardsScoresAdapter.ScoresHolder> {
    private List<LeaderboardScore> items;
    private FragmentActivity activity;

    public LeaderboardsScoresAdapter(List<LeaderboardScore> items, FragmentActivity activity) {
        this.items = items;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ScoresHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboards_score_item, parent, /* attachToRoot = */ false);
        return new ScoresHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoresHolder holder, int position) {
        LeaderboardScore score = items.get(position);
        holder.getUsername().setText(score.getScoreHolderDisplayName());
        ImageManager imageManager = ImageManager.create(activity);
        imageManager.loadImage(holder.getAvatar(), score.getScoreHolderIconImageUri());
        holder.getRank().setText(score.getDisplayRank());
        holder.getScore().setText(String.valueOf(score.getDisplayScore()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Gives access to leaderboards_score_item layout and inserts scores data to it.
     */
    public class ScoresHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView rank;
        private TextView score;
        private ImageView avatar;

        public ScoresHolder(View view) {
            super(view);
            username = view.findViewById(R.id.player_username);
            avatar = view.findViewById(R.id.player_avatar);
            rank = view.findViewById(R.id.player_rank);
            score = view.findViewById(R.id.player_score);
        }

        public ImageView getAvatar() {
            return avatar;
        }

        public TextView getUsername() {
            return username;
        }

        public TextView getRank() {
            return rank;
        }

        public TextView getScore() {
            return score;
        }
    }
}
