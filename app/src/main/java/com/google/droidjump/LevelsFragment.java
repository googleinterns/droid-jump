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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.droidjump.models.LevelManager;
import java.util.Objects;

/**
 * Displays Levels Screen.
 */
public class LevelsFragment extends Fragment {
    private MainActivity activity;
    private LevelsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = Objects.requireNonNull((MainActivity) getActivity());
        Context context = getContext();
        adapter = new LevelsAdapter(Objects.requireNonNull(context), LevelManager.getGameLevels(),
                LevelManager.getLastLevelIndex());
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.levels_screen, container,
                /* attachToRoot= */ false);

        // Finding gridView and putting levels to it.
        GridView gridView = rootView.findViewById(R.id.levels_grid_view);
        gridView.setAdapter(adapter);

        // Adding onClick events.
        gridView.setOnItemClickListener((adapterView, view, index, ignored) -> {
            int levelIndex = (int) adapter.getItem(index);
            if (LevelManager.getLastLevelIndex() >= levelIndex) {
                LevelManager.setCurrentLevelIndex(levelIndex);
                findNavController(view).navigate(
                        R.id.action_levels_screen_to_game_screen);
            }
        });

        // Redirecting on click to a start screen.
        ImageButton menuButton = rootView.findViewById(R.id.menu_button);
        menuButton.setOnClickListener(view -> {
            activity.onBackPressed();
        });
        return rootView;
    }
}
