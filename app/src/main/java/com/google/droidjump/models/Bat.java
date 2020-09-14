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

import android.content.res.Resources;
import android.graphics.Bitmap;
import com.google.droidjump.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents obstacle Bat.
 */
public class Bat extends Obstacle implements TwoStepAnimative {
    private List<Bitmap> batTypes;

    public Bat(int x, int yWithBitmapOffset, Resources resources) {
        super(x, yWithBitmapOffset, R.drawable.bat, resources);
        // Extracting bat types from full bat picture.
        Bitmap fullBatPicture = GameItem.drawableToBitmap(resources.getDrawable(R.drawable.new_bat));
        int halfWidth = fullBatPicture.getWidth() / 2;
        Bitmap leftBatWingPicture = Bitmap.createBitmap(fullBatPicture, 0, 0, halfWidth, fullBatPicture.getHeight());
        Bitmap rightBatWingPicture = Bitmap.createBitmap(fullBatPicture, halfWidth, 0, halfWidth, fullBatPicture.getHeight());
        batTypes = new ArrayList<>();
        batTypes.add(leftBatWingPicture);
        batTypes.add(rightBatWingPicture);
        picture = leftBatWingPicture;
    }

    public void useFirstStepBitmap() {
        picture = batTypes.get(0);
    }

    public void useSecondStepBitmap() {
        picture = batTypes.get(1);
    }
}
