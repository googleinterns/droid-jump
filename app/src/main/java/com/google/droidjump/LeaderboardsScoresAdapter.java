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

package com.google.droidjump;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.droidjump.GameConstants;
import com.google.droidjump.MainActivity;
import com.google.droidjump.R;
import java.util.List;
import java.util.Set;

/**
 * Extracts a scores data and styles it for showing in RecyclerView.
 */
public class LeaderboardsScoresAdapter extends RecyclerView.Adapter {
    private List<LeaderboardScore> items;
    private MainActivity activity;
    private Set<String> friends;
    private boolean friendListAccess;

    public LeaderboardsScoresAdapter(List<LeaderboardScore> items,
                                     FragmentActivity activity,
                                     Set<String> friends,
                                     boolean friendListAccess) {
        this.items = items;
        this.activity = (MainActivity) activity;
        this.friends = friends;
        this.friendListAccess = friendListAccess;
    }

    public void setFriendListAccess(boolean friendListAccess) {
        this.friendListAccess = friendListAccess;
    }

    @NonNull
    @Override
    public ScoresHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboards_score_item, parent, /* attachToRoot = */ false);
        return new ScoresHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder baseHolder, int position) {
        LeaderboardScore score = items.get(position);
        ScoresHolder holder = (ScoresHolder) baseHolder;
        holder.getUsername().setText(score.getScoreHolderDisplayName());
        ImageManager.create(activity).loadImage(holder.getAvatar(), score.getScoreHolderIconImageUri());
        holder.getRank().setText(score.getDisplayRank());
        holder.getScore().setText(String.valueOf(score.getDisplayScore()));
        if (friendListAccess) {
            holder.getComparingButton().setVisibility(View.VISIBLE);
            ImageView playGameServicesIcon = holder.getComparingButton().findViewById(R.id.friend_icon);
            playGameServicesIcon.setOnClickListener(ignored -> showComparingScreen(score.getScoreHolder()));
            if (friends.contains(score.getScoreHolder().getPlayerId())) {
                playGameServicesIcon.setImageResource(R.mipmap.ic_friends);
            } else {
                playGameServicesIcon.setImageResource(R.drawable.ic_not_yet_friends);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void showComparingScreen(Player player) {
        activity.getPlayersClient().getCompareProfileIntent(player).addOnSuccessListener(intent -> {
            activity.startActivityForResult(intent, GameConstants.RC_SHOW_PROFILE);
        });
    }

    /**
     * Gives access to leaderboards_score_item layout and inserts scores data to it.
     */
    private class ScoresHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView rank;
        private TextView score;
        private ImageView avatar;
        private CardView comparingButton;

        public ScoresHolder(View view) {
            super(view);
            username = view.findViewById(R.id.player_username);
            avatar = view.findViewById(R.id.player_avatar);
            rank = view.findViewById(R.id.player_rank);
            score = view.findViewById(R.id.player_score);
            comparingButton = view.findViewById(R.id.friend_section);
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

        public CardView getComparingButton() {
            return comparingButton;
        }
    }
}
