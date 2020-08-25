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

public class LeaderboardsAdapter extends RecyclerView.Adapter<LeaderboardsAdapter.LeaderboardsHolder> {
    private List<Leaderboard> items;

    @NonNull
    @Override
    public LeaderboardsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboards_item, parent, /* attachToRoot = */ false);
        return new LeaderboardsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardsHolder holder, int position) {
        Leaderboard leaderboard = items.get(position);
        holder.getAvatar().setImageResource(leaderboard.getAvatar());
        holder.getName().setText(leaderboard.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class LeaderboardsHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView avatar;

        public LeaderboardsHolder(View view) {
            super(view);
        }

        public TextView getName() {
            return name;
        }

        public ImageView getAvatar() {
            return avatar;
        }
    }
}
