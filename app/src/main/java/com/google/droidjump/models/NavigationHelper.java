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

package com.google.droidjump.models;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.droidjump.R;
import com.google.droidjump.StartFragment;

public class NavigationHelper {
    public static void addOnBackPressedEventListener(FragmentActivity activity, Fragment fragmentToNavigate) {
        activity.getOnBackPressedDispatcher().addCallback(activity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_wrapper, new StartFragment())
                        .commit();
            }
        });
    }

    public static void navigateToFragment(FragmentActivity activity, Fragment fragmentToNavigate) {
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_wrapper, fragmentToNavigate)
                .commit();
    }
}
