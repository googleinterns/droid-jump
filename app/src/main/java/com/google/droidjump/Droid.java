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

    private boolean isJumping;
    private boolean isCrouching;
    private Bitmap[] droidTypes;
    private int initialY;
    private int jumpHeight;

    public Droid(int x, int y, int jumpHeight, Resources resources) {
        super(x, y);
        Bitmap fullDroidPicture = BitmapFactory.decodeResource(resources, R.mipmap.droid);
        int droidCount = 9;
        droidTypes = new Bitmap[droidCount];
        int droidWidth = fullDroidPicture.getWidth() / droidCount;
        int droidHeight = fullDroidPicture.getHeight();
        for (int i = 0; i < droidCount; i++) {
            droidTypes[i] = Bitmap.createBitmap(fullDroidPicture, droidWidth * i, 0, droidWidth,
                    droidHeight);
        }
        setBitmap(droidTypes[5]);
        this.setY(y - getBitmap().getHeight());
        initialY = getY();
        this.jumpHeight = jumpHeight;
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

}
