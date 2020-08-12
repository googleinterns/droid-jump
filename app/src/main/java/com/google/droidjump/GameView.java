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
import static com.google.droidjump.GameConstants.GAME_LEVEL_HEADER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.droidjump.leveldata.Level;
import com.google.droidjump.leveldata.LevelData;
import com.google.droidjump.leveldata.ObstacleType;
import com.google.droidjump.models.Bat;
import com.google.droidjump.models.Droid;
import com.google.droidjump.models.Obstacle;
import com.google.droidjump.models.TwoStepAnimative;
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
    private LevelData levelData;

    public GameView(Context context, int screenX, int screenY, boolean isPlaying) {
        super(context);
        levelData = new LevelData(Level.LEVEL1, getResources());
        intervalTimePoint = GameConstants.INTERVAL_START_TIME;
        receiveLevelDetails();
        timePoint = GameConstants.INTERVAL_START_TIME;
        activity = (MainActivity) context;
        surfaceHolder = getHolder();
        currentLevel = activity.getCurrentLevel();
        this.screenX = screenX;
        this.screenY = screenY;
        this.isPlaying = isPlaying;
        receiveLevelDetails();
        levelPaint = createLevelPaint();
        screenMargin = (int) getResources().getDimension(R.dimen.fab_margin);
        int droidY = screenY - screenMargin;
        droid = new Droid(/* x= */ screenMargin, droidY, getResources());
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

    private void updateGameState() {
        checkTimePoint();
        updateDroidCoordinates();
        updateObstaclesCoordinates();
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
            String levelHeader = String.format("%s %d", GAME_LEVEL_HEADER, currentLevel);
            float levelPaintY = screenMargin + levelPaint.getTextSize();
            canvas.drawText(/* text= */ levelHeader, /* x= */ screenMargin, levelPaintY, levelPaint);
            drawDroid(canvas);
            drawObstacles(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void updateObstaclesCoordinates() {
        for (Obstacle obstacle : obstacleList) {
            if (obstacle instanceof Bat) {
                Bat bat = (Bat) obstacle;
                animateGameItem(bat);
            }
            obstacle.setX(obstacle.getX() - levelSpeed);
        }
    }

    private void animateGameItem(TwoStepAnimative object) {
        if (timePoint % GameConstants.FULL_ANIMATION_TICKS < GameConstants.ANIMATION_STEP_TICKS) {
            object.useFirstStepBitmap();
        } else {
            object.useSecondStepBitmap();
        }
    }

    private void drawObstacles(Canvas canvas) {
        for (Obstacle obstacle : obstacleList) {
            canvas.drawBitmap(obstacle.getBitmap(), obstacle.getX(), obstacle.getY(), /* paint= */ null);
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
        // TODO: Serialize current level data and put it in some container (Assigned to Daria)
        levelTimePoints = 200;
        levelSpeed = 50;
        obstacleList = new LinkedList<>();
        // Example of bat animation: TODO: Replace a hardcoded obstacleList with data from JSON (Assigned to Daria)
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
}
