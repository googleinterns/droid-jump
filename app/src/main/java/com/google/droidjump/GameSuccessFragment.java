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
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.droidjump.drawable.DroidStartView;
import com.google.droidjump.models.LevelManager;
import com.google.droidjump.models.NavigationHelper;

/**
 * Displays Game Success Screen.
 */
public class GameSuccessFragment extends Fragment {
    private FragmentActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LevelManager.updateCurrentLevelMaxScore();
        // Get info about score.
        int currentLevelScore = LevelManager.getCurrentLevelScore();
        int currentLevelMaxScore = LevelManager.getLevelMaxScore(LevelManager.getCurrentLevelIndex());

        View rootView = inflater.inflate(R.layout.game_success_screen, container, /* attachToRoot= */ false);
        FloatingActionButton nextLevelButton = rootView.findViewById(R.id.next_button);
        if (LevelManager.getCurrentLevelIndex() < LevelManager.getLevelsLastIndex()) {
            LevelManager.onCurrentLevelCompleted();
            nextLevelButton.setOnClickListener(view -> {
                NavigationHelper.navigateToFragment(activity, new GameFragment());
            });
        } else {
            nextLevelButton.setVisibility(View.INVISIBLE);
        }
        rootView.findViewById(R.id.menu_button).setOnClickListener(view ->
                NavigationHelper.navigateToFragment(activity, new StartFragment()));
        rootView.findViewById(R.id.how_to_play_button).setOnClickListener(view ->
                NavigationHelper.navigateToFragment(activity, new HowToPlayFragment()));
        ((LinearLayout) rootView.findViewById(R.id.droid_draw_view)).addView(new DroidStartView(getActivity()));
        NavigationHelper.addOnBackPressedEventListener(activity, new StartFragment());
        TextView textView = rootView.findViewById(R.id.score_text_view);
        textView.setText(String.format("score: %d \nbest score: %d", currentLevelScore, currentLevelMaxScore));
        return rootView;
    }
}
