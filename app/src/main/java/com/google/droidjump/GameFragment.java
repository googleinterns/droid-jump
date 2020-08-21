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

import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.droidjump.models.NavigationHelper;

/**
 * Displays Game Screen.
 */
public class GameFragment extends Fragment {
    private GameView gameView;
    private FragmentActivity activity;
    private ConstraintLayout pauseLayout;
    private ImageButton menuButton;

    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point screen = new Point();
        activity = getActivity();
        activity.getWindowManager().getDefaultDisplay().getSize(screen);
        gameView = new GameView(activity, screen.x, screen.y, /* isPlaying= */ true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_screen, container, /* attachToRoot= */ false);
        ((LinearLayout) rootView.findViewById(R.id.game_layout)).addView(gameView);
        menuButton = rootView.findViewById(R.id.menu_button);
        pauseLayout = rootView.findViewById(R.id.pause_layout);
        menuButton.setOnClickListener(ignored -> onPause());
        rootView.findViewById(R.id.play_button).setOnClickListener(ignored -> onResume());
        rootView.findViewById(R.id.restart_button).setOnClickListener(ignored ->
                NavigationHelper.navigateToFragment(activity, new GameFragment()));
        rootView.findViewById(R.id.go_to_menu_button).setOnClickListener(ignored ->
                NavigationHelper.navigateToFragment(activity, new StartFragment()));
        activity.getOnBackPressedDispatcher().addCallback(activity, new OnBackPressedCallback(/* enabled= */ true) {
            @Override
            public void handleOnBackPressed() {
                onPause();
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        gameView.pause();
        menuButton.setVisibility(View.GONE);
        pauseLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        menuButton.setVisibility(View.VISIBLE);
        pauseLayout.setVisibility(View.GONE);
        gameView.resume();
    }
}
