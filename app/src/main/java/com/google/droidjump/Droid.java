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

public class Droid extends DrawableElement {

    private final int speed;
    private boolean isJumping;
    private boolean isCrouching;
    private Bitmap[] droidTypes;
    private int initialY;
    private int jumpHeight;
    private int stepCount;

    public Droid(int x, int y, int speed, Resources resources) {
        super(x, y);
        Bitmap fullDroidPicture = BitmapFactory.decodeResource(resources, R.mipmap.droid);
        int droidCount = GameConstants.DROID_COUNT_ON_FULL_DROID_PICTURE;
        droidTypes = new Bitmap[droidCount];
        int droidWidth = fullDroidPicture.getWidth() / droidCount;
        int droidHeight = fullDroidPicture.getHeight();
        for (int i = 0; i < droidCount; i++) {
            droidTypes[i] = Bitmap.createBitmap(fullDroidPicture, /* x= */ droidWidth * i, /* y= */ 0,
                    droidWidth,
                    droidHeight);
        }
        setBitmap(droidTypes[GameConstants.DROID_FIRST_STEP_INDEX]);
        this.setY(y - getBitmap().getHeight());
        initialY = getY();
        this.speed = speed;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public boolean isCrouching() {
        return isCrouching;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
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

    public void setJumpHeight(int jumpHeight) {
        this.jumpHeight = jumpHeight;
    }

    public void update() {
        if (this.isJumping()) {
            if (this.getY() < this.getInitialY() - this.getJumpHeight()) {
                setJumping(false);
            } else {
                this.setBitmap(getDroidTypes()[GameConstants.DROID_JUMPING_CHARACTER_INDEX]);

                // Increasing droid Y position to jump smoothly
                setY(getY() - speed * 2);
            }
        } else if (getY() == getInitialY()) {
            // Droid Animating
            if (stepCount > 5) {
                setBitmap(getDroidTypes()[GameConstants.DROID_FIRST_STEP_INDEX]);
            } else {
                setBitmap(getDroidTypes()[GameConstants.DROID_SECOND_STEP_INDEX]);
            }
        }

        // Droid Gravity
        if (getY() != getInitialY()) {
            // decreasing droid Y position to jump smoothly
            setY(Math.min(getY() + speed,
                    getInitialY()));
        }

        stepCount++;
        if (stepCount > 10) {
            stepCount = 1;
        }
    }

    public void setDroidJumpHeight(Resources resources) {
        /* Returning the highest obstacle height + additional distance for jumping so that droid
        can easily jump through all obstacles */
        Bitmap palm = BitmapFactory.decodeResource(resources, R.mipmap.palm);
        jumpHeight = palm.getHeight() + this.getHeight() + (palm.getHeight() / 5);
    }
}
