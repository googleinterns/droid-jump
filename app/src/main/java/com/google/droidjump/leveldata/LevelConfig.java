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

import android.content.Context;

public class LevelConfig {
    private Context context;
    private String levelName;
    private LevelType levelType;
    private LevelStrategy levelStrategy;
    private int resourceId;

    LevelConfig(String levelName, LevelType levelType, String resourceName, Context context) {
        this.levelName = levelName;
        this.levelType = levelType;
        this.context = context;
        resourceId = context.getResources().getIdentifier(resourceName, "raw", context.getPackageName());
    }

    public String getLevelName() {
        return levelName;
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public int getResourceId() {
        return resourceId;
    }

    public LevelStrategy getLevelStrategy() {
        switch (levelType) {
            case Infinite:
                levelStrategy = new InfiniteLevelData(resourceId, context.getResources());
                break;
            case Finite:
                levelStrategy = new FiniteLevelData(resourceId, context.getResources());
                break;
            case FiniteGenerated:
                break;
        }
        return levelStrategy;
    }
}
