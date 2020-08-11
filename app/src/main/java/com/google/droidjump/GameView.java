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
import com.google.droidjump.leveldata.Level;
import com.google.droidjump.leveldata.LevelData;
import com.google.droidjump.leveldata.ObstacleType;

public class GameView extends SurfaceView implements Runnable {

    private boolean isPlaying;
    private int screenX;
    private int screenY;
    private int screenMargin;
    private SurfaceHolder surfaceHolder;
    private Droid droid;
    private int timePoint;
    private int intervalTimePoint;
    private Thread thread;
    private int levelTimePoints;
    private int levelSpeed;
    private LevelData levelData;

    public GameView(Context context) {
        super(context);
        isPlaying = false;
    }

    public GameView(Context context, int screenX, int screenY, boolean isPlaying) {
        super(context);
        levelData = new LevelData(Level.LEVEL1, getResources());
        intervalTimePoint = GameConstants.INTERVAL_START_TIME;
        receiveLevelDetails();
        timePoint = GameConstants.INTERVAL_START_TIME;
        surfaceHolder = getHolder();
        this.screenX = screenX;
        this.screenY = screenY;
        this.isPlaying = isPlaying;

        // Margin in px
        screenMargin = (int) getResources().getDimension(R.dimen.fab_margin);

        // Create droid
        droid = new Droid(screenMargin, screenY - screenMargin, getResources());
    }

    @Override
    public void run() {
        while (isPlaying) {
            updateGameState();
            drawScene();
            sleep();
            timePoint++;
            intervalTimePoint++;
        }
    }

    private void receiveLevelDetails() {
        // TODO: Serialize current level data and put it in some container
        levelTimePoints = 200;
        levelSpeed = levelData.getBaseSpeed();
    }

    public void updateGameState() {
        checkTimePoint();
        updateDroidCoordinates();
    }

    private void checkTimePoint() {
        if (levelData.isEmpty()) {

            // When the obstacles end - the level is considered passed.
            winGame();
            return;
        }

        if (intervalTimePoint == levelData.getCurrentTimeInterval()) {
            //  This is just an example of how we can get
            //  info about an obstacle that should appear at the moment.
            ObstacleType newObstacleType = levelData.getNewObstacleType();
            intervalTimePoint = GameConstants.INTERVAL_START_TIME;
        }
    }

    private void updateDroidCoordinates() {
        if (droid.isJumping()) {
            if (droid.getY() + droid.getHeight() < droid.getInitialY() - droid.getJumpHeight()) {
                droid.setJumping(false);
            } else {
                droid.useJumpingBitmap();

                // Increasing droid Y position to make they jump smoothly
                droid.setY(droid.getY() - levelSpeed * 2);
            }
        }
        if (!droid.isJumping() && droid.getY() == droid.getInitialY()) {
            // Droid Animation
            if (timePoint % Droid.fullAnimationTicks < Droid.animationStepTicks) {
                droid.useFirstStepBitmap();
            } else {
                droid.useSecondStepBitmap();
            }
        }

        // Droid Gravity
        if (droid.getY() != droid.getInitialY()) {
            // Decreasing droid Y position to made they jump smoothly
            droid.setY(Math.min(droid.getY() + levelSpeed,
                    droid.getInitialY()));
        }
    }

    public void sleep() {
        try {
            Thread.sleep(GameConstants.SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void drawScene() {
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
        canvas.drawBitmap(droid.getBitmap(), droid.getX(), droid.getY(), /* paint= */ null);
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
