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
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.droidjump.R;
import java.util.ArrayList;
import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private AchievementBuffer achievementBuffer;
    private FragmentActivity activity;
    private List<ItemType> itemTypes;
    private List<String> sectionNames;
    private int sectionIndex;
    private int achievementIndex;

    public AchievementsAdapter(AchievementBuffer achievementBuffer, FragmentActivity activity) {
        this.achievementBuffer = achievementBuffer;
        this.activity = activity;
        itemTypes = new ArrayList<>();
        sectionNames = new ArrayList<>();
        sectionIndex = 0;
        achievementIndex = 0;
        for (int i = 0; i < achievementBuffer.getCount(); i++){
            if (i == 0 && achievementBuffer.get(i).getState() == Achievement.STATE_UNLOCKED){
                    itemTypes.add(ItemType.SECTION_NAME);
                    sectionNames.add("unlocked");
            }
            else if (i == 0 || !(achievementBuffer.get(i).getState() == Achievement.STATE_UNLOCKED) && achievementBuffer.get(i - 1).getState() == Achievement.STATE_UNLOCKED){
                    itemTypes.add(ItemType.SECTION_NAME);
                    sectionNames.add("locked");
            }
            itemTypes.add(ItemType.ACHIEVEMENT);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
            return new SectionViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_item, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mholder, int position) {
        if (itemTypes.get(position) == ItemType.SECTION_NAME){
            SectionViewHolder holder = (SectionViewHolder) mholder;
            Log.d("AchievementsAdapter", "section index: " + sectionIndex);
            holder.getSectionText().setText(sectionNames.get(sectionIndex++));
            return;
        }
        else {
            Log.d("AchievementsAdapter", "achievements index: " + achievementIndex);
            Achievement achievement = achievementBuffer.get(achievementIndex++);
            //holder.getDate().setText(String.valueOf(achievement.getLastUpdatedTimestamp()));
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
                    manager.loadImage(icon, achievement.getRevealedImageUri());
                    break;
                case Achievement.STATE_HIDDEN:
                    break;

            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        if(itemTypes.get(position) == ItemType.SECTION_NAME) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return itemTypes.size();
    }

    public class AchievementViewHolder extends RecyclerView.ViewHolder {

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

    public class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionText;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionText = itemView.findViewById(R.id.section_text);
        }

        public TextView getSectionText() {
            return sectionText;
        }
    }

    public enum ItemType{
        SECTION_NAME,
        ACHIEVEMENT
    }

}
