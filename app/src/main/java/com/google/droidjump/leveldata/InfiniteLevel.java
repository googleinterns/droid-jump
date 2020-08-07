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

public class InfiniteLevel implements LevelStrategy {

    ObstacleData currentObstacle = new ObstacleData(2, ObstacleType.cactus);
    int baseSpeed = 30;

    public InfiniteLevel() {
        // TODO: Get level properties such as baseSpeed from a file
    }

    @Override
    public int getCurrentTimeInterval() {
        return currentObstacle.getInterval();
    }

    @Override
    public ObstacleType getNewObstacleType() {
        ObstacleType newObstacleType = currentObstacle.getType();
        currentObstacle = generateNextObstacle(currentObstacle.getType());
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

    private ObstacleData generateNextObstacle(ObstacleType obstacleType) {
        // TODO: Generate obstacle type
        // TODO: Generate interval

        // So far returns same obstacles
        return new ObstacleData(40, ObstacleType.cactus);
    }
}
