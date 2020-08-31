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
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.achievement.Achievement;

/**
 * Displays achievement details.
 */
public class AchievementDetailsFragment extends Fragment {
    private Achievement achievement;
    private MainActivity activity;

    public AchievementDetailsFragment(Achievement achievement){
        this.achievement = achievement;
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.achievement_details_screen, container, /* attachToRoot= */ false);
        ImageView icon = rootView.findViewById(R.id.achievement_icon);
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
        TextView textView = rootView.findViewById(R.id.achievement_title);
        textView.setText(achievement.getName());
        return rootView;
    }
}

