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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Displays Start Screen.
 */
public class StartFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.start_screen, container, /* attachToRoot= */ false);
        Button playButton = rootView.findViewById(R.id.play_button);
        Button levelButton = rootView.findViewById(R.id.level_button);
        Button newGameButton = rootView.findViewById(R.id.new_game_button);
        FloatingActionButton howToPlayButton = rootView.findViewById(R.id.how_to_play_button);
        playButton.setOnClickListener(this::play);
        levelButton.setOnClickListener(this::chooseLevel);
        newGameButton.setOnClickListener(this::startNewGame);
        howToPlayButton.setOnClickListener(this::goToHowToPlayScreen);

        // Drawing droid.
        LinearLayout drawLayout = rootView.findViewById(R.id.droid_draw_view);
        drawLayout.addView(new DroidStartView(getActivity()));
        return rootView;
    }

    private void play(View view) {
        findNavController(view).navigate(R.id.action_start_screen_to_game_screen);
    }

    private void chooseLevel(View view) {
        findNavController(view).navigate(R.id.action_start_screen_to_levels_screen);
    }

    private void startNewGame(View view) {
        findNavController(view).navigate(R.id.action_start_screen_to_game_screen);
    }

    private void goToHowToPlayScreen(View view) {
        findNavController(view).navigate(R.id.action_start_screen_to_how_to_play_screen);
    }

}
