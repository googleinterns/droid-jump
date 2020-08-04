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
 *
 * @author maksme@google.com
 */
public class Droid extends DrawableElement {

    // the variable is responsible for how much time ticks full animation of droid goes
    public final static int fullAnimationTicks = 4;
    // the variable is responsible for how much time ticks must pass to change a bitmap so that animate droid smoothly
    public final static int animationStepTicks = 2;
    // so that droid can easily jump over all obstacles we need to add two measures: the highest obstacle + additional height
    private final static int additionalHeight = 50;
    private boolean isJumping;
    private boolean isCrouching;
    private Bitmap[] droidTypes;
    private int initialY;
    private int jumpHeight;


    public Droid(int x, int y, Resources resources) {
        super(x, y);
        Bitmap fullDroidPicture = BitmapFactory.decodeResource(resources, R.mipmap.droid);
        int droidCount = GameConstants.DROID_COUNT_ON_FULL_DROID_PICTURE;
        droidTypes = new Bitmap[droidCount];
        int droidWidth = fullDroidPicture.getWidth() / droidCount;
        int droidHeight = fullDroidPicture.getHeight();
        for (int i = 0; i < droidCount; i++) {
            droidTypes[i] = Bitmap.createBitmap(fullDroidPicture, /* x= */ droidWidth * i,
                    /* y= */ 0, droidWidth, droidHeight);
        }
        bitmap = droidTypes[GameConstants.DROID_FIRST_STEP_INDEX];
        this.y -= bitmap.getHeight();
        initialY = getY();
        setJumpHeight(resources);
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    public boolean isCrouching() {
        return isCrouching;
    }

    public void setCrouching(boolean crouching) {
        isCrouching = crouching;
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

    private void setJumpHeight(Resources resources) {
        /* Returning the highest obstacle height + additional distance for jumping so that droid
        can easily jump through all obstacles */
        Bitmap palm = BitmapFactory.decodeResource(resources, R.mipmap.palm);
        jumpHeight = palm.getHeight() + additionalHeight;
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
}
