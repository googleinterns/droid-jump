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

public class MainActivity extends AppCompatActivity {

    private int levelsCount;
    private int currentLevel;
    private SharedPreferences gameData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameData = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        levelsCount = 100;
        currentLevel = getCurrentLevelFromSharedPreferences();
        setContentView(R.layout.main_activity);

    }

    public int getLevelsCount() {
        return levelsCount;
    }

    public int getCurrentLevelFromSharedPreferences() {
        return gameData.getInt("level", /* defValue= */1);
    }

    public void increaseCurrentLevel() {
        if (currentLevel < levelsCount) {
            SharedPreferences.Editor editor = gameData.edit();
            editor.putInt("level", ++currentLevel);
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
