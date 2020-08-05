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

/**
 * Represents Droid character.
 */
public class Droid extends DrawableElement {

    private boolean isJumping;
    private boolean isCrouching;
    private Bitmap[] droidTypes;
    private int initialY;
    private int jumpHeight;

    // The variable is responsible for how much time ticks full animation of droid goes
    public final static int fullAnimationTicks = 4;
    // The variable is responsible for how much time ticks must pass to change a bitmap so that animate droid smoothly
    public final static int animationStepTicks = 2;
    // So that droid can easily jump over all obstacles we need to add two measures: the highest obstacle + additional height
    private final static int additionalHeight = 50;

    public Droid(int x, int y, Resources resources) {
        super(x, y);
        Bitmap fullDroidPicture = BitmapFactory.decodeResource(resources, R.mipmap.droid);
        int droidCount = GameConstants.DROID_COUNT_ON_FULL_DROID_PICTURE;
        droidTypes = new Bitmap[droidCount];

        int fullDroidPictureWidth = fullDroidPicture.getWidth();
        int fullDroidPictureHeight = fullDroidPicture.getHeight();
        int droidWidth = fullDroidPictureWidth / droidCount;
        for (int i = 0; i < droidCount; i++) {
            int droidX = droidWidth * i;
            int droidY = 0;
            droidTypes[i] = Bitmap.createBitmap(fullDroidPicture, droidX,
                    droidY, droidWidth, fullDroidPictureHeight);
        }
        bitmap = droidTypes[GameConstants.DROID_FIRST_STEP_INDEX];
        int bitmapHeight = bitmap.getHeight();
        this.y -= bitmapHeight;
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

    public Bitmap[] getDroidTypes() {
        return droidTypes;
    }

    public int getInitialY() {
        return initialY;
    }

    public int getJumpHeight() {
        return jumpHeight;
    }

    public void useJumpingBitmap() {
        bitmap = getDroidTypes()[GameConstants.DROID_JUMPING_CHARACTER_INDEX];
    }

    public void useFirstStepBitmap() {
        bitmap = getDroidTypes()[GameConstants.DROID_FIRST_STEP_INDEX];
    }

    public void useSecondStepBitmap() {
        bitmap = getDroidTypes()[GameConstants.DROID_SECOND_STEP_INDEX];
    }

    private void setJumpHeight(Resources resources) {
        /* Returning the sum of highest obstacle height and additional jumping distance so that droid
        can easily jump through all obstacles */
        Bitmap palm = BitmapFactory.decodeResource(resources, R.mipmap.palm);
        jumpHeight = palm.getHeight() + additionalHeight;
    }
}
