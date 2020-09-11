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

import static com.google.droidjump.R.string.achievement_great_start;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.droidjump.models.LevelManager;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Manages achievements unlocking.
 */
public class AchievementsManager {
    private static final String TAG = "AchievementsManager";

    MainActivity activity;
    boolean incrementalAchievementsDataChanged;
    ArrayList<Achievement> achievementsList;

    public AchievementsManager(MainActivity activity) {
        this.activity = activity;
        incrementalAchievementsDataChanged = true;
        achievementsList = new ArrayList<>();
    }

    public void checkIncrementalAchievementsChanges() {
        if (!incrementalAchievementsDataChanged){
            updateIncrementalAchievements();
            return;
        }
        activity.getAchievementsClient().load(/* forceReload= */false).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                AchievementBuffer achievementBuffer = task.getResult().get();
                for (Achievement achievement : achievementBuffer) {
                    achievementsList.add(achievement.freeze());
                }
                achievementBuffer.close();
                incrementalAchievementsDataChanged = false;
                updateIncrementalAchievements();
            } else {
                //TODO(dnikolskaia): Improve exception handling behavior.
                String message = Objects.requireNonNull(task.getException()).getMessage();
                Log.e(TAG, activity.getString(R.string.loading_achievements_exception) + message);
                Toast.makeText(activity, activity.getString(R.string.failed_to_load_achievemets_toast_text), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unlockAchievement(int achievementId) {
        activity.getAchievementsClient().unlock(activity.getString(achievementId));
    }

    public void incrementAchievement(int achievementId, int steps) {
        activity.getAchievementsClient().increment(activity.getString(achievementId), steps);
    }

    private void updateIncrementalAchievements() {
        int lastLevelIndex = LevelManager.getLastLevelIndex();
        String greatStartAchievementId = activity.getString(achievement_great_start);
        for (Achievement achievement : achievementsList) {
            if (achievement.getAchievementId().equals(greatStartAchievementId)) {
                if (lastLevelIndex > achievement.getCurrentSteps()) {
                    incrementAchievement(achievement_great_start, 1);
                    incrementAchievement(R.string.achievement_new_star, 1);
                    incrementAchievement(R.string.achievement_profi_player, 1);
                    incrementalAchievementsDataChanged = true;
                }
                break;
            }
        }
    }
}
