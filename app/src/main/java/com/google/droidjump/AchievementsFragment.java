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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.images.ImageManager;
import com.google.droidjump.models.NavigationHelper;

/**
 * Displays Achievements Screen.
 */
public class AchievementsFragment extends Fragment {
    private MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.achievements_screen, container, /* attachToRoot= */ false);
        ImageManager manager = ImageManager.create(activity);
        ImageView userAvatar = rootView.findViewById(R.id.icon);
        manager.loadImage(userAvatar, activity.getPlayer().getIconImageUri());
        rootView.findViewById(R.id.menu_button).setOnClickListener(ignored ->
                NavigationHelper.navigateToFragment(activity, new StartFragment()));
        RecyclerView achievementsView = rootView.findViewById(R.id.achievements_recycler_view);
        achievementsView.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));

        // TODO(dnikolskaia): Extract data from Achievements client.
        RecyclerView.Adapter adapter = new RecyclerView.Adapter<AchievementViewHolder>() {
            @Override
            public AchievementViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.achievement_item, viewGroup, false);
                return new AchievementViewHolder(view);
            }

            @Override
            public void onBindViewHolder(AchievementViewHolder viewHolder, int i) {
                // Example of achievement.
                viewHolder.getDate().setText("Aug 24");
                viewHolder.getDescription().setText("Overcome 1 obstacle");
                viewHolder.getName().setText("MyAchievement");
                viewHolder.getIcon().setImageResource(R.mipmap.cactus);
            }

            @Override
            public int getItemCount() {
                return 1;
            }

        };

        achievementsView.setAdapter(adapter);
        return rootView;
    }

    private class AchievementViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView description;
        private TextView date;
        private ImageView icon;

        public AchievementViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.achievement_name);
            icon = view.findViewById(R.id.icon);
            description = view.findViewById(R.id.description);
            date = view.findViewById(R.id.date);
        }


        public TextView getName() {
            return name;
        }

        public TextView getDescription() {
            return description;
        }

        public TextView getDate() {
            return date;
        }

        public ImageView getIcon() {
            return icon;
        }
    }
}
