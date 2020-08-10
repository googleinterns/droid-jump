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

import android.content.res.Resources;
import java.util.List;

/**
 * Represents a game level.
 */
public class Level {

    private int number;
    private List<String> obstacles;
    private List<Integer> intervals;

    public Level(int number, Resources resources, String packageName) {
        this.number = number;
        getDataFromFile(resources, packageName);
    }

    private void getDataFromFile(Resources resources, String packageName) {
        int resourceID = resources.getIdentifier("level" + number, "raw", packageName);
        // TODO: Extracting data from JSON file using resourceID
    }

    public int getNumber() {
        return number;
    }

    public List<String> getObstacles() {
        return obstacles;
    }

    public List<Integer> getIntervals() {
        return intervals;
    }
}
