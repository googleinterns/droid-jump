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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_failure_screen,
                container, /* attachToRoot= */ false);
        // Redirecting on click to game screen.
        FloatingActionButton retryButton = rootView.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(view -> {
            NavigationHelper.navigateToFragment(activity, new GameFragment());
        });

        // Redirecting on click to start screen.
        ImageButton menuButton = rootView.findViewById(R.id.menu_button);
        menuButton.setOnClickListener(view -> {
            NavigationHelper.navigateToFragment(activity, new StartFragment());
        });

        // Drawing droid.
        LinearLayout drawLayout = rootView.findViewById(R.id.droid_draw_view);
        drawLayout.addView(new DroidStartView(getActivity()));

        // Redirecting on click to How To Play screen.
        FloatingActionButton howToPlayButton = rootView.findViewById(R.id.how_to_play_button);
        howToPlayButton.setOnClickListener(view -> {
            NavigationHelper.navigateToFragment(activity, new HowToPlayFragment());
        });
        NavigationHelper.addOnBackPressedEventListener(activity, new StartFragment());
        return rootView;
    }
}
