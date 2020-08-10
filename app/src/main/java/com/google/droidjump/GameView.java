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
import com.google.droidjump.models.Animative;
import com.google.droidjump.models.Bat;
import com.google.droidjump.models.Droid;
import com.google.droidjump.models.Obstacle;
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
    private int levelTimePoints;
    private int levelSpeed;
    private List<Obstacle> obstacleList;

    public GameView(Context context, int screenX, int screenY, boolean isPlaying) {
        super(context);
        activity = (MainActivity) context;
        timePoint = 0;
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

    public GameView(Context context) {
        super(context);
        isPlaying = false;
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

    public void updateGameState() {
        // TODO: Check if time point is in level data and add data to some container, than move
        //  it to left
        updateDroidCoordinates();
        updateObstaclesCoordinates();

        // Level finishing.
        if (timePoint == levelTimePoints) {
            winGame();
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

    public void animateGameItem(Animative object) {
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
        // TODO: Serialize current level data and put it in some container
        levelTimePoints = 200;
        levelSpeed = 50;
        obstacleList = new LinkedList<>();
        // Example of bat animation: TODO: Replace a hardcoded obstacleList with data from JSON
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
