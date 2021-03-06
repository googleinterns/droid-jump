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

/**
 * Stores keys for level properties in JSON files.
 */
public interface JSONKeys {
    String BASE_SPEED_KEY = "baseSpeed";
    String TIMELINE_KEY = "timeline";
    String INTERVAL_KEY = "interval";
    String TYPE_KEY = "type";
    String OBSTACLES_LIMIT_KEY = "obstaclesLimit";
    String GENERATION_FREQUENCIES_KEY = "generationFrequencies";
}
