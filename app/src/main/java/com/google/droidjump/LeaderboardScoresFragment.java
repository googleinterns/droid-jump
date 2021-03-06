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

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.FriendsResolutionRequiredException;
import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.droidjump.models.LoadingHelper;
import com.google.droidjump.models.NavigationHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays Leaderboards Screen.
 */
public class LeaderboardScoresFragment extends Fragment {
    private static final String ALL_TIME_SCORES = "All time";
    private static final String WEEKLY_SCORES = "Weekly";
    private static final String DAILY_SCORES = "Daily";
    private static final int SHOW_SHARING_FRIENDS_CONSENT = 3001;

    private Leaderboard leaderboard;
    private MainActivity activity;
    private List<LeaderboardScore> scores;
    private LeaderboardsScoresAdapter adapter;
    private int timeSpan;
    private int collection;
    private int recyclerViewId;

    public LeaderboardScoresFragment(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        scores = new ArrayList<>();
        timeSpan = LeaderboardVariant.TIME_SPAN_ALL_TIME;
        collection = LeaderboardVariant.COLLECTION_PUBLIC;
        recyclerViewId = R.id.scores_recycler_view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_SHARING_FRIENDS_CONSENT) {
            fetchScores(timeSpan, collection);
        } else if (requestCode == GameConstants.RC_SHOW_PROFILE) {
            LoadingHelper.onLoading(activity, getView(), recyclerViewId);
            activity.loadFriendIds().addOnSuccessListener(ignored -> {
                fetchScores(timeSpan, collection);
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.leaderboard_scores_screen, container, /* attachToRoot= */ false);
        ImageManager.create(activity).loadImage((ImageView) rootView.findViewById(R.id.leaderboard_avatar), leaderboard.getIconImageUri());
        ((TextView) rootView.findViewById(R.id.leaderboards_title)).setText(leaderboard.getDisplayName());
        adapter = new LeaderboardsScoresAdapter(scores, activity, activity.getFriendIds(), activity.hasFriendListAccess());
        RecyclerView scoresView = rootView.findViewById(R.id.scores_recycler_view);
        scoresView.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
        scoresView.setAdapter(adapter);
        Switch friendsSwitch = rootView.findViewById(R.id.friends_switch);
        friendsSwitch.setOnClickListener(ignored -> {
            if (friendsSwitch.isChecked()) {
                collection = LeaderboardVariant.COLLECTION_FRIENDS;
            } else {
                collection = LeaderboardVariant.COLLECTION_PUBLIC;
            }
            fetchScores(timeSpan, collection);
        });
        rootView.findViewById(R.id.back_button).setOnClickListener(ignored -> activity.onBackPressed());
        Spinner timeSpinner = rootView.findViewById(R.id.time_spinner);

        // Adding items to spinner.
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item,
                new String[]{ALL_TIME_SCORES, WEEKLY_SCORES, DAILY_SCORES});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(spinnerAdapter);
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = timeSpinner.getSelectedItem().toString();
                if (selectedItemText.equals(ALL_TIME_SCORES)) {
                    timeSpan = LeaderboardVariant.TIME_SPAN_ALL_TIME;
                } else if (selectedItemText.equals(WEEKLY_SCORES)) {
                    timeSpan = LeaderboardVariant.TIME_SPAN_WEEKLY;
                } else if (selectedItemText.equals(DAILY_SCORES)) {
                    timeSpan = LeaderboardVariant.TIME_SPAN_DAILY;
                }
                fetchScores(timeSpan, collection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        NavigationHelper.addOnBackPressedEventListener(activity);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity.getLoadFriendNames()) {
            activity.loadFriendIds().addOnSuccessListener(data -> {
                adapter.setFriendListAccess(activity.hasFriendListAccess());
                adapter.notifyDataSetChanged();
                fetchScores(timeSpan, collection);
            });
        } else {
            fetchScores(timeSpan, collection);
        }
    }

    private void fetchScores(int timeSpan, int collection) {
        View rootView = getView();
        TextView emptyListText = rootView.findViewById(R.id.empty_list_text);
        emptyListText.setVisibility(View.GONE);
        LoadingHelper.onLoading(activity, rootView, recyclerViewId);
        activity.getLeaderboardsClient().loadPlayerCenteredScores(
                leaderboard.getLeaderboardId(), timeSpan,
                collection, GameConstants.SCORES_PER_PAGE, /* forceReload= */ false)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        LeaderboardScoreBuffer scoreBuffer = task.getResult().get().getScores();
                        scores.clear();
                        if (scoreBuffer.getCount() > 0) {
                            for (LeaderboardScore score : scoreBuffer) {
                                scores.add(score.freeze());
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            emptyListText.setVisibility(View.VISIBLE);
                        }
                        LoadingHelper.onLoaded(rootView, recyclerViewId);
                        scoreBuffer.close();
                    } else {
                        if (task.getException() instanceof FriendsResolutionRequiredException) {
                            PendingIntent pendingIntent =
                                    ((FriendsResolutionRequiredException) task.getException())
                                            .getResolution();
                            activity.startIntentSenderForResult(
                                    pendingIntent.getIntentSender(),
                                    /* requestCode */ SHOW_SHARING_FRIENDS_CONSENT,
                                    /* fillInIntent */ null,
                                    /* flagsMask */ 0,
                                    /* flagsValues */ 0,
                                    /* extraFlags */ 0,
                                    /* options */ null);
                        }
                    }
                    return null;
                });
    }
}
