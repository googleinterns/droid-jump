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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents obstacle Bat.
 */
public class Bat extends DrawableElement {
    private Wing wing;
    private List<Bitmap> batTypes;

    public Bat(int x, int y, Resources resources) {
        super(x, y);
        wing = Wing.left;
        // Extracting bat types from full bat picture.
        Bitmap fullBatPicture = BitmapFactory.decodeResource(resources, R.mipmap.bat);
        int halfWidth = fullBatPicture.getWidth() / 2;
        Bitmap leftBatWingPicture = Bitmap.createBitmap(fullBatPicture, 0, 0, halfWidth, fullBatPicture.getHeight());
        Bitmap rightBatWingPicture = Bitmap.createBitmap(fullBatPicture, halfWidth, 0, halfWidth, fullBatPicture.getHeight());
        batTypes = new ArrayList<>();
        batTypes.add(leftBatWingPicture);
        batTypes.add(rightBatWingPicture);
        bitmap = leftBatWingPicture;
    }

    public Bitmap getBitmap() {
        bitmap = batTypes.get(wing.getValue());
        // Changing a wing to opposite
        wing = wing == Wing.left ? Wing.right : Wing.left;
        return bitmap;
    }

    private void changeWing() {
        if (wing == Wing.left) {
            wing = Wing.right;
        } else {
            wing = Wing.left;
        }
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
