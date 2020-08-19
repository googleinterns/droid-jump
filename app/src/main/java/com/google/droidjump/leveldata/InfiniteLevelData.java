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
import android.content.res.Resources;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages infinite level data.
 */
public class InfiniteLevelData implements LevelStrategy {
    private static final ObstacleType[] OBSTACLE_TYPES = ObstacleType.values();
    private ObstacleData currentObstacle;
    private int baseSpeed;
    private LevelGenerator levelGenerator;

    public InfiniteLevelData(int fileId, Resources resources) {
        getDataFromFile(fileId, resources);
        currentObstacle = levelGenerator.generateNextObstacle();
    }

    private void getDataFromFile(int fileId, Resources resources) {
        Map<ObstacleType, Integer> frequencies = new HashMap<>();
        JSONObject leveldata = JSONReader.getJSONObjectFromResource(fileId, resources);
        try {
            baseSpeed = leveldata.getInt(BASE_SPEED_KEY);
            JSONObject generationFrequencies = leveldata.getJSONObject(GENERATION_FREQUENCIES_KEY);
            for (ObstacleType obstacleType : OBSTACLE_TYPES) {
                frequencies.put(obstacleType, generationFrequencies.getInt(obstacleType.name()));
            }
        } catch (JSONException e) {
            Log.e(InfiniteLevelData.class.getName(), "Failed to get data from JSONObject: " + e.getMessage());
        }
        levelGenerator = new LevelGenerator(frequencies);
    }

    @Override
    public int getCurrentTimeInterval() {
        return currentObstacle.getInterval();
    }

    @Override
    public ObstacleType getNewObstacleType() {
        ObstacleType newObstacleType = currentObstacle.getType();
        currentObstacle = levelGenerator.generateNextObstacle();
        return newObstacleType;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getBaseSpeed() {
        return baseSpeed;
    }
}
