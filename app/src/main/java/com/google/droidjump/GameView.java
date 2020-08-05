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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.droidjump.models.Cactus;
import com.google.droidjump.models.Droid;
import com.google.droidjump.models.Obstacle;
import com.google.droidjump.models.Palm;
import java.util.LinkedList;
import java.util.List;

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
    private int platformX = 0;
    private Bitmap platform = BitmapFactory.decodeResource(this.getResources(), R.mipmap.platform);
    private List<Obstacle> obstacleList;

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

        // Margin in px
        screenMargin = (int) getResources().getDimension(R.dimen.fab_margin);

        // Create droid
        droid = new Droid(screenMargin, screenY - screenMargin, getResources());

        // Create obstacle list (just for animation example)
        Cactus cactus = new Cactus(screenX, screenY - screenMargin, getResources());
        Palm palm = new Palm(screenX + 2000, screenY - screenMargin, getResources());
        obstacleList = new LinkedList<>();
        obstacleList.add(palm);
        obstacleList.add(cactus);

        platformX = levelSpeed;
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

    private void receiveLevelDetails() {
        // TODO: Serialize current level data and put it in some container
        levelTimePoints = 200;
        levelSpeed = 20;
    }

    public void updateGameState() {
        // TODO: Check if time point is in level data and add data to some container, than move
        //  it to left
        updateDroidCoordinates();
        updateObstaclesCoordinates();
        updatePlatformX();
        // Level Finishing
        if (timePoint == levelTimePoints) {
            winGame();
        }
    }

    private void updateObstaclesCoordinates() {
        for (Obstacle obstacle : obstacleList) {
            obstacle.setX(obstacle.getX() - levelSpeed);
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

    public void updatePlatformX() {
        platformX = (platformX - levelSpeed) % platform.getWidth();
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

            // Drawing Obstacles
            drawObstacles(canvas);

            // Drawing platform
            drawPlatform(canvas);

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

    private void drawPlatform(Canvas canvas) {
        int platformY = screenY - platform.getHeight();
        for (int curPlatformX = platformX; curPlatformX < screenX; curPlatformX += platform.getWidth())
            canvas.drawBitmap(platform, curPlatformX, platformY, null);
    }

    private void drawObstacles(Canvas canvas) {
        for (Obstacle obstacle : obstacleList) {
            canvas.drawBitmap(obstacle.getBitmap(), obstacle.getX(), obstacle.getY(), null);
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
}
