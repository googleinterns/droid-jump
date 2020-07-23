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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.navigation.Navigation;

import org.xmlpull.v1.XmlPullParser;

public class StartFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_start, container, false);
        Button playButton = rootView.findViewById(R.id.play_button);
        Button levelButton = rootView.findViewById(R.id.level_button);
        Button newGameButton = rootView.findViewById(R.id.new_game_button);
        FloatingActionButton howToPlayButton = rootView.findViewById(R.id.how_to_play_button);
        LinearLayout startDrawView = rootView.findViewById(R.id.start_draw_view);
        startDrawView.addView(new DroidStartView(getContext()));
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(view);
            }
        });
        levelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseLevel(view);
            }
        });
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGame(view);
            }
        });
        howToPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_startFragment_to_howToPlayFragment);
            }
        });

        return rootView;
    }

    public void play(View view) {
        Navigation.findNavController(view).navigate(R.id.action_startFragment_to_gameFragment);
    }

    public void chooseLevel(View view) {
        Navigation.findNavController(view).navigate(R.id.action_startFragment_to_levelsFragment);
    }

    public void startNewGame(View view) {
        Navigation.findNavController(view).navigate(R.id.action_startFragment_to_gameFragment);
    }

}
