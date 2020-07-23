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
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class DroidStartView extends View {

    public DroidStartView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        @SuppressLint("DrawAllocation") Bitmap fullDroidPic = BitmapFactory.decodeResource(getResources(), R.mipmap.droid);
        int droidStep = (int) fullDroidPic.getWidth() / 9;
        @SuppressLint("DrawAllocation") Bitmap droid = Bitmap.createBitmap(fullDroidPic, droidStep * 4, 0, droidStep, fullDroidPic.getHeight(), null, true);
        canvas.drawBitmap(droid, 0, getHeight() - droid.getHeight(), null);
    }
}
