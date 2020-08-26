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
import androidx.recyclerview.widget.RecyclerView;
import com.google.droidjump.R;
import java.util.List;

/**
 * Extracts a player data and styles it for showing in RecyclerView.
 */
public class LeaderboardsPlayersAdapter extends RecyclerView.Adapter<LeaderboardsPlayersAdapter.PlayersHolder> {
    private List<LeaderboardsPlayer> items;

    public LeaderboardsPlayersAdapter(List<LeaderboardsPlayer> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PlayersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboards_player_item, parent, /* attachToRoot = */ false);
        return new PlayersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayersHolder holder, int position) {
        LeaderboardsPlayer leaderboardsPlayer = items.get(position);
        holder.getUsername().setText(leaderboardsPlayer.getUsername());
        holder.getAvatar().setImageResource(leaderboardsPlayer.getAvatar());
        holder.getRank().setText(String.valueOf(leaderboardsPlayer.getRank()));
        holder.getScore().setText(String.valueOf(leaderboardsPlayer.getScore()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Holds players data.
     */
    public class PlayersHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView rank;
        private TextView score;
        private ImageView avatar;

        public PlayersHolder(View view) {
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
