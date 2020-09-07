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

import android.annotation.SuppressLint;
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
    private final static int SECTION_NAME_TYPE = 0;
    private final static int ACHIEVEMENT_TYPE = 1;

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
            int currentAchievementState = achievementBuffer.get(i).getState();
            boolean currentAchievementIsUnlocked = currentAchievementState == Achievement.STATE_UNLOCKED;
            if (i == 0) {
                if (currentAchievementIsUnlocked) {
                    items.add(new RecyclerViewItem(ItemType.SECTION_NAME, sectionNames.size()));
                    sectionNames.add(activity.getString(R.string.unlocked_section_name));
                } else {
                    items.add(new RecyclerViewItem(ItemType.SECTION_NAME, sectionNames.size()));
                    sectionNames.add(activity.getString(R.string.locked_section_name));
                }
            } else if (!currentAchievementIsUnlocked) {
                int previousAchievementState = achievementBuffer.get(i - 1).getState();
                if (previousAchievementState == Achievement.STATE_UNLOCKED) {
                    items.add(new RecyclerViewItem(ItemType.SECTION_NAME, sectionNames.size()));
                    sectionNames.add(activity.getString(R.string.locked_section_name));
                }
            }
            items.add(new RecyclerViewItem(ItemType.ACHIEVEMENT, i));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SECTION_NAME_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
            return new SectionViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_item, parent, false);
        return new AchievementViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NewApi"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int listPosition = items.get(position).getListPosition();
        if (holder instanceof SectionViewHolder) {
            ((SectionViewHolder) holder).getSectionText().setText(sectionNames.get(listPosition));
            return;
        }
        Achievement achievement = achievementBuffer.get(listPosition);
        AchievementViewHolder achievementHolder = (AchievementViewHolder) holder;
        achievementHolder.getDescription().setText(achievement.getDescription());
        achievementHolder.getName().setText(achievement.getName());
        ImageView icon = achievementHolder.getIcon();
        switch (achievement.getState()) {
            case Achievement.STATE_UNLOCKED:
                ImageManager.create(activity).loadImage(icon, achievement.getUnlockedImageUri());
                break;
            case Achievement.STATE_REVEALED:
                icon.setImageDrawable(activity.getDrawable(R.drawable.ic_baseline_lock_24));
                break;
            case Achievement.STATE_HIDDEN:
                icon.setImageDrawable(activity.getDrawable(R.drawable.ic_baseline_block_24));
                achievementHolder.getDescription().setText(R.string.hidden_achievement_description);
                achievementHolder.getName().setText(R.string.hidden_achievement_name);
                break;
        }
        achievementHolder.itemView.setOnClickListener(view -> {
            NavigationHelper.navigateToFragment(activity, new AchievementDetailsFragment(achievement));
        });
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        if (items.get(position).getType() == ItemType.SECTION_NAME) {
            return SECTION_NAME_TYPE;
        }
        return ACHIEVEMENT_TYPE;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private enum ItemType {
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

        public ImageView getIcon() {
            return icon;
        }
    }
}
