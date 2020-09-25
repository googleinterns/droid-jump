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

import com.google.droidjump.models.LevelManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class SavedGameManager {
    private static String LAST_LEVEL_INDEX_KEY = "lastLevelIndex";
    private static String MAX_SCORES_KEY = "maxScores";

    public static byte[] getGameData() {
        JSONObject gameData = new JSONObject();
        try {
            gameData.put(LAST_LEVEL_INDEX_KEY, LevelManager.getLastLevelIndex());
            gameData.put(MAX_SCORES_KEY, new JSONArray(LevelManager.getAllMaxScores()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gameData.toString().getBytes();
    }

    public static void setGameData(byte[] data) {
        try {
            JSONObject gameData = new JSONObject(new String(data));
            int lastLevelIndex = gameData.getInt(LAST_LEVEL_INDEX_KEY);
            JSONArray scores = gameData.getJSONArray(MAX_SCORES_KEY);
            ArrayList<Long> maxScores = new ArrayList<>();
            for (int i = 0; i < scores.length(); i++) {
                maxScores.add(scores.getLong(i));
            }
            LevelManager.setGameData(lastLevelIndex, maxScores);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
