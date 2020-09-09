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

import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import java.util.ArrayList;
import java.util.Objects;

public class AchievementsManager {
    MainActivity activity;

    public AchievementsManager(MainActivity activity) {
        this.activity = activity;
    }

    public ArrayList<Achievement> getAchievementsList() {
        ArrayList<Achievement> achievementsList = new ArrayList<>();
        activity.getAchievementsClient().load(true).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                AchievementBuffer achievementBuffer = task.getResult().get();
                Log.d("AchievementsManager ", "buffer size : " + achievementBuffer.getCount());
                for (Achievement achievement: achievementBuffer){
                    achievementsList.add(achievement.freeze());
                }
                Log.d("AchievementsManager ", "list size : " + achievementBuffer.getCount());
                achievementBuffer.close();
            } else {
                //TODO(dnikolskaia): Improve exception handling behavior.
                String message = Objects.requireNonNull(task.getException()).getMessage();
                Log.e("AchievementsManager", "Failed to load achievements from client: " + message);
                Toast.makeText(activity, "Oops, something went wrong with getting achievements", Toast.LENGTH_SHORT).show();
            }
        });
        return achievementsList;
    }

    public void unlockAchievement(int achievementId) {
        activity.getAchievementsClient().unlock(activity.getString(achievementId));
    }

    public void incrementAchievement(int achievementId, int steps) {
        activity.getAchievementsClient().increment(activity.getString(achievementId), steps);
    }
}
