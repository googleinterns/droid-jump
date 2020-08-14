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
import androidx.appcompat.app.AppCompatActivity;
import com.google.droidjump.leveldata.LevelConfig;
import com.google.droidjump.leveldata.LevelConfigParser;
import com.google.droidjump.leveldata.LevelStrategy;
import java.util.ArrayList;

/**
 * Represents main activity.
 */
public class MainActivity extends AppCompatActivity {
    private int levelsLastIndex;
    private SharedPreferences gameData;
    private SharedPreferences.Editor gameDataEditor;
    private ArrayList<LevelConfig> gameLevels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameData = getSharedPreferences(GAME_VIEW_DATA, Context.MODE_PRIVATE);
        gameDataEditor = gameData.edit();
        gameLevels = LevelConfigParser.getLevelConfigsFromResource(R.raw.level_configs, this);
        levelsLastIndex = gameLevels.size() - 1;
        setContentView(R.layout.main_activity);
    }

    public int getLevelsLastIndex() {
        return levelsLastIndex;
    }

    public void onCurrentLevelCompleted() {
        int currentLevelIndex = getCurrentLevelIndex();
        int lastLevel = getLastLevelIndex();
        if (currentLevelIndex < levelsLastIndex) {
            // Increasing the current level
            gameDataEditor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, ++currentLevelIndex);
            // Increasing the last level
            if (currentLevelIndex > lastLevel) {
                gameDataEditor.putInt(GAME_VIEW_LAST_LEVEL_STRING, currentLevelIndex);
            }
            gameDataEditor.apply();
        }
    }

    public void setCurrentLevelIndex(int currentLevel) {
        gameDataEditor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, currentLevel);
        gameDataEditor.apply();
    }

    public void resetGameData() {
        SharedPreferences.Editor editor = gameData.edit();
        editor.putInt(GAME_VIEW_CURRENT_LEVEL_STRING, FIRST_LEVEL_ID);
        editor.putInt(GAME_VIEW_LAST_LEVEL_STRING, FIRST_LEVEL_ID);
        editor.apply();
    }

    public int getCurrentLevelIndex() {
        return gameData.getInt(GAME_VIEW_CURRENT_LEVEL_STRING, FIRST_LEVEL_ID);
    }

    public int getLastLevelIndex() {
        return gameData.getInt(GAME_VIEW_LAST_LEVEL_STRING, FIRST_LEVEL_ID);
    }

    public LevelStrategy getCurrentLevelStrategy() {
        return gameLevels.get(getCurrentLevelIndex()).getLevelStrategy();
    }

    public String getCurrentLevelName(){
        return gameLevels.get(getCurrentLevelIndex()).getLevelName();
    }

    public ArrayList<LevelConfig> getGameLevels(){
        return (ArrayList<LevelConfig>) gameLevels.clone();
    }
}
