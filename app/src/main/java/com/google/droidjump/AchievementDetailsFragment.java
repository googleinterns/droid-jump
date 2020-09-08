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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.achievement.Achievement;
import com.google.droidjump.models.NavigationHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Displays achievement details.
 */
public class AchievementDetailsFragment extends Fragment {
    private Achievement achievement;
    private MainActivity activity;

    public AchievementDetailsFragment(Achievement achievement) {
        this.achievement = achievement;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NewApi", "DefaultLocale"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.achievement_details_screen, container, /* attachToRoot= */ false);
        rootView.findViewById(R.id.back_button).setOnClickListener(ignored ->
                NavigationHelper.navigateToFragment(activity, new AchievementsFragment()));
        ImageView achievementIcon = rootView.findViewById(R.id.achievement_icon);
        TextView nameTextView = rootView.findViewById(R.id.achievement_name);
        TextView descriptionTextView = rootView.findViewById(R.id.achievement_description);
        TextView xpTextView = rootView.findViewById(R.id.xp_value);
        TextView dateTextView = rootView.findViewById(R.id.date);
        TextView dateCaptionTextView = rootView.findViewById(R.id.date_caption);
        LinearLayout xpLayout = rootView.findViewById(R.id.xp_layout);
        LinearLayout dateLayout = rootView.findViewById(R.id.date_layout);
        dateTextView.setText(new SimpleDateFormat("d MMMM y", Locale.ENGLISH)
                .format(new Date(achievement.getLastUpdatedTimestamp())));
        nameTextView.setText(achievement.getName());
        descriptionTextView.setText(achievement.getDescription());
        xpTextView.setText(String.valueOf(achievement.getXpValue()));

        switch (achievement.getState()) {
            case Achievement.STATE_UNLOCKED:
                ImageManager.create(activity).loadImage(achievementIcon, achievement.getUnlockedImageUri());
                dateCaptionTextView.setText(activity.getText(R.string.date_caption_for_unlocked_achievements));
                break;
            case Achievement.STATE_REVEALED:
                if (achievement.getType() == Achievement.TYPE_INCREMENTAL) {
                    rootView.findViewById(R.id.progressbar_layout).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.progress_info_layout).setVisibility(View.VISIBLE);
                    achievementIcon.setVisibility(View.GONE);
                    int progress = getPercentCountOfProgress(achievement);
                    ((ProgressBar) rootView.findViewById(R.id.progress_bar)).setProgress(progress);
                    ((TextView) rootView.findViewById(R.id.progress_text)).setText(String.format("%d%s", progress, "%"));
                    String stepsCompleted = activity.getString(R.string.steps_completed);
                    ((TextView) rootView.findViewById(R.id.steps))
                            .setText(String.format("%s %d/%d", stepsCompleted, achievement.getCurrentSteps(), achievement.getTotalSteps()));
                    break;
                }
                achievementIcon.setImageDrawable(activity.getDrawable(R.drawable.ic_baseline_lock_24));
                dateLayout.setVisibility(View.GONE);
                break;
            case Achievement.STATE_HIDDEN:
                achievementIcon.setImageDrawable(activity.getDrawable(R.drawable.ic_baseline_block_24));
                nameTextView.setText(R.string.hidden_achievement_name);
                descriptionTextView.setText(R.string.hidden_achievement_description);
                dateLayout.setVisibility(View.GONE);
                xpLayout.setVisibility(View.GONE);
                break;
        }
        return rootView;
    }

    private int getPercentCountOfProgress(Achievement achievement) {
        return achievement.getCurrentSteps() * 100 / achievement.getTotalSteps();
    }
}

