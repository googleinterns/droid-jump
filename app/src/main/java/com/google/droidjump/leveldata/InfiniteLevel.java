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
import android.content.res.Resources;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Manages infinite level data.
 */
public class InfiniteLevel implements LevelStrategy {
    ObstacleData currentObstacle;
    int baseSpeed;

    public InfiniteLevel(Level level, Resources resources) {
        getDataFromFile(level, resources);
        currentObstacle = generateNextObstacle();
    }

    private void getDataFromFile(Level level, Resources resources) {
        JSONObject leveldata = JSONReader.getJSONObjectFromResource(level.fileId, resources);
        try {
            baseSpeed = leveldata.getInt(BASE_SPEED_KEY);
        } catch (JSONException e) {
            Log.e("InfiniteLevel", "Failed to get data from JSONObject: " + e.getMessage());
        }
    }

    @Override
    public int getCurrentTimeInterval() {
        return currentObstacle.getInterval();
    }

    @Override
    public ObstacleType getNewObstacleType() {
        ObstacleType newObstacleType = currentObstacle.getType();
        currentObstacle = generateNextObstacle();
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

    private ObstacleData generateNextObstacle() {
        // TODO(dnikolskaia): Generate obstacle type
        // TODO(dnikolskaia): Generate interval

        // So far returns same obstacles
        return new ObstacleData(40, ObstacleType.cactus);
    }
}
