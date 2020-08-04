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

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import java.util.Objects;

/**
 * Displays Game Screen.
 *
 * @author maksme@google.com
 */
public class GameFragment extends Fragment {

    private GameView gameView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point screen = new Point();
        Activity activity = getActivity();
        WindowManager windowManager = Objects.requireNonNull(activity).getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();

        // Writing size to the screen variable
        defaultDisplay.getSize(screen);

        // Extracting screen coordinates
        int screenX = screen.x;
        int screenY = screen.y;

        // Initializing gameView
        gameView = new GameView(getActivity(), screenX, screenY, /* isPlaying= */ true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return gameView;
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
