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
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.droidjump.drawable.DroidStartView;
import com.google.droidjump.models.LevelManager;
import com.google.droidjump.models.NavigationHelper;

/**
 * Displays Start Screen.
 */
public class StartFragment extends Fragment {
    private MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.start_screen, container, /* attachToRoot= */ false);
        ((LinearLayout) rootView.findViewById(R.id.droid_draw_view)).addView(new DroidStartView(activity));
        rootView.findViewById(R.id.menu_button).setOnClickListener(ignored -> activity.openUserMenu());
        rootView.findViewById(R.id.play_button).setOnClickListener(this::play);
        rootView.findViewById(R.id.level_button).setOnClickListener(this::chooseLevel);
        rootView.findViewById(R.id.new_game_button).setOnClickListener(this::startNewGame);
        rootView.findViewById(R.id.how_to_play_button).setOnClickListener(this::goToHowToPlayScreen);
        NavigationHelper.clearBackStack(activity);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void play(View view) {
        NavigationHelper.navigateToFragment(activity, new GameFragment());
    }

    private void chooseLevel(View view) {
        NavigationHelper.navigateToFragment(activity, new LevelsFragment());
    }

    private void startNewGame(View view) {
        LevelManager.resetGameData();
        activity.countTimeOfPlaying();
        play(view);
    }

    private void goToHowToPlayScreen(View view) {
        NavigationHelper.navigateToFragment(activity, new HowToPlayFragment());
    }
}
