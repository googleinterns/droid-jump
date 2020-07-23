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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.constraint.solver.Metrics;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {
    private boolean isPlaying;
    private int screenX;
    private int screenY;
    private SurfaceHolder surfaceHolder;
    private MainActivity activity;
    private Droid droid;
    private int currentLevel;
    private int timePoint;
    private int levelSpeed = 10;
    Thread thread = null;

    public GameView(Context context) {
        super(context);
        isPlaying = false;
    }

    public GameView(Context context, int screenX, int screenY, boolean isPlaying) {
        super(context);
        timePoint = 0;
        surfaceHolder = getHolder();
        activity = (MainActivity) context;
        this.currentLevel = activity.getCurrentLevel();

        this.screenX = screenX;
        this.screenY = screenY;
        this.isPlaying = isPlaying;

        // Create droid
        droid = new Droid(screenX / 20, screenY - screenY / 50, getResources());
        droid.setY(droid.getY() - droid.getBitmap().getHeight());
    }


    @Override
    public void run() {
        receiveLevelDetails();
        draw();
        while (isPlaying) {
            update();
            draw();
            sleep();
            timePoint++;
        }
    }

    private void receiveLevelDetails() {
        // TODO: Serialize current level data and put in some container
    }

    public void update() {
        // TODO: Check if time point is in level data and add data to some container, than move it to left
        // Droid moving
        if (droid.isJumping()) {
            if (droid.getY() < screenY - droid.getBitmap().getHeight() - 350) {
                droid.setJumping(false);
            } else {
                droid.setBitmap(droid.getDroidTypes()[4]);
                droid.setY((int) (droid.getY() - 50));
            }
        } else {
            if (droid.getY() < screenY - droid.getBitmap().getHeight() - (screenY / 50)) {
                droid.setY((int) (droid.getY() + 50));
            }
            if (timePoint % 5 < 2) {
                droid.setBitmap(droid.getDroidTypes()[5]);

            } else {
                droid.setBitmap(droid.getDroidTypes()[6]);

            }

        }
    }

    public void sleep() {
        try {
            Thread.sleep((long) (1.0/levelSpeed * 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            // Cleaning previous canvas
            canvas.drawColor(Color.WHITE);
            // Drawing droid
            drawDroid(canvas);
            // Drawing canvas with all elements
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void drawDroid(Canvas canvas) {
        canvas.drawBitmap(droid.getBitmap(), droid.getX(), droid.getY(), null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!droid.isJumping() && droid.getY() == screenY - droid.getBitmap().getHeight() - (int) (screenY / 50)) {
            droid.setJumping(true);
        }
        return super.onTouchEvent(event);

    }
}
