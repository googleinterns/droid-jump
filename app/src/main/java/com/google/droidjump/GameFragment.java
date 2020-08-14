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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.droidjump.models.NavigationHelper;
import java.util.Objects;

/**
 * Displays Game Screen.
 */
public class GameFragment extends Fragment {
    private GameView gameView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point screen = new Point();
        FragmentActivity activity = getActivity();
        WindowManager windowManager = Objects.requireNonNull(activity).getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();

        // Writing size to the screen variable.
        defaultDisplay.getSize(screen);
        gameView = new GameView(activity, screen.x, screen.y, /* isPlaying= */ true);

        NavigationHelper.addOnBackPressedEventListener(activity, new StartFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_screen, container, /* attachToRoot= */ false);
        ConstraintLayout layout = rootView.findViewById(R.id.game_layout);
        layout.addView(gameView);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        gameView.resume();
    }
}
