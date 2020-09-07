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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.droidjump.R;
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mholder, int position) {
        if (items.get(position).getType() == ItemType.SECTION_NAME) {
            SectionViewHolder holder = (SectionViewHolder) mholder;
            holder.getSectionText().setText(sectionNames.get(items.get(position).getListPosition()));
            return;
        }
        Achievement achievement = achievementBuffer.get(items.get(position).getListPosition());
        AchievementViewHolder holder = (AchievementViewHolder) mholder;
        holder.getDescription().setText(achievement.getDescription());
        holder.getName().setText(achievement.getName());
        ImageView icon = holder.getIcon();
        ImageManager manager = ImageManager.create(activity);
        switch (achievement.getState()) {
            case Achievement.STATE_UNLOCKED:
                manager.loadImage(icon, achievement.getUnlockedImageUri());
                break;
            case Achievement.STATE_REVEALED:
                if (achievement.getType() == Achievement.TYPE_INCREMENTAL){
                    holder.getProgressbarLayout().setVisibility(View.VISIBLE);
                    icon.setVisibility(View.GONE);
                    int percentage = getPercentCountOfProgress(achievement);
                    holder.getProgressBar().setProgress(percentage);
                    holder.getProgressText().setText(String.valueOf(percentage) + "%");
                }
                else{
                    manager.loadImage(icon, achievement.getRevealedImageUri());
                }
                break;
            case Achievement.STATE_HIDDEN:
                //TODO(dnikolskaia): Load image for hidden achievements.
                break;
        }
    }

    private int getPercentCountOfProgress(Achievement achievement){
        int totalSteps = achievement.getTotalSteps();
        int currentSteps = achievement.getCurrentSteps();
        Log.d("IncrementslAchievement", "total steps: " + totalSteps + " current steps : " + currentSteps);
        return currentSteps  * 100 / totalSteps;
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
        private ConstraintLayout progressbarLayout;
        private ProgressBar progressBar;
        private TextView progressText;

        public AchievementViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.achievement_name);
            icon = view.findViewById(R.id.icon);
            description = view.findViewById(R.id.description);
            progressbarLayout = view.findViewById(R.id.progressbar_layout);
            progressBar = view.findViewById(R.id.progress_bar);
            progressText = view.findViewById(R.id.progress_text);
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

        public ConstraintLayout getProgressbarLayout() {
            return progressbarLayout;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public TextView getProgressText() {
            return progressText;
        }
    }
}
