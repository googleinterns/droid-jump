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
import com.google.droidjump.drawable.DroidStartView;
import com.google.droidjump.models.LevelManager;
import com.google.droidjump.models.NavigationHelper;

/**
 * Displays Game Failure Screen.
 */
public class GameFailureFragment extends Fragment {
    FragmentActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LevelManager.updateCurrentLevelMaxScore();
        View rootView = inflater.inflate(R.layout.game_failure_screen,
                container, /* attachToRoot= */ false);
        ((TextView) rootView.findViewById(R.id.score_text_view))
                .setText(String.format("score: %d \nbest score: %d", LevelManager.getCurrentLevelScore(),
                        LevelManager.getLevelMaxScore(LevelManager.getCurrentLevelIndex())));
        rootView.findViewById(R.id.retry_button).setOnClickListener(ignored ->
                NavigationHelper.navigateToFragment(activity, new GameFragment()));
        rootView.findViewById(R.id.menu_button).setOnClickListener(ignored ->
                NavigationHelper.navigateToFragment(activity, new StartFragment()));
        rootView.findViewById(R.id.how_to_play_button).setOnClickListener(ignored ->
                NavigationHelper.navigateToFragment(activity, new HowToPlayFragment()));
        ((LinearLayout) rootView.findViewById(R.id.droid_draw_view)).addView(new DroidStartView(activity));
        NavigationHelper.addOnBackPressedEventListener(activity, new StartFragment());
        return rootView;
    }
}
