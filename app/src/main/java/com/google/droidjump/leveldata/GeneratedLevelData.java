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
 * Super class for generated levels.
 **/
abstract public class GeneratedLevelData implements LevelStrategy{
    protected static final ObstacleType[] OBSTACLE_TYPES = ObstacleType.values();
    protected ObstacleData currentObstacle;
    protected int baseSpeed;
    protected LevelGenerator levelGenerator;
    protected JSONObject leveldata;

    GeneratedLevelData(int fileId, Resources resources){
        getDataFromFile(fileId, resources);
        currentObstacle = levelGenerator.generateNextObstacle();
    }

    private void getDataFromFile(int fileId, Resources resources) {
        Map<ObstacleType, Integer> frequencies = new HashMap<>();
        leveldata = JSONReader.getJSONObjectFromResource(fileId, resources);
        try {
            baseSpeed = leveldata.getInt(BASE_SPEED_KEY);
            JSONObject generationFrequencies = leveldata.getJSONObject(GENERATION_FREQUENCIES_KEY);
            for (ObstacleType obstacleType : OBSTACLE_TYPES) {
                frequencies.put(obstacleType, generationFrequencies.getInt(obstacleType.name()));
            }
        } catch (JSONException e) {
            Log.e(GeneratedLevelData.class.getName(), "Failed to get data from JSONObject: " + e.getMessage());
        }
        levelGenerator = new LevelGenerator(frequencies);
    }

    protected JSONObject getJSONObject(){
        return leveldata;
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
    public int getBaseSpeed() {
        return baseSpeed;
    }
}
