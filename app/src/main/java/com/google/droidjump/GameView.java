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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.droidjump.leveldata.LevelConfig;
import com.google.droidjump.leveldata.LevelStrategy;
import com.google.droidjump.leveldata.LevelType;
import com.google.droidjump.leveldata.ObstacleType;
import com.google.droidjump.models.Bat;
import com.google.droidjump.models.Cactus;
import com.google.droidjump.models.Droid;
import com.google.droidjump.models.LevelManager;
import com.google.droidjump.models.NavigationHelper;
import com.google.droidjump.models.Obstacle;
import com.google.droidjump.models.Palm;
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
    private Paint levelPaint;
    private Bitmap platform = BitmapFactory.decodeResource(getResources(), R.mipmap.platform);
    private LevelStrategy level;
    private List<Obstacle> obstacleList;
    private Thread thread;
    private boolean isPlaying;
    private int screenX;
    private int screenY;
    private int screenMargin;
    private int timePoint;
    private int intervalTimePoint;
    private int levelSpeed;
    private int platformX = 0;
    private int groundHeight;
    private int score;
    private int cactusScore;
    private int palmScore;
    private int batScore;

    public GameView(Context context, int screenX, int screenY, boolean isPlaying) {
        super(context);
        intervalTimePoint = GameConstants.INTERVAL_START_TIME;
        timePoint = GameConstants.INTERVAL_START_TIME;
        activity = (MainActivity) context;
        surfaceHolder = getHolder();
        level = LevelManager.getCurrentLevelStrategy();
        this.screenX = screenX;
        this.screenY = screenY;
        this.isPlaying = isPlaying;
        levelPaint = createLevelPaint();
        screenMargin = (int) getResources().getDimension(R.dimen.fab_margin);
        score = 0;
        cactusScore = 0;
        receiveLevelDetails();

        // Droid should be on a ground height, but platform includes grass.
        groundHeight = (int) (platform.getHeight() * GROUND_PROPORTION);

        droid = new Droid(screenMargin, screenY - groundHeight, getResources());
        NavigationHelper.addOnBackPressedEventListener(activity, new StartFragment());
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
        if (isPlaying && !droid.isJumping() && droid.getY() == droid.getInitialY()) {
            droid.setJumping(true);
        }
        return super.onTouchEvent(event);
    }

    private void updateGameState() {
        checkTimePoint();
        updateDroidCoordinates();
        updateObstaclesCoordinates();
        updatePlatformCoordinates();
    }

    private void checkTimePoint() {
        if (level.isEmpty()) {
            // When the obstacles end - the level is considered passed.
            if (obstacleList.isEmpty()) {
                winGame();
            }
            return;
        }
        if (intervalTimePoint == level.getCurrentTimeInterval()) {
            ObstacleType newObstacleType = level.getNewObstacleType();
            // Adding new obstacle to game.
            switch (newObstacleType) {
                case CACTUS:
                    obstacleList.add(new Cactus(screenX, screenY - groundHeight, getResources()));
                    break;
                case PALM:
                    obstacleList.add(new Palm(screenX, screenY - groundHeight, getResources()));
                    break;
                case BAT:
                    // 700 - random value.
                    // TODO(Max): calculate y coordinate for bat.
                    obstacleList.add(new Bat(screenX, screenY - 700, getResources()));
                    break;
            }
            intervalTimePoint = GameConstants.INTERVAL_START_TIME;
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
            // Removal of passed obstacles.
            if (obstacle.getX() + obstacle.getWidth() < 0) {
                it.remove();
                score += 10;
                if (activity.getSavedSignedInAccount() != null) {
                    if (obstacle instanceof Cactus) {
                        cactusScore += 1;
                    } else if (obstacle instanceof Bat) {
                        batScore += 1;
                    } else if (obstacle instanceof Palm) {
                        palmScore += 1;
                    }
                }
            }
        }
    }

    private void updateObstacleLeaderboards() {
        if (activity.getSavedSignedInAccount() == null) {
            return;
        }
        LeaderboardsClient client = Games.getLeaderboardsClient(activity, activity.getSavedSignedInAccount());
        int[] leaderboards = {R.string.leaderboard_cactus_jumper, R.string.leaderboard_bat_avoider, R.string.leaderboard_palm_climber};
        Resources resources = getResources();
        for (int leaderboard : leaderboards) {
            String leaderboardId = resources.getString(leaderboard);
            client.loadCurrentPlayerLeaderboardScore(
                    leaderboardId,
                    LeaderboardVariant.TIME_SPAN_ALL_TIME,
                    LeaderboardVariant.COLLECTION_PUBLIC).addOnSuccessListener(data -> {
                        LeaderboardScore leaderboardScore = data.get();
                        long newScore;
                        switch (leaderboard) {
                            case R.string.leaderboard_cactus_jumper:
                                newScore = cactusScore;
                                break;
                            case R.string.leaderboard_bat_avoider:
                                newScore = batScore;
                                break;
                            default:
                                newScore = palmScore;
                        }
                        if (leaderboardScore != null) {
                            newScore += leaderboardScore.getRawScore();
                        }
                        client.submitScore(leaderboardId, newScore);
                    }
            );
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
            canvas.drawColor(Color.WHITE);
            String levelHeader = String.format("%s %s", GAME_LEVEL_HEADER, LevelManager.getCurrentLevelName());
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
        LevelManager.setCurrentLevelScore(score);
        NavigationHelper.navigateToFragment(activity, new GameFailureFragment());
        LevelConfig currentLevel = LevelManager.getGameLevels().get(LevelManager.getCurrentLevelIndex());
        if (currentLevel.getLevelType() == LevelType.INFINITE) {
            updateScoreInMaxScoreLeaderboard();
        }
        updateObstacleLeaderboards();
    }

    private void updateScoreInMaxScoreLeaderboard() {
        SharedPreferences gameData
                = activity.getSharedPreferences(GameConstants.GAME_VIEW_DATA, Context.MODE_PRIVATE);
        long maxScore = gameData.getLong(GameConstants.INFINITE_LEVEL_MAX_SCORE, /* defValue= */ GameConstants.SCORE_DEF_VALUE);
        if (maxScore < score && activity.getSavedSignedInAccount() != null) {
            String leaderboardId = activity.getResources().getString(R.string.leaderboard_best_score);
            Games.getLeaderboardsClient(activity, activity.getSavedSignedInAccount())
                    .submitScore(leaderboardId, score);
        }
    }

    private void winGame() {
        isPlaying = false;
        LevelManager.setCurrentLevelScore(score);
        NavigationHelper.navigateToFragment(activity, new GameSuccessFragment());
        updateObstacleLeaderboards();
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
        levelSpeed = level.getBaseSpeed();
        obstacleList = new LinkedList<>();
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
