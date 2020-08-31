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
import static com.google.droidjump.GameConstants.GAME_TIME;
import static com.google.droidjump.GameConstants.GAME_VIEW_CURRENT_LEVEL_STRING;
import static com.google.droidjump.GameConstants.GAME_VIEW_DATA;
import static com.google.droidjump.GameConstants.GAME_VIEW_LAST_LEVEL_STRING;
import static com.google.droidjump.GameConstants.INFINITE_LEVEL_MAX_SCORE;
import static com.google.droidjump.GameConstants.SCORE_DEF_VALUE;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.droidjump.R;
import com.google.droidjump.leveldata.LevelConfig;
import com.google.droidjump.leveldata.LevelConfigParser;
import com.google.droidjump.leveldata.LevelStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the game data.
 */
public class LevelManager {
    private static int levelsLastIndex;
    private static SharedPreferences gameData;
    private static SharedPreferences.Editor gameDataEditor;
    private static ArrayList<LevelConfig> gameLevels;
    private static int currentLevelScore;

    public static void init(Context context) {
        gameData = context.getSharedPreferences(GAME_VIEW_DATA, Context.MODE_PRIVATE);
        gameDataEditor = gameData.edit();
        gameLevels = LevelConfigParser.getLevelConfigsFromResource(R.raw.level_configs, context);
        levelsLastIndex = gameLevels.size() - 1;
        currentLevelScore = 0;
    }

    public static int getLevelsLastIndex() {
        return levelsLastIndex;
    }

    public static void setLevelsLastIndex(int levelsLastIndex) {
        LevelManager.levelsLastIndex = levelsLastIndex;
    }

    public static void onCurrentLevelCompleted() {
        int currentLevelIndex = getCurrentLevelIndex();
        int lastLevel = getLastLevelIndex();
        if (currentLevelIndex < levelsLastIndex) {
            setCurrentLevelIndex(++currentLevelIndex);
            if (currentLevelIndex > lastLevel) {
                setLastLevelIndex(currentLevelIndex);
            }
            gameDataEditor.apply();
        }
    }

    public static void resetGameData() {
        gameDataEditor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, FIRST_LEVEL_ID);
        gameDataEditor.putInt(GAME_VIEW_LAST_LEVEL_STRING, FIRST_LEVEL_ID);
        gameDataEditor.putInt(INFINITE_LEVEL_MAX_SCORE, SCORE_DEF_VALUE);
        gameDataEditor.putInt(GAME_TIME, SCORE_DEF_VALUE);
        for (int levelIndex = 0; levelIndex <= levelsLastIndex; levelIndex++) {
            gameDataEditor.putInt(String.valueOf(levelIndex), SCORE_DEF_VALUE);
        }
        gameDataEditor.apply();
    }

    public static int getCurrentLevelIndex() {
        return gameData.getInt(GAME_VIEW_CURRENT_LEVEL_STRING, FIRST_LEVEL_ID);
    }

    public static void setCurrentLevelIndex(int currentLevelIndex) {
        gameDataEditor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, currentLevelIndex);
        gameDataEditor.apply();
    }

    public static int getLastLevelIndex() {
        return gameData.getInt(GAME_VIEW_LAST_LEVEL_STRING, FIRST_LEVEL_ID);
    }

    public static void setLastLevelIndex(int lastLevelIndexLevel) {
        gameDataEditor.putInt(GAME_VIEW_LAST_LEVEL_STRING, lastLevelIndexLevel);
        gameDataEditor.apply();
    }

    public static LevelStrategy getCurrentLevelStrategy() {
        return gameLevels.get(getCurrentLevelIndex()).getLevelStrategy();
    }

    public static int getLevelMaxScore(int index) {
        return gameData.getInt(String.valueOf(index), SCORE_DEF_VALUE);
    }

    public static String getCurrentLevelName() {
        return gameLevels.get(getCurrentLevelIndex()).getLevelName();
    }

    public static List<LevelConfig> getGameLevels() {
        return Collections.unmodifiableList(gameLevels);
    }

    public static int getCurrentLevelScore() {
        return currentLevelScore;
    }

    public static void setCurrentLevelScore(int score) {
        currentLevelScore = score;
    }

    public static void updateCurrentLevelMaxScore() {
        if (getLevelMaxScore(getCurrentLevelIndex()) < currentLevelScore) {
            gameDataEditor.putInt(String.valueOf(getCurrentLevelIndex()), currentLevelScore);
            gameDataEditor.apply();
        }
    }
}
