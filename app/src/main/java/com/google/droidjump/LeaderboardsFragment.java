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
import com.google.droidjump.leaderboards_data.LeaderboardsPlayer;
import com.google.droidjump.leaderboards_data.LeaderboardsPlayersAdapter;
import com.google.droidjump.models.NavigationHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays Leaderboards Screen.
 */
public class LeaderboardsFragment extends Fragment {
    private MainActivity activity;
    private List<LeaderboardsPlayer> players;
    private LeaderboardsPlayersAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        players = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.leaderboards_screen, container, /* attachToRoot= */ false);
        RecyclerView playersView = rootView.findViewById(R.id.players_recycler_view);
        playersView.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
        adapter = new LeaderboardsPlayersAdapter(players);
        playersView.setAdapter(adapter);
        populatePlayers();
        NavigationHelper.addOnBackPressedEventListener(activity);
        return rootView;
    }

    private void populatePlayers() {
        // TODO(maksme): Receive data from PGS
        players.add(new LeaderboardsPlayer("username1", 100, 1, R.mipmap.droid));
        players.add(new LeaderboardsPlayer("username2", 98, 2, R.mipmap.bat));
        players.add(new LeaderboardsPlayer("username3", 88, 3, R.mipmap.cactus));
        players.add(new LeaderboardsPlayer("username4", 85, 4, R.mipmap.bat));
        players.add(new LeaderboardsPlayer("username5", 72, 5, R.mipmap.cactus));
        players.add(new LeaderboardsPlayer("username6", 56, 6, R.mipmap.bat));
        adapter.notifyDataSetChanged();
    }
}
