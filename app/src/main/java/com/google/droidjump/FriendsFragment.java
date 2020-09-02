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
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.games.FriendsResolutionRequiredException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayerBuffer;
import com.google.android.gms.games.PlayersClient;
import com.google.droidjump.models.LoadingHelper;
import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    private static final int SHOW_SHARING_FRIENDS_CONSENT = 3561;
    private final int recyclerView = R.id.friends_recycler_view;
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
        client = Games.getPlayersClient(activity, activity.getSavedSignedInAccount());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_SHARING_FRIENDS_CONSENT || requestCode == RC_SHOW_PROFILE) {
            fetchFriends();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_screen, container, /* attachToRoot = */ false);
        RecyclerView recyclerView = view.findViewById(this.recyclerView);
        recyclerView.setAdapter(adapter);
        view.findViewById(R.id.load_more_button).setOnClickListener(ignored -> loadMore());
        view.findViewById(R.id.back_button).setOnClickListener(ignored -> activity.onBackPressed());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchFriends();
    }

    private void fetchFriends() {
        LoadingHelper.onLoading(activity, getView(), recyclerView);
        client.loadFriends(GameConstants.ITEMS_PER_PAGE, /* forceReload = */ false)
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
                    LoadingHelper.onLoaded(getView(), recyclerView);
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
        LoadingHelper.onLoading(activity, getView(), recyclerView);
        client.loadMoreFriends(GameConstants.ITEMS_PER_PAGE)
                .addOnSuccessListener(data -> {
                    PlayerBuffer playerBuffer = data.get();
                    if (playerBuffer.getCount() > 0) {
                        players.clear();
                        for (Player player : playerBuffer) {
                            players.add(player.freeze());
                        }
                        adapter.notifyDataSetChanged();
                    }
                    LoadingHelper.onLoaded(getView(), recyclerView);
                    playerBuffer.close();
                });
    }

    private void onEmptyFriendsList() {
        getView().findViewById(R.id.load_more_button).setVisibility(View.GONE);
        getView().findViewById(R.id.empty_list_text).setVisibility(View.VISIBLE);
    }
}
