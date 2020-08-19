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

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import com.google.droidjump.models.LevelManager;

/**
 * Displays Start Screen.
 */
public class StartFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.start_screen, container, /* attachToRoot= */ false);
        ((LinearLayout) rootView.findViewById(R.id.droid_draw_view)).addView(new DroidStartView(getActivity()));
        rootView.findViewById(R.id.play_button).setOnClickListener(this::play);
        rootView.findViewById(R.id.level_button).setOnClickListener(this::chooseLevel);
        rootView.findViewById(R.id.new_game_button).setOnClickListener(this::startNewGame);
        rootView.findViewById(R.id.how_to_play_button).setOnClickListener(this::goToHowToPlayScreen);
        return rootView;
    }

    private void play(View view) {
        findNavController(view).navigate(R.id.action_start_screen_to_game_screen);
    }

    private void chooseLevel(View view) {
        findNavController(view).navigate(R.id.action_start_screen_to_levels_screen);
    }

    private void startNewGame(View view) {
        LevelManager.resetGameData();
        findNavController(view).navigate(R.id.action_start_screen_to_game_screen);
    }

    private void goToHowToPlayScreen(View view) {
        findNavController(view).navigate(R.id.action_start_screen_to_how_to_play_screen);
    }
}
