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

import static com.google.droidjump.GameConstants.GAME_VIEW_CURRENT_LEVEL_STRING;
import static com.google.droidjump.GameConstants.GAME_VIEW_DATA;
import static com.google.droidjump.GameConstants.GAME_VIEW_LAST_LEVEL_STRING;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Represents main activity.
 */
public class MainActivity extends AppCompatActivity {

    private int levelsCount;
    private int currentLevel; // the recent level the user played
    private int lastLevel; // The biggest level the user played
    private SharedPreferences gameData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameData = getSharedPreferences(GAME_VIEW_DATA, Context.MODE_PRIVATE);
        levelsCount = GameConstants.GAME_LEVELS_COUNT;
        currentLevel = getCurrentLevelFromSharedPreferences();
        lastLevel = getLastLevelFromSharedPreferences();
        setContentView(R.layout.main_activity);
    }

    public int getLevelsCount() {
        return levelsCount;
    }

    public int getCurrentLevelFromSharedPreferences() {
        return gameData.getInt(GAME_VIEW_CURRENT_LEVEL_STRING, /* defValue= */ 1);
    }

    public int getLastLevelFromSharedPreferences() {
        return gameData.getInt(GAME_VIEW_LAST_LEVEL_STRING, /* defValue= */ getCurrentLevel());
    }

    public void increaseLevel() {
        if (currentLevel < levelsCount) {
            SharedPreferences.Editor editor = gameData.edit();
            // Increasing the current level
            editor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, ++currentLevel);
            // Increasing the last level
            if (currentLevel > lastLevel) {
                lastLevel = currentLevel;
                editor.putInt(GAME_VIEW_LAST_LEVEL_STRING, currentLevel);
            }
            editor.apply();
        }
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void setLastLevel(int lastLevel) {
        this.lastLevel = lastLevel;
    }

    public void resetGameData() {
        currentLevel = 1;
        lastLevel = 1;
        SharedPreferences.Editor editor = gameData.edit();
        editor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, 1);
        editor.putInt(GAME_VIEW_LAST_LEVEL_STRING, 1);
        editor.apply();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getLastLevel() {
        return lastLevel;
    }
}
