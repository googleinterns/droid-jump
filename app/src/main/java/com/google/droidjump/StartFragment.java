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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.droidjump.databinding.StartScreenBinding;

public class StartFragment extends Fragment {
    private StartScreenBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StartScreenBinding.inflate(getLayoutInflater());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = binding.getRoot();
        Button playButton = binding.playButton;
        Button levelButton = binding.levelButton;
        Button newGameButton = binding.newGameButton;
        FloatingActionButton howToPlayButton = binding.howToPlayButton;
        playButton.setOnClickListener(this::play);
        levelButton.setOnClickListener(this::chooseLevel);
        newGameButton.setOnClickListener(this::startNewGame);
        howToPlayButton.setOnClickListener(view -> {
            findNavController(view).navigate(R.id.action_start_screen_to_how_to_play_screen);
        });

        // Drawing a droid
        LinearLayout drawLayout = binding.droidDrawView;
        drawLayout.addView(new DroidStartView(this.getActivity()));
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
}
