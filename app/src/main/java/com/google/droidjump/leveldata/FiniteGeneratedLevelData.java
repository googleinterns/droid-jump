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

package com.google.droidjump.leveldata;

import static com.google.droidjump.leveldata.JSONKeys.OBSTACLES_LIMIT_KEY;
import android.content.res.Resources;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Manages finite generated level data.
 */
public class FiniteGeneratedLevelData extends GeneratedLevelData{
    private int obstaclesLimit;
    private int obstaclesCount;

    public FiniteGeneratedLevelData(int fileId, Resources resources) {
        super(fileId, resources);
        obstaclesCount = 0;
        JSONObject leveldata = super.getJSONObject();
        try {
            obstaclesLimit = leveldata.getInt(OBSTACLES_LIMIT_KEY);
        } catch (JSONException e) {
            Log.e(FiniteGeneratedLevelData.class.getName(), "Failed to get data from JSONObject: " + e.getMessage());
        }
    }

    @Override
    public ObstacleType getNewObstacleType() {
        if (obstaclesCount == obstaclesLimit) {
            return null;
        }
        obstaclesCount++;
        return super.getNewObstacleType();
    }

    @Override
    public boolean isEmpty() {
        return obstaclesCount == obstaclesLimit;
    }
}
