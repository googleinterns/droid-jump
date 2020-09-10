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

import static com.google.droidjump.leveldata.ObstacleType.BAT;
import java.util.Map;
import java.util.Random;

/**
 * Generates obstacles.
 * In later improvements could base generation on custom parameters.
 */
public class LevelGenerator {
    static private final int MIN_INTERVAL_VALUE = 20; // Min value of interval between obstacles.
    static private final int MAX_INTERVAL_VALUE = 80; // Max value of interval between obstacles.
    // Options of obstacles for generation.
    static private final ObstacleType[] OBSTACLE_OPTIONS = ObstacleType.values();
    static private final Random random = new Random();
    private int[] frequencies = new int[OBSTACLE_OPTIONS.length];
    private int frequencySum = 0;
    private int count = 0;

    LevelGenerator(Map<ObstacleType, Integer> frequencyShares) {
        // Fill frequencies array and calculate sum.
        for (int i = 0; i < OBSTACLE_OPTIONS.length; i++) {
            frequencies[i] = frequencyShares.get(OBSTACLE_OPTIONS[i]);
            frequencySum += frequencies[i];
        }
    }

    public ObstacleData generateNextObstacle() {
//        count++;
//        if (count % 2 == 0)
//            return new ObstacleData(60, PALM);
        return new ObstacleData(60, BAT);
//        return new ObstacleData(generateInterval(), generateObstacleType());
    }

    private ObstacleType generateObstacleType() {
        ObstacleType generatedObstacleType = null;
        // Random value from frequency range.
        int generatedFrequencyValue = random.nextInt(frequencySum);
        int currentPrefSum = 0;
        for (int i = 0; i < frequencies.length; i++) {
            currentPrefSum += frequencies[i];
            if (generatedFrequencyValue < currentPrefSum) {
                generatedObstacleType = OBSTACLE_OPTIONS[i];
                break;
            }
        }
        return generatedObstacleType;
    }

    private int generateInterval() {
        return MIN_INTERVAL_VALUE + random.nextInt(MAX_INTERVAL_VALUE - MIN_INTERVAL_VALUE + 1);
    }
}
