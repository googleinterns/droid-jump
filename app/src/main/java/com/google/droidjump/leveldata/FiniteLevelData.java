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
import static com.google.droidjump.leveldata.JSONKeys.INTERVAL_KEY;
import static com.google.droidjump.leveldata.JSONKeys.TIMELINE_KEY;
import static com.google.droidjump.leveldata.JSONKeys.TYPE_KEY;
import android.content.res.Resources;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedList;

/**
 * Manages finite level data.
 */
public class FiniteLevelData implements LevelStrategy {
    private int baseSpeed;
    private LinkedList<ObstacleData> obstaclesData;

    public FiniteLevelData(int fileId, Resources resources) {
        obstaclesData = new LinkedList<>();
        getDataFromFile(fileId, resources);
    }

    private void getDataFromFile(int fileId, Resources resources) {
        JSONObject leveldata = JSONReader.getJSONObjectFromResource(fileId, resources);
        try {
            baseSpeed = leveldata.getInt(BASE_SPEED_KEY);
            JSONArray timeline = leveldata.getJSONArray(TIMELINE_KEY);
            JSONObject currentObject;
            int interval;
            ObstacleType type;
            for (int i = 0; i < timeline.length(); i++) {
                currentObject = timeline.getJSONObject(i);
                interval = currentObject.getInt(INTERVAL_KEY);
                type = Enum.valueOf(ObstacleType.class, currentObject.getString(TYPE_KEY));
                obstaclesData.add(new ObstacleData(interval, type));
            }
        } catch (JSONException e) {
            Log.e(FiniteLevelData.class.getName(), "Failed to get data from JSONObject: " + e.getMessage());
        }
    }

    @Override
    public int getCurrentTimeInterval() {
        return obstaclesData.getFirst().getInterval();
    }

    @Override
    public ObstacleType getNewObstacleType() {
        return obstaclesData.removeFirst().getType();
    }

    @Override
    public int getBaseSpeed() {
        return baseSpeed;
    }

    @Override
    public boolean isEmpty() {
        return obstaclesData.isEmpty();
    }
}
