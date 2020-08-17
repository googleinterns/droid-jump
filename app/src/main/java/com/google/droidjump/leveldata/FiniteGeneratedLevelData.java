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

import static com.google.droidjump.leveldata.JSONKeys.BASE_SPEED_KEY;
import static com.google.droidjump.leveldata.JSONKeys.OBSTACLES_LIMIT_KEY;
import android.content.res.Resources;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Manages finite generated level data.
 */
public class FiniteGeneratedLevelData implements LevelStrategy {
    ObstacleData currentObstacle;
    int baseSpeed;
    int obstaclesLimit;
    int obstaclesCount;

    public FiniteGeneratedLevelData(int fileId, Resources resources) {
        getDataFromFile(fileId, resources);
        currentObstacle = LevelGenerator.generateNextObstacle();
        obstaclesCount = 0;
    }

    private void getDataFromFile(int fileId, Resources resources) {
        JSONObject leveldata = JSONReader.getJSONObjectFromResource(fileId, resources);
        try {
            baseSpeed = leveldata.getInt(BASE_SPEED_KEY);
            obstaclesLimit = leveldata.getInt(OBSTACLES_LIMIT_KEY);
        } catch (JSONException e) {
            Log.e(FiniteGeneratedLevelData.class.getName(), "Failed to get data from JSONObject: " + e.getMessage());
        }
    }

    @Override
    public int getCurrentTimeInterval() {
        return currentObstacle.getInterval();
    }

    @Override
    public ObstacleType getNewObstacleType() {
        if (obstaclesCount == obstaclesLimit) {
            return null;
        }
        ObstacleType newObstacleType = currentObstacle.getType();
        currentObstacle = LevelGenerator.generateNextObstacle();
        obstaclesCount++;
        return newObstacleType;
    }

    @Override
    public boolean isEmpty() {
        return obstaclesCount == obstaclesLimit;
    }

    @Override
    public int getBaseSpeed() {
        return baseSpeed;
    }
}
