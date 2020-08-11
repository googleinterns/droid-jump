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

import android.content.res.Resources;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Managing finite level data.
 */
public class LevelData {
    final static String BASE_SPEED_KEY = "baseSpeed";
    final static String TIMELINE_KEY = "timeline";
    final static String INTERVAL_KEY = "interval";
    final static String TYPE_KEY = "type";

    private int baseSpeed;
    private LinkedList<ObstacleData> obstaclesData;

    public LevelData(Level level, Resources resources) {
        obstaclesData = new LinkedList<>();
        getDataFromFile(level, resources);
    }

    private void getDataFromFile(Level level, Resources resources) {
        try {
            JSONObject leveldata = new JSONObject(getJSONStringFromResource(level.fileId, resources));
            baseSpeed = leveldata.getInt(BASE_SPEED_KEY);
            JSONArray timeline = leveldata.getJSONArray(TIMELINE_KEY);
            for (int i = 0; i < timeline.length(); i++) {
                JSONObject currentObject = timeline.getJSONObject(i);
                int interval = currentObject.getInt(INTERVAL_KEY);
                ObstacleType type = Enum.valueOf(ObstacleType.class, currentObject.getString(TYPE_KEY));
                obstaclesData.add(new ObstacleData(interval, type));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getJSONStringFromResource(int fileID, Resources resources) {
        String jsonString = "";
        try {
            InputStream is = resources.openRawResource(fileID);
            Scanner scanner = new Scanner(is);
            jsonString = scanner.useDelimiter("\\A").next();
            is.close();
        } catch (Resources.NotFoundException | IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public int getCurrentTimeInterval() {
        return obstaclesData.getFirst().getInterval();
    }

    public ObstacleType getNewObstacleType() {
        return obstaclesData.removeFirst().getType();
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }

    public boolean isEmpty() {
        return obstaclesData.isEmpty();
    }
}
