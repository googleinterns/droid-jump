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

import static com.google.droidjump.GameConstants.GAME_VIEW_DATA;
import static com.google.droidjump.GameConstants.SCORE_DEF_VALUE;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.droidjump.GameConstants;
import com.google.droidjump.MainActivity;

public class ScoreManager {
    private static SharedPreferences gameData;
    private static SharedPreferences.Editor gameDataEditor;
    private static MainActivity activity;

    public static void init(Context context) {
        activity = (MainActivity) context;
        gameData = context.getSharedPreferences(GAME_VIEW_DATA, Context.MODE_PRIVATE);
        gameDataEditor = gameData.edit();
    }

    public static long getLocalScoreFromLeaderboard(String leaderboardId) {
        return gameData.getLong(leaderboardId, GameConstants.SCORE_DEF_VALUE);
    }

    public static void clearScores() {
        // Clearing local leaderboard scores.
        for (int leaderboard : GameConstants.LEADERBOARD_LIST) {
            String leaderboardId = activity.getResources().getString(leaderboard);
            gameDataEditor.putLong(leaderboardId, SCORE_DEF_VALUE);
        }

        // Clearing local level scores.
        for (int levelIndex = 0; levelIndex <= LevelManager.getLevelsLastIndex(); levelIndex++) {
            gameDataEditor.putLong(String.valueOf(levelIndex), SCORE_DEF_VALUE);
        }
        gameDataEditor.apply();
    }
}
