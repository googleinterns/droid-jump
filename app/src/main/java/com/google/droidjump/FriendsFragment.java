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

import static com.google.droidjump.GameConstants.RC_SHOW_PROFILE;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.games.FriendsResolutionRequiredException;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayerBuffer;
import com.google.android.gms.games.PlayersClient;
import com.google.droidjump.models.LoadingHelper;
import com.google.droidjump.models.NavigationHelper;
import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    private static final int FRIENDS_PER_PAGE = 100;
    private static final int SHOW_SHARING_FRIENDS_CONSENT = 3561;
    private final int recyclerViewId = R.id.friends_recycler_view;
    private MainActivity activity;
    private List<Player> players;
    private FriendsAdapter adapter;
    private PlayersClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        players = new ArrayList<>();
        adapter = new FriendsAdapter(players, activity);
        client = activity.getPlayersClient();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friends_screen, container, /* attachToRoot = */ false);
        RecyclerView recyclerView = rootView.findViewById(recyclerViewId);
        recyclerView.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        rootView.findViewById(R.id.load_more_button).setOnClickListener(ignored -> loadMore());
        rootView.findViewById(R.id.back_button).setOnClickListener(ignored -> activity.onBackPressed());
        NavigationHelper.addOnBackPressedEventListener(activity);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchFriends();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_SHARING_FRIENDS_CONSENT || requestCode == RC_SHOW_PROFILE) {
            fetchFriends();
        }
    }

    private void fetchFriends() {
        View rootView = getView();
        LoadingHelper.onLoading(activity, rootView, recyclerViewId);
        client.loadFriends(FRIENDS_PER_PAGE, /* forceReload = */ false)
                .addOnSuccessListener(data -> {
                    PlayerBuffer playerBuffer = data.get();
                    if (playerBuffer.getCount() > 0) {
                        players.clear();
                        for (Player player : playerBuffer) {
                            players.add(player.freeze());
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        onEmptyFriendsList();
                    }
                    LoadingHelper.onLoaded(rootView, recyclerViewId);
                    playerBuffer.close();
                })
                .addOnFailureListener(exception -> {
                    if (exception instanceof FriendsResolutionRequiredException) {
                        PendingIntent pendingIntent =
                                ((FriendsResolutionRequiredException) exception)
                                        .getResolution();
                        try {
                            activity.startIntentSenderForResult(
                                    pendingIntent.getIntentSender(),
                                    /* requestCode */ SHOW_SHARING_FRIENDS_CONSENT,
                                    /* fillInIntent */ null,
                                    /* flagsMask */ 0,
                                    /* flagsValues */ 0,
                                    /* extraFlags */ 0,
                                    /* options */ null
                            );
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(getClass().toString(), e.getMessage());
                        }
                    }
                });
    }

    private void loadMore() {
        View rootView = getView();
        LoadingHelper.onLoading(activity, rootView, recyclerViewId);
        client.loadMoreFriends(FRIENDS_PER_PAGE)
                .addOnSuccessListener(data -> {
                    PlayerBuffer playerBuffer = data.get();
                    if (playerBuffer.getCount() > 0) {
                        players.clear();
                        for (Player player : playerBuffer) {
                            players.add(player.freeze());
                        }
                        adapter.notifyDataSetChanged();
                    }
                    LoadingHelper.onLoaded(rootView, recyclerViewId);
                    playerBuffer.close();
                });
    }

    private void onEmptyFriendsList() {
        View rootView = getView();
        rootView.findViewById(R.id.load_more_button).setVisibility(View.GONE);
        rootView.findViewById(R.id.empty_list_text).setVisibility(View.VISIBLE);
    }
}
