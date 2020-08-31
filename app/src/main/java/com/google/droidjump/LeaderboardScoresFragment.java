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

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.droidjump.leaderboards_data.LeaderboardsScoresAdapter;
import com.google.droidjump.models.NavigationHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays Leaderboards Screen.
 */
public class LeaderboardScoresFragment extends Fragment {
    private Leaderboard leaderboard;
    private MainActivity activity;
    private List<LeaderboardScore> scores;
    private LeaderboardsScoresAdapter adapter;
    private int timeSpan;
    private int collection;
    private final int SHOW_SHARING_FRIENDS_CONSENT = 3001;

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_SHARING_FRIENDS_CONSENT) {
            collection = LeaderboardVariant.COLLECTION_FRIENDS;
            fetchScores(timeSpan, collection);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.leaderboard_scores_screen, container, /* attachToRoot= */ false);
        ImageManager.create(activity).loadImage((ImageView) rootView.findViewById(R.id.leaderboard_avatar), leaderboard.getIconImageUri());
        ((TextView) rootView.findViewById(R.id.leaderboards_title)).setText(leaderboard.getDisplayName());
        adapter = new LeaderboardsScoresAdapter(scores, activity);
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
        NavigationHelper.addOnBackPressedEventListener(activity);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchScores(timeSpan, collection);
    }

    private void onLoading() {
        scores.clear();
        getView().findViewById(R.id.scores_recycler_view).setVisibility(View.GONE);
        View loadingView = getView().findViewById(R.id.loading_layout);
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.placeholder);
        loadingView.startAnimation(animation);
        loadingView.setVisibility(View.VISIBLE);
    }

    private void onLoaded() {
        getView().findViewById(R.id.scores_recycler_view).setVisibility(View.VISIBLE);
        View loadingView = getView().findViewById(R.id.loading_layout);
        loadingView.clearAnimation();
        loadingView.setVisibility(View.GONE);
    }

    @SuppressLint("CommitPrefEdits")
    private void fetchScores(int timeSpan, int collection) {
        onLoading();
        Games.getLeaderboardsClient(activity, activity.getSavedSignedInAccount())
                .loadPlayerCenteredScores(
                        leaderboard.getLeaderboardId(), timeSpan,
                        collection, GameConstants.SCORES_PER_PAGE, /* forceReload = */ false)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        LeaderboardScoreBuffer scoreBuffer = task.getResult().get().getScores();
                        scores.clear();
                        for (LeaderboardScore score : scoreBuffer) {
                            scores.add(score.freeze());
                        }
                        adapter.notifyDataSetChanged();
                        onLoaded();
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
