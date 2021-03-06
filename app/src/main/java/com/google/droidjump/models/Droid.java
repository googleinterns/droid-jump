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
import com.google.droidjump.GameConstants;
import com.google.droidjump.R;

/**
 * Represents Droid character.
 */
public class Droid extends GameItem implements TwoStepAnimative {
    private boolean isJumping;
    private boolean isCrouching;
    private Bitmap[] droidTypes;
    private int initialY;
    private int jumpHeight;
    // So that droid can easily jump over all obstacles we need to add two measures: the highest obstacle + additional height.
    private static final int additionalHeight = 100;

    public Droid(int x, int yWithBitmapOffset, Resources resources) {
        super(x, yWithBitmapOffset, R.drawable.droid, resources);
        Bitmap fullDroidPicture = picture;
        int droidCount = GameConstants.DROID_COUNT_ON_FULL_DROID_PICTURE;
        droidTypes = new Bitmap[droidCount];
        int droidWidth = fullDroidPicture.getWidth() / droidCount;
        int droidHeight = fullDroidPicture.getHeight();
        for (int i = 0; i < droidCount; i++) {
            int droidX = droidWidth * i;
            int droidY = 0;
            droidTypes[i] = Bitmap.createBitmap(fullDroidPicture, droidX,
                    droidY, droidWidth, droidHeight);
        }
        picture = droidTypes[GameConstants.DROID_FIRST_STEP_INDEX];
        initialY = getY();
        setJumpHeight(resources);
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    public boolean isCrouching() {
        return isCrouching;
    }

    public void setCrouching(boolean isCrouching) {
        this.isCrouching = isCrouching;
    }

    public int getInitialY() {
        return initialY;
    }

    public int getJumpHeight() {
        return jumpHeight;
    }

    public void useJumpingBitmap() {
        picture = droidTypes[GameConstants.DROID_JUMPING_CHARACTER_INDEX];
    }

    public void useFirstStepBitmap() {
        picture = droidTypes[GameConstants.DROID_FIRST_STEP_INDEX];
    }

    public void useSecondStepBitmap() {
        picture = droidTypes[GameConstants.DROID_SECOND_STEP_INDEX];
    }

    private void setJumpHeight(Resources resources) {
        /* Returning the sum of highest obstacle height and additional distance for jumping so that droid
        can easily jump through all obstacles. */
        Bitmap palm = GameItem.drawableToBitmap(resources.getDrawable(R.drawable.palm));
        jumpHeight = palm.getHeight() + additionalHeight;
    }
}
