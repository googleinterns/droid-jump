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

package com.google.droidjump.achievements_data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.droidjump.AchievementDetailsFragment;
import com.google.droidjump.R;
import com.google.droidjump.models.NavigationHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of achievements in RecyclerView.
 **/
public class AchievementsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private AchievementBuffer achievementBuffer;
    private FragmentActivity activity;
    private List<RecyclerViewItem> items;
    private List<String> sectionNames;

    public AchievementsAdapter(AchievementBuffer achievementBuffer, FragmentActivity activity) {
        this.achievementBuffer = achievementBuffer;
        this.activity = activity;
        fillViewItems();
    }

    private void fillViewItems() {
        items = new ArrayList<>();
        sectionNames = new ArrayList<>();
        for (int i = 0; i < achievementBuffer.getCount(); i++) {
            if (i == 0 && achievementBuffer.get(i).getState() == Achievement.STATE_UNLOCKED) {
                items.add(new RecyclerViewItem(ItemType.SECTION_NAME, sectionNames.size()));
                sectionNames.add("unlocked");
            } else if (i == 0 || !(achievementBuffer.get(i).getState() == Achievement.STATE_UNLOCKED) && achievementBuffer.get(i - 1).getState() == Achievement.STATE_UNLOCKED) {
                items.add(new RecyclerViewItem(ItemType.SECTION_NAME, sectionNames.size()));
                sectionNames.add("locked");
            }
            items.add(new RecyclerViewItem(ItemType.ACHIEVEMENT, i));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
            return new SectionViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_item, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mholder, int position) {
        if (items.get(position).getType() == ItemType.SECTION_NAME) {
            SectionViewHolder holder = (SectionViewHolder) mholder;
            holder.getSectionText().setText(sectionNames.get(items.get(position).getListPosition()));
            return;
        }
        Achievement achievement = achievementBuffer.get(items.get(position).getListPosition());
        AchievementViewHolder holder = (AchievementViewHolder) mholder;
        ImageView icon = holder.getIcon();
        ImageManager manager = ImageManager.create(activity);
        switch (achievement.getState()) {
            case Achievement.STATE_UNLOCKED:
                manager.loadImage(icon, achievement.getUnlockedImageUri());
                holder.getDescription().setText(achievement.getDescription());
                holder.getName().setText(achievement.getName());
                break;
            case Achievement.STATE_REVEALED:
                manager.loadImage(icon, achievement.getRevealedImageUri());
                holder.getDescription().setText(achievement.getDescription());
                holder.getName().setText(achievement.getName());
                break;
            case Achievement.STATE_HIDDEN:
                holder.getDescription().setText(R.string.hidden_achievement_description);
                holder.getName().setText(R.string.hidden_achievement_name);
                break;
        }
        holder.itemView.setOnClickListener(view -> {
            NavigationHelper.navigateToFragment(activity, new AchievementDetailsFragment(achievement));
        });

    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        if (items.get(position).getType() == ItemType.SECTION_NAME) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public enum ItemType {
        SECTION_NAME,
        ACHIEVEMENT
    }

    private static class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionText;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionText = itemView.findViewById(R.id.section_text);
        }

        public TextView getSectionText() {
            return sectionText;
        }
    }

    private static class RecyclerViewItem {
        private ItemType type;
        private int listPosition;

        RecyclerViewItem(ItemType type, int listPosition) {
            this.type = type;
            this.listPosition = listPosition;
        }

        public ItemType getType() {
            return type;
        }

        public int getListPosition() {
            return listPosition;
        }
    }

    private static class AchievementViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView description;
        private TextView date;
        private ImageView icon;

        public AchievementViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.achievement_name);
            icon = view.findViewById(R.id.icon);
            description = view.findViewById(R.id.description);
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
