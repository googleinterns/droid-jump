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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.droidjump.leaderboards_data.Leaderboard;
import com.google.droidjump.leaderboards_data.LeaderboardsAdapter;
import com.google.droidjump.models.NavigationHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays Leaderboards Screen.
 */
public class LeaderboardsFragment extends Fragment {
    private MainActivity activity;
    private List<Leaderboard> leaderboards;
    private LeaderboardsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        leaderboards = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.leaderboards_screen, container, /* attachToRoot= */ false);
        RecyclerView leaderboardsView = rootView.findViewById(R.id.leaderboards_recycler_view);
        leaderboardsView.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
        adapter = new LeaderboardsAdapter(leaderboards, activity);
        leaderboardsView.setAdapter(adapter);
        populateLeaderboards();
        rootView.findViewById(R.id.back_button).setOnClickListener(ignored -> activity.onBackPressed());
        NavigationHelper.addOnBackPressedEventListener(activity);

        return rootView;
    }

    private void populateLeaderboards() {
        // TODO(maksme): Receive data from PGS
        leaderboards.clear();
        leaderboards.add(new Leaderboard("leaderboard1", R.mipmap.droid));
        leaderboards.add(new Leaderboard("leaderboard2", R.mipmap.cactus));
        leaderboards.add(new Leaderboard("leaderboard3", R.mipmap.bat));
    }
}
