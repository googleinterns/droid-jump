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

import static androidx.navigation.Navigation.findNavController;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    private boolean isPlaying;
    private int screenX;
    private int screenY;
    private int screenMargin;
    private SurfaceHolder surfaceHolder;
    private MainActivity activity;
    private Droid droid;
    private int timePoint;
    Thread thread;
    private int levelTimePoints;
    private int levelSpeed;

    public GameView(Context context) {
        super(context);
        isPlaying = false;
    }

    public GameView(Context context, int screenX, int screenY, boolean isPlaying) {
        super(context);
        timePoint = 0;
        surfaceHolder = getHolder();
        activity = (MainActivity) context;
        this.screenX = screenX;
        this.screenY = screenY;
        this.isPlaying = isPlaying;

        // Margin in px
        screenMargin = (int) getResources().getDimension(R.dimen.fab_margin);
        // Measuring droid jump height
        DrawableElement palm = new DrawableElement(/* x= */0, /* y= */0,
                /* bitmap= */BitmapFactory.decodeResource(getResources(), R.mipmap.palm));
        int jumpHeight = palm.getHeight() + 100;
        // Create droid
        droid = new Droid(screenMargin, screenY - screenMargin, jumpHeight, getResources());
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
        // TODO: Serialize current level data and put it in some container
        levelTimePoints = 200;
        levelSpeed = 3;
    }

    public void update() {
        // TODO: Check if time point is in level data and add data to some container, than move
        //  it to left
        if (droid.isJumping()) {
            if (droid.getY() < droid.getInitialY() - droid.getJumpHeight()) {
                droid.setJumping(false);
            } else {
                // Setting 4th droid character(droid in jump)
                droid.setBitmap(droid.getDroidTypes()[GameConstants.DROID_JUMPING_CHARACTER_INDEX]);
                droid.setY(droid.getY() - droid.getJumpHeight() / 3);
            }
        } else if (droid.getY() == droid.getInitialY()) {
            // Droid Animating
            if (timePoint % 5 < 2) {
                droid.setBitmap(droid.getDroidTypes()[GameConstants.DROID_FIRST_STEP_INDEX]);

            } else {
                droid.setBitmap(droid.getDroidTypes()[GameConstants.DROID_SECOND_STEP_INDEX]);
            }
        }
        // Droid Gravity
        if (droid.getY() != droid.getInitialY()) {
            droid.setY(Math.min(droid.getY() + droid.getJumpHeight() / 6, droid.getInitialY()));
        }
        // Level Finishing
        if (timePoint == levelTimePoints) {
            failGame();
        }
    }

    public void sleep() {
        try {
            Thread.sleep(GameConstants.SLEEP_TIME);
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

    public void failGame() {
        findNavController(this).navigate(R.id.action_game_screen_to_game_failure_screen);
    }

    public void winGame() {
        findNavController(this).navigate(R.id.action_game_screen_to_game_success_screen);
    }

    private void drawDroid(Canvas canvas) {
        canvas.drawBitmap(droid.getBitmap(), droid.getX(), droid.getY(), /* paint= */null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!droid.isJumping() && droid.getY() == droid.getInitialY()) {
            droid.setJumping(true);
        }
        return super.onTouchEvent(event);
    }
}
