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
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayerBuffer;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.droidjump.models.GameItem;
import com.google.droidjump.models.LevelManager;
import com.google.droidjump.models.NavigationHelper;
import com.google.droidjump.models.ScoreManager;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents main activity.
 */
public class MainActivity extends FragmentActivity {
    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_VIDEO_OVERLAY = 9011;
    private static final int TEN_SECONDS_IN_MILLISECONDS = 10 * 1000;
    private static final String TAG = MainActivity.class.getName();
    private boolean isActiveConnection;
    private boolean friendListAccess;
    private boolean isLoadFriendNames;
    private Player player;
    private GoogleSignInAccount savedSignedInAccount;
    private AchievementsClient achievementsClient;
    private LeaderboardsClient leaderboardsClient;
    private SnapshotsClient snapshotsClient;
    private PlayersClient playersClient;
    private Set<String> friendIds;
    private Snapshot snapshot;
    private Bitmap coverImage;
    private String description = "";

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScoreManager.init(this);
        LevelManager.init(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_wrapper, new StartFragment()).commit();
        setContentView(R.layout.main_activity);
        ((DrawerLayout) findViewById(R.id.drawer_layout))
                .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        leaderboardsClient = null;
        achievementsClient = null;
        friendListAccess = false;
        coverImage = GameItem.drawableToBitmap(this.getResources().getDrawable(R.drawable.bat));
        friendIds = new HashSet<>();
        isLoadFriendNames = true;
        savedSignedInAccount = null;
        playersClient = null;
        isActiveConnection = false;
    }

    public boolean hasFriendListAccess() {
        return friendListAccess;
    }

    public Set<String> getFriendIds() {
        return friendIds;
    }

    public void openUserMenu() {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(GameConstants.NAVIGATION_START_POSITION);
    }

    public GoogleSignInAccount getSavedSignedInAccount() {
        return savedSignedInAccount;
    }

    public LeaderboardsClient getLeaderboardsClient() {
        return leaderboardsClient;
    }

    public Player getPlayer() {
        return player;
    }

    public AchievementsClient getAchievementsClient() {
        return achievementsClient;
    }

    public PlayersClient getPlayersClient() {
        return playersClient;
    }

    public boolean getLoadFriendNames() {
        return isLoadFriendNames;
    }

    public void countTimeOfPlaying() {
        String leaderboardId = getResources().getString(R.string.leaderboard_best_time);
        if (savedSignedInAccount != null && LevelManager.getLastLevelIndex() != LevelManager.getLevelsLastIndex()) {
            leaderboardsClient.loadCurrentPlayerLeaderboardScore(
                    leaderboardId,
                    LeaderboardVariant.TIME_SPAN_ALL_TIME,
                    LeaderboardVariant.COLLECTION_PUBLIC).addOnSuccessListener(
                    result -> {
                        runTimeTask(leaderboardId);
                    });
        }
    }

    String mCurrentSaveName = "snapshotTemp";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
                onConnected(signedInAccount);
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = "Unknown error";
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
        // Making onActivityResult work in all fragments.
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Task<SnapshotMetadata> writeSnapshot(Snapshot snapshot,
                                                 byte[] data, Bitmap coverImage, String desc) {

        // Set the data payload for the snapshot
        snapshot.getSnapshotContents().writeBytes(data);

        // Create the change operation
        SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                .setCoverImage(coverImage)
                .setDescription(desc)
                .build();

        SnapshotsClient snapshotsClient =
                Games.getSnapshotsClient(this, GoogleSignIn.getLastSignedInAccount(this));

        // Commit the operation
        return snapshotsClient.commitAndClose(snapshot, metadataChange);
    }

    Task<byte[]> loadSnapshot() {
        // Display a progress dialog
        // ...

        // Get the SnapshotsClient from the signed in account.
        SnapshotsClient snapshotsClient =
                Games.getSnapshotsClient(this, GoogleSignIn.getLastSignedInAccount(this));

        // In the case of a conflict, the most recently modified version of this snapshot will be used.
        int conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED;

        // Open the saved game using its name.
        return snapshotsClient.open(mCurrentSaveName, true, conflictResolutionPolicy)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error while opening Snapshot.", e);
                    }
                }).continueWith(new Continuation<SnapshotsClient.DataOrConflict<Snapshot>, byte[]>() {
                    @Override
                    public byte[] then(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) throws Exception {
                        snapshot = task.getResult().getData();

                        // Opening the snapshot was a success and any conflicts have been resolved.
                        try {
                            // Extract the raw data from the snapshot.
                            Log.d(TAG, "I am inside of getting snapshot!");
                            return snapshot.getSnapshotContents().readFully();
                        } catch (IOException e) {
                            Log.e(TAG, "Error while reading Snapshot.", e);
                        }

                        return null;
                    }
                }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        // Dismiss progress dialog and reflect the changes in the UI when complete.
                        // ...
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isActiveConnection) {
            signInSilently();
        }
    }

    public Task<AnnotatedData<PlayerBuffer>> loadFriendIds() {
        return getPlayersClient().loadFriends(GameConstants.FRIENDS_PER_PAGE, /* forceReload = */ false)
                .addOnSuccessListener(buffer -> {
                    AtomicReference<PlayerBuffer> playerBufferAtomicReference = new AtomicReference<>();
                    playerBufferAtomicReference.set(buffer.get());
                    while (DataBufferUtils.hasNextPage(playerBufferAtomicReference.get())) {
                        getPlayersClient().loadMoreFriends(GameConstants.FRIENDS_PER_PAGE).addOnSuccessListener(data -> {
                            playerBufferAtomicReference.set(data.get());
                        });
                    }
                    friendIds.clear();
                    for (Player player : playerBufferAtomicReference.get()) {
                        String friendId = player.freeze().getPlayerId();
                        friendIds.add(friendId);
                    }
                    friendListAccess = true;
                    isLoadFriendNames = false;
                    playerBufferAtomicReference.get().close();
                }).addOnFailureListener(ignored -> friendListAccess = false);
    }

    private void runTimeTask(String leaderboardId) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (savedSignedInAccount != null) {
                    long time = ScoreManager.getScore(leaderboardId);
                    if (LevelManager.getLastLevelIndex() == LevelManager.getLevelsLastIndex()) {
                        ScoreManager.submitScore(leaderboardId, time);
                        timer.cancel();
                    }
                    time += TEN_SECONDS_IN_MILLISECONDS;
                    ScoreManager.submitLocalScore(leaderboardId, time);
                }
            }
        };
        timer.scheduleAtFixedRate(
                /* task = */ task,
                /* delay = */ TEN_SECONDS_IN_MILLISECONDS,
                /* period = */ TEN_SECONDS_IN_MILLISECONDS);
    }

    private void signInSilently() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                        // Add the APPFOLDER scope for Snapshot support.
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .build();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Already signed in.
            onConnected(account);
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
            signInClient
                    .silentSignIn()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // The signed in account is stored in the task's result.
                            GoogleSignInAccount signedInAccount = task.getResult();
                            Log.d(TAG, " Silent sign in success");
                            onConnected(signedInAccount);
                        } else {
                            // Player will need to sign-in explicitly using via UI.
                            Log.d(TAG, " Silent sign in failed");
                            onDisconnected();
                        }
                    });
        }
    }

    private void startSignInIntent() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                        // Add the APPFOLDER scope for Snapshot support.
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void onDisconnected() {
        isActiveConnection = true;
        savedSignedInAccount = null;
        leaderboardsClient = null;
        achievementsClient = null;
        playersClient = null;
        isLoadFriendNames = true;
        friendListAccess = false;
        ScoreManager.setClient(null);
        disableNavigationMenu();
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        if (savedSignedInAccount != googleSignInAccount) {
            savedSignedInAccount = googleSignInAccount;
            achievementsClient = Games.getAchievementsClient(this, savedSignedInAccount);
            leaderboardsClient = Games.getLeaderboardsClient(this, savedSignedInAccount);
            snapshotsClient = Games.getSnapshotsClient(this, savedSignedInAccount);
            updateGameState();
            playersClient = Games.getPlayersClient(this, savedSignedInAccount);
            loadFriendIds();
            ScoreManager.setClient(leaderboardsClient);
            playersClient.getCurrentPlayer()
                    .addOnSuccessListener(player -> {
                        this.player = player;
                        // Showing a welcome popup.
                        Games.getGamesClient(this, googleSignInAccount)
                                .setViewForPopups(findViewById(R.id.activity_wrapper));
                        if (isActiveConnection) {
                            isActiveConnection = false;
                        }
                        enableNavigationMenu(player);
                        setLeaderboardsScores();
                        countTimeOfPlaying();
                    })
                    .addOnFailureListener(createFailureListener("There was a problem getting the player id!"));
        }
    }

    private void updateGameState(){
        loadSnapshot().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                byte[] savedGameData = task.getResult();
                if (savedGameData.length == 0)
                    return;
                SavedGameManager.setGameData(savedGameData);
                Log.d(TAG, "success get snapshot data");
            }
            else{
                Log.d(TAG, "failed get snapshot data");
            }
        });
    }

    private OnFailureListener createFailureListener(final String message) {
        return e -> {
            Log.e(TAG, message);
            onDisconnected();
        };
    }

    private void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        writeSnapshot(snapshot, SavedGameManager.getGameData(),coverImage, description).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d(TAG, "Snapshot is recorded");
            }
            else{
                Log.d(TAG, "Snapshot is not recorded");
            }
        });
        signInClient.signOut().addOnCompleteListener(this,
                task -> {
                    // At this point, the user is signed out.
                    onDisconnected();
                });
    }

    private void setupDrawer(boolean isEnabled) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.getMenu().setGroupEnabled(R.id.nav_items, isEnabled);
        navigationView.getMenu().setGroupCheckable(R.id.nav_items, isEnabled, isEnabled);
        navigationView.getMenu().setGroupEnabled(R.id.nav_recording_items, isEnabled);
        navigationView.getMenu().setGroupCheckable(R.id.nav_recording_items, isEnabled, isEnabled);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (isEnabled) {
                switch (id) {
                    case R.id.nav_friends:
                        NavigationHelper.navigateToFragment(MainActivity.this, new FriendsFragment());
                        break;
                    case R.id.nav_achievements:
                        NavigationHelper.navigateToFragment(MainActivity.this, new AchievementsFragment());
                        break;
                    case R.id.nav_auth:
                        startSignInIntent();
                        break;
                    case R.id.nav_leaderboards:
                        NavigationHelper.navigateToFragment(MainActivity.this, new LeaderboardsFragment());
                        break;
                    case R.id.nav_video_recording:
                        showVideoOverlay(findViewById(R.id.activity_wrapper));
                        break;
                    case R.id.nav_saved_games:
                        showSavedGamesUI();
                        break;
                }
                ((DrawerLayout) findViewById(R.id.drawer_layout))
                        .closeDrawer(GameConstants.NAVIGATION_START_POSITION);
            }
            return true;
        });
    }

    private void disableNavigationMenu() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        findViewById(R.id.drawer_header_placeholder).setVisibility(View.VISIBLE);
        findViewById(R.id.menu_header).setVisibility(View.GONE);
        MenuItem authButton = navigationView.getMenu().findItem(R.id.nav_auth);
        authButton.setTitle(R.string.sign_in);
        authButton.setOnMenuItemClickListener(ignored -> {
            startSignInIntent();
            return true;
        });
        setupDrawer(/* isEnabled= */ false);
        NavigationHelper.navigateToFragment(this, new StartFragment());
    }

    private void enableNavigationMenu(Player player) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        findViewById(R.id.drawer_header_placeholder).setVisibility(View.GONE);
        ImageManager manager = ImageManager.create(this);
        ImageView userAvatar = findViewById(R.id.user_avatar);
        manager.loadImage(userAvatar, player.getIconImageUri());
        navigationView.findViewById(R.id.menu_header).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.username)).setText(player.getDisplayName());
        MenuItem authButton = navigationView.getMenu().findItem(R.id.nav_auth);
        authButton.setTitle(R.string.sign_out);
        authButton.setOnMenuItemClickListener(ignored -> {
            signOut();
            return true;
        });
        setupDrawer(/* isEnabled= */ true);
        NavigationHelper.navigateToFragment(this, new StartFragment());
    }

    private void disableGameProcess(){

    }

    private void setLeaderboardsScores() {
        for (int leaderboard : GameConstants.LEADERBOARD_LIST) {
            String leaderboardId = getResources().getString(leaderboard);
            leaderboardsClient.loadCurrentPlayerLeaderboardScore(
                    leaderboardId,
                    LeaderboardVariant.TIME_SPAN_ALL_TIME,
                    LeaderboardVariant.COLLECTION_PUBLIC)
                    .addOnSuccessListener(data -> {
                        LeaderboardScore score = data.get();
                        long localScore = ScoreManager.getScore(leaderboardId);

                        // Merging a local score with the leaderboard score.
                        long newScore;
                        if (score != null) {
                            long leaderboardScore = score.getRawScore();
                            newScore = Math.max(localScore, leaderboardScore);
                        } else {
                            newScore = localScore;
                        }
                        if (leaderboard != R.string.leaderboard_best_time
                                || LevelManager.getLastLevelIndex() == LevelManager.getLevelsLastIndex()) {
                            ScoreManager.submitScore(leaderboardId, newScore);
                        }
                    });
        }
    }

    private void showVideoOverlay(View myview) {
        Games.getVideosClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getCaptureOverlayIntent()
                .addOnSuccessListener(intent -> startActivityForResult(intent, RC_VIDEO_OVERLAY));
    }

    private static final int RC_SAVED_GAMES = 9009;

    private void showSavedGamesUI() {
        SnapshotsClient snapshotsClient =
                Games.getSnapshotsClient(this, GoogleSignIn.getLastSignedInAccount(this));
        int maxNumberOfSavedGamesToShow = 5;

        Task<Intent> intentTask = snapshotsClient.getSelectSnapshotIntent(
                "See My Saves", true, true, maxNumberOfSavedGamesToShow);

        intentTask.addOnSuccessListener(new OnSuccessListener<Intent>() {
            @Override
            public void onSuccess(Intent intent) {
                startActivityForResult(intent, RC_SAVED_GAMES);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (savedSignedInAccount != null){
            writeSnapshot(snapshot, SavedGameManager.getGameData(), null, null);
        }
        super.onDestroy();
    }
}
