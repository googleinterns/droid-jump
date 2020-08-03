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
        Bitmap fullDroidPicture = decodeResource(getResources(), R.mipmap.droid);
        int droidWidth =
                fullDroidPicture.getWidth() / GameConstants.DROID_COUNT_ON_FULL_DROID_PICTURE;
        int jumpingDroidPosition = droidWidth * GameConstants.DROID_JUMPING_CHARACTER_INDEX;
        Bitmap droid = createBitmap(fullDroidPicture, jumpingDroidPosition, /* y= */ 0,
                /* width= */ droidWidth,
                /* height= */ fullDroidPicture.getHeight());

        // To draw droid at the bottom of the frame we need to subtract the last frame point from droid's height
        int topMargin = getHeight() - droid.getHeight();
        canvas.drawBitmap(droid, /* left= */ 0,
                /* top= */ topMargin, /* paint= */ null);
    }
}
