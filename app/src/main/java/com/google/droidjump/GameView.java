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
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.droidjump.models.Droid;

/**
 * Shows main game process.
 */
public class GameView extends SurfaceView implements Runnable {

    private boolean isPlaying;
    private int screenX;
    private int screenY;
    private int screenMargin;
    private SurfaceHolder surfaceHolder;
    private Droid droid;
    private int timePoint;
    private Thread thread;
    private int levelTimePoints;
    private int levelSpeed;

    public GameView(Context context) {
        super(context);
        isPlaying = false;
    }

    public GameView(Context context, int screenX, int screenY, boolean isPlaying) {
        super(context);
        receiveLevelDetails();
        timePoint = 0;
        surfaceHolder = getHolder();
        this.screenX = screenX;
        this.screenY = screenY;
        this.isPlaying = isPlaying;
        screenMargin = (int) getResources().getDimension(R.dimen.fab_margin);
        droid = new Droid(screenMargin, screenY - screenMargin, getResources());
    }

    @Override
    public void run() {
        while (isPlaying) {
            updateGameState();
            drawScene();
            sleep();
            timePoint++;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!droid.isJumping() && droid.getY() == droid.getInitialY()) {
            droid.setJumping(true);
        }
        return super.onTouchEvent(event);
    }

    private void updateGameState() {
        // TODO: Check if time point is in level data and add data to some container, than move
        //  it to left
        updateDroidCoordinates();
        // Level Finishing.
        if (timePoint == levelTimePoints) {
            winGame();
        }
    }

    private void updateDroidCoordinates() {
        if (droid.isJumping()) {
            if (droid.getY() + droid.getHeight() < droid.getInitialY() - droid.getJumpHeight()) {
                droid.setJumping(false);
            } else {
                droid.useJumpingBitmap();
                // Increasing droid Y position to make them jump smoothly.
                droid.setY(droid.getY() - levelSpeed * 2);
            }
        }
        if (!droid.isJumping() && droid.getY() == droid.getInitialY()) {
            // Droid Animation.
            if (timePoint % Droid.fullAnimationTicks < Droid.animationStepTicks) {
                droid.useFirstStepBitmap();
            } else {
                droid.useSecondStepBitmap();
            }
        }

        // Droid Gravity.
        if (droid.getY() != droid.getInitialY()) {
            // Decreasing droid Y position to made they jump smoothly.
            droid.setY(Math.min(droid.getY() + levelSpeed,
                    droid.getInitialY()));
        }
    }

    private void sleep() {
        try {
            Thread.sleep(GameConstants.SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void drawScene() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawColor(Color.WHITE);
            drawDroid(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void failGame() {
        findNavController(this).navigate(R.id.action_game_screen_to_game_failure_screen);
    }

    private void winGame() {
        findNavController(this).navigate(R.id.action_game_screen_to_game_success_screen);
    }

    private void drawDroid(Canvas canvas) {
        canvas.drawBitmap(droid.getBitmap(), droid.getX(), droid.getY(), /* paint= */ null);
    }

    private void receiveLevelDetails() {
        // TODO: Serialize current level data and put it in some container
        levelTimePoints = 200;
        levelSpeed = 50;
    }
}
