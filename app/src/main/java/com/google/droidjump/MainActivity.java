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
import android.view.View;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.droidjump.models.LevelManager;

/**
 * Represents main activity.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "MainActivity";
    private String playerId;
    private boolean isActiveConnection = false;
    private GoogleSignInAccount savedSignedInAccount = null;
    private AchievementsClient achievementsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    .addOnCompleteListener(
                            this,
                            new OnCompleteListener<GoogleSignInAccount>() {
                                @Override
                                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
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
        setContentView(R.layout.activity_sign_in);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "Into on connected method");
        if (savedSignedInAccount != googleSignInAccount) {
            savedSignedInAccount = googleSignInAccount;
            achievementsClient = Games.getAchievementsClient(this, savedSignedInAccount);
            // Get the playerId from the PlayersClient
            PlayersClient playersClient = Games.getPlayersClient(this, googleSignInAccount);
            playersClient.getCurrentPlayer()
                    .addOnSuccessListener(new OnSuccessListener<Player>() {
                        @Override
                        public void onSuccess(Player player) {
                            playerId = player.getPlayerId();
                            Log.d(TAG, "Player name : " + player.getDisplayName());
                            switchToGameScreen();
                            if (isActiveConnection)
                                isActiveConnection = false;
                        }
                    })
                    .addOnFailureListener(createFailureListener("There was a problem getting the player id!"));
        }
    }

    private OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, string);
                onDisconnected();
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
                startSignInIntent();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.achievements_button:
                showAchievements();
                break;
            case R.id.unlock_button:
                achievementsClient.unlock(getString(R.string.achievement_my_first_achievement));
                break;
        }
    }

    private void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // at this point, the user is signed out.
                        onDisconnected();
                    }
                });
    }

    void switchToGameScreen() {
        LevelManager.init(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_wrapper, new StartFragment()).commit();
        setContentView(R.layout.main_activity);
    }

    private static final int RC_ACHIEVEMENT_UI = 9003;

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
