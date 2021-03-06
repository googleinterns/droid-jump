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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import java.util.List;

/**
 * Extracts a friends data and styles it for showing in RecyclerView.
 */
public class FriendsAdapter extends RecyclerView.Adapter {
    private List<Player> items;
    private MainActivity activity;
    private PlayersClient client;

    public FriendsAdapter(List<Player> items, FragmentActivity activity) {
        this.items = items;
        this.activity = (MainActivity) activity;
        client = this.activity.getPlayersClient();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_item, parent, /* attachToRoot = */ false);
        return new FriendsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder baseHolder, int position) {
        Player player = items.get(position);
        FriendsHolder holder = (FriendsHolder) baseHolder;
        ImageManager.create(activity).loadImage(holder.getAvatar(), player.getIconImageUri());
        holder.getName().setText(player.getDisplayName());
        holder.getFriendIcon().setOnClickListener(ignored -> showComparingScreen(player));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void showComparingScreen(Player player) {
        client.getCompareProfileIntent(player).addOnSuccessListener(intent -> {
            activity.startActivityForResult(intent, GameConstants.RC_SHOW_PROFILE);
        });
    }

    /**
     * Gives access to friends_item layout and inserts leaderboards data to it.
     */
    private class FriendsHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView avatar;
        private ImageView friendIcon;

        public FriendsHolder(View view) {
            super(view);
            name = view.findViewById(R.id.friend_item_name);
            avatar = view.findViewById(R.id.friend_item_avatar);
            friendIcon = view.findViewById(R.id.friend_icon);
        }

        public TextView getName() {
            return name;
        }

        public ImageView getAvatar() {
            return avatar;
        }

        public ImageView getFriendIcon() {
            return friendIcon;
        }
    }
}
