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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.navigation.Navigation;

import org.xmlpull.v1.XmlPullParser;

public class GameFragment extends Fragment {
    private GameView gameView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Point screen = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(screen);

        gameView = new GameView(getActivity(), screen.x, screen.y,true);
        return gameView;
//        View rootView = inflater.inflate((XmlPullParser) gameView, container, false);
//        Button winButton = rootView.findViewById(R.id.win_button);
//        Button loseButton = rootView.findViewById(R.id.lose_button);
//        winButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Navigation.findNavController(view).navigate(R.id.action_gameFragment_to_gameSuccessFragment);
//            }
//        });
//        loseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Navigation.findNavController(view).navigate(R.id.action_gameFragment_to_gameFailureFragment);
//            }
//        });
//
//
//        return rootView;
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
