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

import static com.google.droidjump.leveldata.ObstacleType.bat;
import static com.google.droidjump.leveldata.ObstacleType.cactus;
import static com.google.droidjump.leveldata.ObstacleType.palm;
import java.util.Random;

/**
 * Generates obstacles.
 * In later improvements could base generation on custom parameters.
 */
public class LevelGenerator {
    static private final int MIN_INTERVAL_VALUE = 20; // Min value of interval between obstacles.
    static private final int MAX_INTERVAL_VALUE = 100; // Max value of interval between obstacles.
    static private final ObstacleType[] OBSTACLE_OPTIONS = {cactus, cactus, cactus, palm, palm, bat}; // Options of obstacle for generation.

    private static final Random random = new Random();

    public static ObstacleData generateNextObstacle() {
        return new ObstacleData(generateInterval(), generateObstacleType());
    }

    private static ObstacleType generateObstacleType() {
        return OBSTACLE_OPTIONS[random.nextInt(OBSTACLE_OPTIONS.length)];
    }

    private static int generateInterval() {
        return MIN_INTERVAL_VALUE + random.nextInt(MAX_INTERVAL_VALUE - MIN_INTERVAL_VALUE + 1);
    }
}
