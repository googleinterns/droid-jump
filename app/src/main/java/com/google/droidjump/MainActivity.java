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

import static com.google.droidjump.GameConstants.FIRST_LEVEL_ID;
import static com.google.droidjump.GameConstants.GAME_VIEW_CURRENT_LEVEL_STRING;
import static com.google.droidjump.GameConstants.GAME_VIEW_DATA;
import static com.google.droidjump.GameConstants.GAME_VIEW_LAST_LEVEL_STRING;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

/**
 * Represents main activity.
 */
public class MainActivity extends FragmentActivity {
    private int levelsCount;
    private SharedPreferences gameData;
    private SharedPreferences.Editor gameDataEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameData = getSharedPreferences(GAME_VIEW_DATA, Context.MODE_PRIVATE);
        gameDataEditor = gameData.edit();
        levelsCount = GameConstants.GAME_LEVELS_COUNT;
        setContentView(R.layout.main_activity);
    }

    public int getLevelsCount() {
        return levelsCount;
    }

    public void onCurrentLevelCompleted() {
        int currentLevel = getCurrentLevel();
        int lastLevel = getLastLevel();
        if (currentLevel < levelsCount) {
            // Increasing the current level
            gameDataEditor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, ++currentLevel);
            // Increasing the last level
            if (currentLevel > lastLevel) {
                gameDataEditor.putInt(GAME_VIEW_LAST_LEVEL_STRING, currentLevel);
            }
            gameDataEditor.apply();
        }
    }

    public void setCurrentLevel(int currentLevel) {
        gameDataEditor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, currentLevel);
        gameDataEditor.apply();
    }

    public void setLastLevel(int lastLevel) {
        gameDataEditor.putInt(GAME_VIEW_LAST_LEVEL_STRING, lastLevel);
        gameDataEditor.apply();
    }

    public void resetGameData() {
        SharedPreferences.Editor editor = gameData.edit();
        editor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, FIRST_LEVEL_ID);
        editor.putInt(GAME_VIEW_LAST_LEVEL_STRING, FIRST_LEVEL_ID);
        editor.apply();
    }

    public int getCurrentLevel() {
        return gameData.getInt(GAME_VIEW_CURRENT_LEVEL_STRING, FIRST_LEVEL_ID);
    }

    public int getLastLevel() {
        return gameData.getInt(GAME_VIEW_LAST_LEVEL_STRING, FIRST_LEVEL_ID);
    }
}
