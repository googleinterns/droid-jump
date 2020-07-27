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
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.droidjump.databinding.GameFailureScreenBinding;

public class GameFailureFragment extends Fragment {
    GameFailureScreenBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GameFailureScreenBinding.inflate(getLayoutInflater());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = binding.getRoot();
        FloatingActionButton retryButton = binding.retryButton;
        retryButton.setOnClickListener(view -> {
            findNavController(view).navigate(R.id.action_game_failure_screen_to_game_screen);
        });
        ImageButton menuButton = binding.successMenuButton;
        menuButton.setOnClickListener(view -> {
            findNavController(view).navigate(R.id.action_game_failure_screen_to_start_screen);
        });

        // Drawing a a droid
        LinearLayout drawLayout = binding.droidDrawView;
        drawLayout.addView(new DroidStartView(this.getActivity()));

        // Adding a navigation to howToPlay screen
        FloatingActionButton howToPlayButton = binding.howToPlayButton;
        howToPlayButton.setOnClickListener(view -> {
            findNavController(view).navigate(R.id.action_game_failure_screen_to_how_to_play_screen);
        });

        return rootView;
    }
}
