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

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.google.droidjump.R;

/**
 * Shows a placeholder while data is fetching.
 */
public class LoadingHelper {
    public static void onLoading(Context context, View view, int layoutId) {
        view.findViewById(layoutId).setVisibility(View.GONE);
        View loadingView = view.findViewById(R.id.loading_layout);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.placeholder);
        loadingView.startAnimation(animation);
        loadingView.setVisibility(View.VISIBLE);
    }

    public static void onLoaded(View view, int layoutId) {
        view.findViewById(layoutId).setVisibility(View.VISIBLE);
        View loadingView = view.findViewById(R.id.loading_layout);
        loadingView.clearAnimation();
        loadingView.setVisibility(View.GONE);
    }
}
