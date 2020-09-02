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

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.droidjump.achievements_data.AchievementsAdapter;
import com.google.droidjump.models.NavigationHelper;
import java.util.Objects;

/**
 * Displays Achievements Screen.
 */
public class AchievementsFragment extends Fragment {
    private MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.achievements_screen, container, /* attachToRoot= */ false);
        rootView.findViewById(R.id.menu_button).setOnClickListener(ignored ->
                NavigationHelper.navigateToFragment(activity, new StartFragment()));
        RecyclerView achievementsView = rootView.findViewById(R.id.achievements_recycler_view);
        achievementsView.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
        activity.getAchievementsClient().load(true).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                AchievementBuffer achievementBuffer = task.getResult().get();
                if (achievementBuffer != null) {
                    AchievementsAdapter adapter = new AchievementsAdapter(achievementBuffer, activity);
                    achievementsView.setAdapter(adapter);
                }
            } else {
                String message = Objects.requireNonNull(task.getException()).getMessage();
                Log.e("AchievementsFragment", "Failed to load achievements from client: " + message);
            }
        });
        return rootView;
    }
}
