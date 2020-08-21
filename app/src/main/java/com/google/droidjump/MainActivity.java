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

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.droidjump.models.LevelManager;

/**
 * Represents main activity.
 */
public class MainActivity extends FragmentActivity {
    private static final int RC_SIGN_IN = 2001;
    private GoogleSignInClient signInClient;
    private GoogleSignInAccount signedInAccount;
    private Player player;

    public GoogleSignInAccount getSignedInAccount() {
        return signedInAccount;
    }

    public Player getPlayer() {
        return player;
    }

    public void startSignInIntent() {
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LevelManager.init(this);
        signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_wrapper, new StartFragment()).commit();
        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        signInSilently();
    }

    private void signInSilently() {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            signedInAccount = account;
            extractPlayerFromAccount(signedInAccount);
            setUsernameInUserMenu(player.getDisplayName());
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
            signInClient.silentSignIn().addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // The signed in account is stored in the task's result.
                    signedInAccount = task.getResult();
                    extractPlayerFromAccount(signedInAccount);
                }
            }).continueWithTask((Continuation<GoogleSignInAccount, Task<Void>>) task -> {
                        return Tasks.forResult(null);
                    }
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            String message = result.getStatus().getStatusMessage();
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                signedInAccount = result.getSignInAccount();
                extractPlayerFromAccount(signedInAccount);
                setUsernameInUserMenu(player.getDisplayName());
            } else {
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
            }
            new AlertDialog.Builder(this).setMessage(message)
                    .setNeutralButton(android.R.string.ok, null).show();
        }
    }

    private void extractPlayerFromAccount(GoogleSignInAccount googleSignInAccount) {
        PlayersClient playersClient = Games.getPlayersClient(this, googleSignInAccount);
        playersClient.getCurrentPlayer().addOnSuccessListener(gamesPlayer -> {
            player = gamesPlayer;
        });
    }

    private void setUsernameInUserMenu(String username) {
        ((TextView) findViewById(R.id.username)).setText(username);
    }
}
