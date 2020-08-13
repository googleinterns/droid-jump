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

/**
 * Stores all global constants of the game.
 */
public interface GameConstants {
    int DROID_COUNT_ON_FULL_DROID_PICTURE = 9; // Count of droids at res/mipmap/droid.png image.
    int DROID_JUMPING_CHARACTER_INDEX = 4; // Index of droid at res/mipmap/droid.png image.
    int DROID_FIRST_STEP_INDEX = 5; // Index of droid at res/mipmap/droid.png image.
    int DROID_SECOND_STEP_INDEX = 6; // Index of droid at res/mipmap/droid.png image.
    int SLEEP_TIME = 15; // Sleep time in gameView (ms).
    int FIRST_LEVEL_ID = 0; // Default value.
    int INTERVAL_START_TIME =  0; // Beginning of the new time interval between obstacles
    double GROUND_PROPORTION = 0.6; // Handpicked value of proportion in a platform picture

    String GAME_LEVEL_HEADER = "Lvl."; // Used for displaying the current level in GameView.
    // Used for identifying the current level from SharedPreferences.
    String GAME_VIEW_CURRENT_LEVEL_STRING = "java.com.google.droidjump.GameView.CurrentLevel";
    // Used for identifying the last level from SharedPreferences.
    String GAME_VIEW_LAST_LEVEL_STRING = "java.com.google.droidjump.GameView.LastLevel";
    // Used to identifying game data from SharePreferences.
    String GAME_VIEW_DATA = "java.com.google.droidjump.GameView.Data";
}
