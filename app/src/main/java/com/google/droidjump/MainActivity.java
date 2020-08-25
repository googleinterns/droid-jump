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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.droidjump.models.LevelManager;
import com.google.droidjump.models.NavigationHelper;

/**
 * Represents main activity.
 */
public class MainActivity extends FragmentActivity {
    public void openUserMenu() {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(GameConstants.DRAWER_POSITION);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LevelManager.init(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_wrapper, new StartFragment()).commit();
        setContentView(R.layout.main_activity);
        ((DrawerLayout) findViewById(R.id.drawer_layout))
                .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setupDrawer();
    }

    private void setupDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            Fragment fragmentToNavigate = null;
            switch (id) {
                case R.id.nav_friends:
                case R.id.nav_achievements:
                    fragmentToNavigate = new StartFragment();
                    break;
                case R.id.nav_leaderboards:
                    fragmentToNavigate = new LeaderboardsFragment();
            }
            NavigationHelper.navigateToFragment(MainActivity.this, fragmentToNavigate);
            ((DrawerLayout) findViewById(R.id.drawer_layout))
                    .closeDrawer(GameConstants.DRAWER_POSITION);
            return true;
        });
    }
}
