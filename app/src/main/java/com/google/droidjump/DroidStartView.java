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

import static android.graphics.Bitmap.createBitmap;
import static android.graphics.BitmapFactory.decodeResource;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class DroidStartView extends View {

    public DroidStartView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap fullDroidPic = decodeResource(getResources(), R.mipmap.droid);
        int droidCountOnFullDroidPic = 9;
        int droidStep = (int) fullDroidPic.getWidth() / droidCountOnFullDroidPic;
        int droidCharacterStandingNumber = 4;
        Bitmap droid = createBitmap(fullDroidPic, /* 4th droid on full droid picture */droidStep * droidCharacterStandingNumber, 0, droidStep,
                fullDroidPic.getHeight());
        canvas.drawBitmap(droid, 0, getHeight() - droid.getHeight(), /* paint= */null);
    }
}
