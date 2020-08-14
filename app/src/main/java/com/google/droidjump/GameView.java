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

import static com.google.droidjump.GameConstants.GAME_LEVEL_HEADER;
import static com.google.droidjump.GameConstants.GROUND_PROPORTION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.droidjump.leveldata.InfiniteLevel;
import com.google.droidjump.leveldata.Level;
import com.google.droidjump.leveldata.LevelStrategy;
import com.google.droidjump.leveldata.ObstacleType;
import com.google.droidjump.models.Bat;
import com.google.droidjump.models.Droid;
import com.google.droidjump.models.LevelManager;
import com.google.droidjump.models.NavigationHelper;
import com.google.droidjump.models.Obstacle;
import com.google.droidjump.models.TwoStepAnimative;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Shows main game process.
 */
public class GameView extends SurfaceView implements Runnable {
    private MainActivity activity;
    private SurfaceHolder surfaceHolder;
    private Droid droid;
    private Thread thread;
    private Paint levelPaint;
    private boolean isPlaying;
    private int currentLevel;
    private int screenX;
    private int screenY;
    private int screenMargin;
    private int timePoint;
    private int intervalTimePoint;
    private int levelTimePoints;
    private int levelSpeed;
    private List<Obstacle> obstacleList;
    private LevelStrategy level;
    private int platformX = 0;
    private Bitmap platform = BitmapFactory.decodeResource(getResources(), R.mipmap.platform);

    public GameView(Context context, int screenX, int screenY, boolean isPlaying) {
        super(context);
        intervalTimePoint = GameConstants.INTERVAL_START_TIME;
        // TODO: Later, based on the type of level, implement the choice of the strategy
        level = new InfiniteLevel(Level.INFINITE, getResources());
        receiveLevelDetails();
        timePoint = GameConstants.INTERVAL_START_TIME;
        activity = (MainActivity) context;
        surfaceHolder = getHolder();
        currentLevel = LevelManager.getCurrentLevel();
        this.screenX = screenX;
        this.screenY = screenY;
        this.isPlaying = isPlaying;
        receiveLevelDetails();
        levelPaint = createLevelPaint();
        screenMargin = (int) getResources().getDimension(R.dimen.fab_margin);

        // Handpicked value
        // Droid should be on a ground height, but platform includes grass.
        int groundHeight = (int) (platform.getHeight() * GROUND_PROPORTION);

        int droidY = screenY - screenMargin;
        droid = new Droid(screenMargin, screenY - groundHeight, getResources());
    }

