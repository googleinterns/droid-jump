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
import static com.google.droidjump.leveldata.JSONKeys.GENERATION_FREQUENCIES_KEY;
import static com.google.droidjump.leveldata.JSONKeys.OBSTACLES_LIMIT_KEY;
import android.content.res.Resources;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages finite generated level data.
 */
public class FiniteGeneratedLevelData implements LevelStrategy {
    private static final ObstacleType[] OBSTACLE_TYPES = ObstacleType.values();
    private ObstacleData currentObstacle;
    private int baseSpeed;
    private int obstaclesLimit;
    private int obstaclesCount;
    private LevelGenerator levelGenerator;

    public FiniteGeneratedLevelData(int fileId, Resources resources) {
        getDataFromFile(fileId, resources);
        currentObstacle = levelGenerator.generateNextObstacle();
        obstaclesCount = 0;
    }

    private void getDataFromFile(int fileId, Resources resources) {
        Map<ObstacleType, Integer> frequencies = new HashMap<>();
        JSONObject leveldata = JSONReader.getJSONObjectFromResource(fileId, resources);
        try {
            baseSpeed = leveldata.getInt(BASE_SPEED_KEY);
            obstaclesLimit = leveldata.getInt(OBSTACLES_LIMIT_KEY);
            JSONObject generationFrequencies = leveldata.getJSONObject(GENERATION_FREQUENCIES_KEY);
            for (ObstacleType obstacleType : OBSTACLE_TYPES) {
                frequencies.put(obstacleType, generationFrequencies.getInt(obstacleType.name()));
            }
        } catch (JSONException e) {
            Log.e(FiniteGeneratedLevelData.class.getName(), "Failed to get data from JSONObject: " + e.getMessage());
        }
        levelGenerator = new LevelGenerator(frequencies);
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
        currentObstacle = levelGenerator.generateNextObstacle();
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
