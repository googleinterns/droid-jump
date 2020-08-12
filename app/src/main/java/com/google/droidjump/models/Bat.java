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
import android.graphics.BitmapFactory;
import com.google.droidjump.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents obstacle Bat.
 */
public class Bat extends Obstacle implements TwoStepAnimative {

    private List<Bitmap> batTypes;

    public Bat(int x, int y, Resources resources) {
        super(x, y, R.mipmap.bat, resources);
        // Extracting bat types from full bat picture.
        Bitmap fullBatPicture = BitmapFactory.decodeResource(resources, R.mipmap.bat);
        int halfWidth = fullBatPicture.getWidth() / 2;
        Bitmap leftBatWingPicture = Bitmap.createBitmap(fullBatPicture, 0, 0, halfWidth, fullBatPicture.getHeight());
        Bitmap rightBatWingPicture = Bitmap.createBitmap(fullBatPicture, halfWidth, 0, halfWidth, fullBatPicture.getHeight());
        batTypes = new ArrayList<>();
        batTypes.add(leftBatWingPicture);
        batTypes.add(rightBatWingPicture);
        picture = leftBatWingPicture;
    }

    public Bitmap getBitmap() {
        return picture;
    }

    public void useFirstStepBitmap() {
        picture = batTypes.get(Wing.left.value);
    }

    public void useSecondStepBitmap() {
        picture = batTypes.get(Wing.right.value);
    }

    private enum Wing {
        left(0),
        right(1);

        private int value;

        Wing(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
