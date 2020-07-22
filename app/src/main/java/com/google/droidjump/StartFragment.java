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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.navigation.Navigation;

public class StartFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);
        Button playButton = rootView.findViewById(R.id.play_button);
        Button levelButton = rootView.findViewById(R.id.level_button);
        Button newGameButton = rootView.findViewById(R.id.new_game_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(view);
            }
        });
        levelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_level(view);
            }
        });
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_new_game(view);
            }
        });
        return rootView;
    }

    public void play(View view) {
        Navigation.findNavController(view).navigate(R.id.action_startFragment_to_gameFragment);
    }

    public void choose_level(View view) {
        Navigation.findNavController(view).navigate(R.id.action_startFragment_to_levelsFragment);
    }

    public void start_new_game(View view) {
        Navigation.findNavController(view).navigate(R.id.action_startFragment_to_gameFragment);
    }

}
