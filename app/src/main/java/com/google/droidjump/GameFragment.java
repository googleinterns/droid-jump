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

public class GameFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.game_screen, container, false);
        Button winButton = rootView.findViewById(R.id.win_button);
        Button loseButton = rootView.findViewById(R.id.lose_button);
        winButton.setOnClickListener((View view) -> {
            Navigation.findNavController(view).navigate(R.id.action_game_screen_to_game_success_screen);
        });
        loseButton.setOnClickListener((View view) -> {
            Navigation.findNavController(view).navigate(R.id.action_game_screen_to_game_failure_screen);
        });
        return rootView;
    }
}
