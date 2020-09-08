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

public class AchievementsManager {
    MainActivity activity;

    public AchievementsManager(MainActivity activity) {
        this.activity = activity;
    }

    public void unlockAchievement(int achievementId) {
        activity.getAchievementsClient().unlock(activity.getString(achievementId));
    }

    public void incrementAchievement(int achievementId, int steps) {
        activity.getAchievementsClient().increment(activity.getString(achievementId), steps);
    }
}
