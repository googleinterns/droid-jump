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
import android.widget.ImageButton;

public class GameFailureFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_failure_screen, container, /* attachToRoot= */false);
        FloatingActionButton retryButton = rootView.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(view -> {
            findNavController(view).navigate(R.id.action_game_failure_screen_to_game_screen, getArguments());
        });
        ImageButton menuButton = rootView.findViewById(R.id.success_menu_button);
        menuButton.setOnClickListener(view -> {
            findNavController(view).navigate(R.id.action_game_failure_screen_to_start_screen);
        });
        return rootView;
    }
}