    @Override
    public void run() {
        while (isPlaying) {
            updateGameState();
            drawScene();
            handleCollision();
            sleep();
            timePoint++;
            intervalTimePoint++;
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

    private void checkTimePoint() {
        if (level.isEmpty()) {
            // When the obstacles end - the level is considered passed.
            winGame();
            return;
        }

        if (intervalTimePoint == level.getCurrentTimeInterval()) {
            ObstacleType newObstacleType = level.getNewObstacleType();
            intervalTimePoint = GameConstants.INTERVAL_START_TIME;
        }
    }

    private void updateGameState() {
        checkTimePoint();
        updateDroidCoordinates();
        updateObstaclesCoordinates();
        updatePlatformCoordinates();
        // Level Finishing
        if (timePoint == levelTimePoints) {
            winGame();
        }
    }

    private void updateObstaclesCoordinates() {
        Iterator<Obstacle> it = obstacleList.iterator();
        while (it.hasNext()) {
            Obstacle obstacle = it.next();
            // Animating bat.
            if (obstacle instanceof Bat) {
                Bat bat = (Bat) obstacle;
                animateGameItem(bat);
            }
            // Moving obstacles to the left.
            obstacle.setX(obstacle.getX() - levelSpeed);
            // Removal of passed obstacles
            if (obstacle.getX() + obstacle.getWidth() < 0) {
                it.remove();
            }
        }
    }

    private void updateDroidCoordinates() {
        if (droid.isJumping()) {
            if (droid.getY() + droid.getHeight() < droid.getInitialY() - droid.getJumpHeight()) {
                droid.setJumping(false);
            } else {
                droid.useJumpingBitmap();
                // Increasing droid Y position to make they jump smoothly.
                droid.setY(droid.getY() - levelSpeed * 2);
            }
        }
        if (!droid.isJumping() && droid.getY() == droid.getInitialY()) {
            // Droid Animation.
            animateGameItem(droid);
        }
        if (droid.getY() != droid.getInitialY()) {
            // Decreasing droid Y position to made they jump smoothly.
            int newDroidY = droid.getY() + levelSpeed;
            droid.setY(Math.min(newDroidY, droid.getInitialY()));
        }
    }

    public void updatePlatformCoordinates() {
        // The leftmost coordinate where the new platform starts
        platformX = (platformX - levelSpeed) % platform.getWidth();
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
            // Cleaning previous canvas
            canvas.drawColor(Color.WHITE);
            String levelHeader = String.format("%s %d", GAME_LEVEL_HEADER, currentLevel);
            float levelPaintY = screenMargin + levelPaint.getTextSize();
            canvas.drawText(/* text= */ levelHeader, /* x= */ screenMargin, levelPaintY, levelPaint);
            drawDroid(canvas);
            drawObstacles(canvas);
            drawPlatform(canvas);

            // Drawing canvas with all elements
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void animateGameItem(TwoStepAnimative object) {
        if (timePoint % GameConstants.FULL_ANIMATION_TICKS < GameConstants.ANIMATION_STEP_TICKS) {
            object.useFirstStepBitmap();
        } else {
            object.useSecondStepBitmap();
        }
    }

    private void failGame() {
        isPlaying = false;
        NavigationHelper.navigateToFragment(activity, new GameFailureFragment());
    }

    private void winGame() {
        isPlaying = false;
        NavigationHelper.navigateToFragment(activity, new GameSuccessFragment());
    }

    private void drawDroid(Canvas canvas) {
        canvas.drawBitmap(droid.getBitmap(), droid.getX(), droid.getY(), /* paint= */ null);
    }

    private void drawPlatform(Canvas canvas) {
        int platformY = screenY - platform.getHeight();
        for (int curPlatformX = platformX; curPlatformX < screenX; curPlatformX += platform.getWidth()) {
            canvas.drawBitmap(platform, curPlatformX, platformY, /* paint= */ null);
        }
    }

    private void drawObstacles(Canvas canvas) {
        for (Obstacle obstacle : obstacleList) {
            canvas.drawBitmap(obstacle.getBitmap(), obstacle.getX(), obstacle.getY(), /* paint= */ null);
        }
    }

    private void receiveLevelDetails() {
        // TODO(dnikolskaia): Serialize current level data and put it in some container.
        levelTimePoints = 200;
        levelSpeed = level.getBaseSpeed();
        obstacleList = new LinkedList<>();
        int batY = screenY - 700; // random hardcoded value
        obstacleList.add(new Bat(screenX, batY, getResources()));
    }

    private Paint createLevelPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        float headerTextSize = getResources().getDimension(R.dimen.header_text_size);
        paint.setTextSize(headerTextSize);
        return paint;
    }

    public void handleCollision() {
        for (Obstacle obstacle : obstacleList) {
            if (checkIntersection(droid, obstacle)) {
                failGame();
            }
        }
    }

    private boolean checkIntersection(Droid droid, Obstacle obstacle) {
        return !(droid.getY() > obstacle.getY() + obstacle.getHeight()
                || droid.getY() + droid.getHeight() < obstacle.getY()
                || droid.getX() > obstacle.getX() + obstacle.getWidth()
                || droid.getX() + droid.getWidth() < obstacle.getX());
    }
}
