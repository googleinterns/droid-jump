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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private int currentLevel;
    private int levelCount;
    private SharedPreferences gameData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        levelCount = 10;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        gameData = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        currentLevel = gameData.getInt("level", 1);
        setContentView(R.layout.main_activity);
    }

    public void increaseLevel() {
        if (currentLevel < levelCount) {
            currentLevel += 1;
            SharedPreferences.Editor editor = gameData.edit();
            editor.putInt("level", currentLevel);
            editor.apply();
        }
    }

    public void resetGameData() {
        currentLevel = 1;
        SharedPreferences.Editor editor = gameData.edit();
        editor.putInt("level", 1);
        editor.apply();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
