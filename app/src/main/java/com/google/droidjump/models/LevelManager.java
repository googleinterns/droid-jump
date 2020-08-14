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

package com.google.droidjump.models;

import static com.google.droidjump.GameConstants.FIRST_LEVEL_ID;
import static com.google.droidjump.GameConstants.GAME_LEVELS_COUNT;
import static com.google.droidjump.GameConstants.GAME_VIEW_CURRENT_LEVEL_STRING;
import static com.google.droidjump.GameConstants.GAME_VIEW_DATA;
import static com.google.droidjump.GameConstants.GAME_VIEW_LAST_LEVEL_STRING;

import android.content.Context;
import android.content.SharedPreferences;

public class LevelManager {
    private static int levelsCount;
    private static SharedPreferences gameData;
    private static SharedPreferences.Editor gameDataEditor;

    public static void init(Context context) {
        gameData = context.getSharedPreferences(GAME_VIEW_DATA, Context.MODE_PRIVATE);
        gameDataEditor = gameData.edit();
        levelsCount = extractLevelsCount();
    }

    public static int getLevelsCount() {
        return levelsCount;
    }

    private static int extractLevelsCount() {
        // TODO(dnikolskaia) Extract data from a real JSON file.
        return GAME_LEVELS_COUNT;
    }

    public static void onCurrentLevelCompleted() {
        int currentLevel = getCurrentLevel();
        int lastLevel = getLastLevel();
        if (currentLevel < levelsCount) {
            // Increasing the current level
            gameDataEditor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, ++currentLevel);
            gameDataEditor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, ++currentLevel);
            if (currentLevel > lastLevel) {
                gameDataEditor.putInt(GAME_VIEW_LAST_LEVEL_STRING, currentLevel);
            }
            gameDataEditor.apply();
        }
    }

    public static void setCurrentLevel(int currentLevel) {
        gameDataEditor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, currentLevel);
        gameDataEditor.apply();
    }

    public static void setLastLevel(int lastLevel) {
        gameDataEditor.putInt(GAME_VIEW_LAST_LEVEL_STRING, lastLevel);
        gameDataEditor.apply();
    }

    public static void resetGameData() {
        SharedPreferences.Editor editor = gameData.edit();
        editor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, FIRST_LEVEL_ID);
        editor.putInt(GAME_VIEW_LAST_LEVEL_STRING, FIRST_LEVEL_ID);
        editor.apply();
    }

    public static int getCurrentLevel() {
        return gameData.getInt(GAME_VIEW_CURRENT_LEVEL_STRING, FIRST_LEVEL_ID);
    }

    public static int getLastLevel() {
        return gameData.getInt(GAME_VIEW_LAST_LEVEL_STRING, FIRST_LEVEL_ID);
    }
}
