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

public class LevelData {
    private int baseSpeed;
    private LinkedList<Integer> intervals;
    private LinkedList<String> obstacleTypes;

    public LevelData(Level level, Resources resources){
        intervals = new LinkedList<>();
        obstacleTypes = new LinkedList<>();
        try {
            JSONObject leveldata = new JSONObject(getJSONStringFromResource(level.fileID, resources));
            baseSpeed = leveldata.getInt("baseSpeed");
            JSONArray timeline = leveldata.getJSONArray("timeline");
            for(int i = 0; i < timeline.length(); i++){
                JSONObject curObject = timeline.getJSONObject(i);
                intervals.add(curObject.getInt("interval"));
                obstacleTypes.add(curObject.getString("type"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getJSONStringFromResource(int fileID, Resources resources){
        String JSONString = "";
        try{
            InputStream is = resources.openRawResource(fileID);
            Scanner scanner = new Scanner(is);
            JSONString = scanner.useDelimiter("\\A").next();
            is.close();
        } catch (Resources.NotFoundException | IOException e) {
            e.printStackTrace();
        }

        return JSONString;
    }

    public int getCurTimeInterval(){
            return intervals.getFirst();
    }

    public String getNewObstacleType(){
        String newObstacleType = obstacleTypes.removeFirst();
        intervals.removeFirst();
        return newObstacleType;
    }

    public int getBaseSpeed(){
        return baseSpeed;
    }

    public boolean isEmpty(){
        return intervals.isEmpty();
    }

}
