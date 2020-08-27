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

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.droidjump.models.LevelManager;
import com.google.droidjump.models.NavigationHelper;

/**
 * Represents main activity.
 */
public class MainActivity extends FragmentActivity {
    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "MainActivity";
    private static final int RC_ACHIEVEMENT_UI = 9003;
    private String playerId;
    private boolean isActiveConnection = false;
    private GoogleSignInAccount savedSignedInAccount = null;

    public void openUserMenu() {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(GameConstants.NAVIGATION_START_POSITION);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Into onResume()");
        super.onResume();
        if (!isActiveConnection) {
            signInSilently();
        }
    }

    private void signInSilently() {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Log.d(TAG, "Into Silent Sign in");
        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            Log.d(TAG, "Into Silent Sign in : already signed in");
            GoogleSignInAccount signedInAccount = account;
            onConnected(signedInAccount);
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
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
                onConnected(signedInAccount);
                Log.d(TAG, " Active sign in success");
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = "Unknown error";
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

    private void onDisconnected() {
        isActiveConnection = true;
        disableNavigationMenu();
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "Into on connected method");
        if (savedSignedInAccount != googleSignInAccount) {
            savedSignedInAccount = googleSignInAccount;
            // Get the playerId from the PlayersClient.
            PlayersClient playersClient = Games.getPlayersClient(this, googleSignInAccount);
            playersClient.getCurrentPlayer()
                    .addOnSuccessListener(player -> {
                        playerId = player.getPlayerId();
                        Games.getGamesClient(this, googleSignInAccount)
                                .setViewForPopups(findViewById(R.id.activity_wrapper));
                        if (isActiveConnection) {
                            isActiveConnection = false;
                        }
                        enableNavigationMenu(player);
                    })
                    .addOnFailureListener(createFailureListener("There was a problem getting the player id!"));
        }
    }

    private OnFailureListener createFailureListener(final String string) {
        return e -> {
            Log.e(TAG, string);
            onDisconnected();
        };
    }

    private void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(this,
                task -> {
                    // At this point, the user is signed out.
                    onDisconnected();
                });
    }

    private void init() {
        LevelManager.init(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_wrapper, new StartFragment()).commit();
        setContentView(R.layout.main_activity);
        ((DrawerLayout) findViewById(R.id.drawer_layout))
                .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void setupDrawer(boolean isEnabled) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.getMenu().setGroupEnabled(R.id.nav_items, isEnabled);
        navigationView.getMenu().setGroupCheckable(R.id.nav_items, isEnabled, isEnabled);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (isEnabled) {
                switch (id) {
                    case R.id.nav_friends:
                    case R.id.nav_achievements:
                        showAchievements();
                        break;
                    case R.id.nav_auth:
                        startSignInIntent();
                        break;
                    case R.id.nav_leaderboards:
                        NavigationHelper.navigateToFragment(MainActivity.this, new LeaderboardsFragment());
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

    private void showAchievements() {
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_ACHIEVEMENT_UI);
                    }
                });
    }

}
