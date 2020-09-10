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

import static com.google.droidjump.GameConstants.AMATEUR_INFINITE_LEVEL_PLAYER_OBSTACLE_COUNT;
import static com.google.droidjump.GameConstants.CACTUS_COMBO_COUNT;
import static com.google.droidjump.GameConstants.GAME_LEVEL_HEADER;
import static com.google.droidjump.GameConstants.GROUND_PROPORTION;
import static com.google.droidjump.GameConstants.HARDCORE_COMBO;
import static com.google.droidjump.GameConstants.MASTER_INFINITE_LEVEL_PLAYER_OBSTACLE_COUNT;
import static com.google.droidjump.GameConstants.NOVICE_INFINITE_LEVEL_PLAYER_OBSTACLE_COUNT;
import static com.google.droidjump.GameConstants.PALM_COMBO_COUNT;
import static com.google.droidjump.GameConstants.PRO_INFINITE_LEVEL_PLAYER_OBSTACLE_COUNT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.droidjump.leveldata.LevelConfig;
import com.google.droidjump.leveldata.InfiniteLevelData;
import com.google.droidjump.leveldata.LevelStrategy;
import com.google.droidjump.leveldata.LevelType;
import com.google.droidjump.leveldata.ObstacleType;
import com.google.droidjump.models.Bat;
import com.google.droidjump.models.Cactus;
import com.google.droidjump.models.Droid;
import com.google.droidjump.models.GameItem;
import com.google.droidjump.models.LevelManager;
import com.google.droidjump.models.NavigationHelper;
import com.google.droidjump.models.Obstacle;
import com.google.droidjump.models.Palm;
import com.google.droidjump.models.ScoreManager;
import com.google.droidjump.models.TwoStepAnimative;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Shows main game process.
 */
public class GameView extends SurfaceView implements Runnable {
    private final Bitmap platform = GameItem.drawableToBitmap(getResources().getDrawable(R.drawable.platform));
    private final int obstacleAdditionalMargin = 10;
    private MainActivity activity;
    private AchievementsManager achievementsManager;
    private SurfaceHolder surfaceHolder;
    private Droid droid;
    private Paint levelPaint;
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
    private int platformX;
    private int groundHeight;
    private int score;
    private int cactusScore;
    private int palmScore;
    private int batScore;
    private int batY;
    private int obstaclesCount;
    private int cactusCount;
    private int palmCount;
    private int hardcoreComboIndex;

    public GameView(Context context, int screenX, int screenY, boolean isPlaying) {
        super(context);
        intervalTimePoint = GameConstants.INTERVAL_START_TIME;
        timePoint = GameConstants.INTERVAL_START_TIME;
        activity = (MainActivity) context;
        achievementsManager = new AchievementsManager(activity);
        surfaceHolder = getHolder();
        level = LevelManager.getCurrentLevelStrategy();
        this.screenX = screenX;
        this.screenY = screenY;
        this.isPlaying = isPlaying;
        batY = screenY - 400;
        levelPaint = createLevelPaint();
        screenMargin = (int) getResources().getDimension(R.dimen.fab_margin);
        score = 0;
        obstaclesCount = 0;
        cactusCount = 0;
        palmCount = 0;
        hardcoreComboIndex = 0;
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
            checkForAchievements();
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
            int obstacleY = screenY - groundHeight + obstacleAdditionalMargin;
            // Adding new obstacle to game.
            switch (newObstacleType) {
                case CACTUS:
                    obstacleList.add(new Cactus(screenX, obstacleY, getResources()));
                    break;
                case PALM:
                    obstacleList.add(new Palm(screenX, obstacleY, getResources()));
                    break;
                case BAT:
                    obstacleList.add(new Bat(screenX, batY, getResources()));
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
                if (level instanceof InfiniteLevelData) {
                    updateCounters(obstacle);
                }
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
        Resources resources = getResources();
        for (int leaderboard : GameConstants.LEADERBOARD_LIST) {
            String leaderboardId = resources.getString(leaderboard);
            long score = ScoreManager.getScore(leaderboardId);
            switch (leaderboard) {
                case R.string.leaderboard_cactus_jumper:
                    score += cactusScore;
                    break;
                case R.string.leaderboard_bat_avoider:
                    score += batScore;
                    break;
                case R.string.leaderboard_palm_climber:
                    score += palmScore;
                    break;
                case R.string.leaderboard_best_score:
                case R.string.leaderboard_best_time:
                    return;
                default:
                    Log.e(getClass().getName(), "Found an unknown obstacle.");
                    return;
            }
            ScoreManager.submitScore(leaderboardId, score);
        }
    }

    private void updateCounters(Obstacle obstacle) {
        obstaclesCount++;
        if (obstacle instanceof Cactus) {
            cactusCount++;
            palmCount = 0;
        } else if (obstacle instanceof Palm) {
            palmCount++;
            cactusCount = 0;
        } else {
            cactusCount = 0;
            palmCount = 0;
        }

        if (obstacle.getClass() == HARDCORE_COMBO[hardcoreComboIndex]) {
            hardcoreComboIndex++;
        } else {
            hardcoreComboIndex = 0;
            if (obstacle.getClass() == HARDCORE_COMBO[hardcoreComboIndex]) {
                hardcoreComboIndex++;
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
            animateGameItem(droid);
        }
        if (droid.getY() != droid.getInitialY()) {
            // Decreasing droid Y position to made they jump smoothly.
            int newDroidY = droid.getY() + levelSpeed;
            droid.setY(Math.min(newDroidY, droid.getInitialY()));
        }
    }

    public void updatePlatformCoordinates() {
        // The leftmost coordinate where the new platform starts.
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
        String leaderboardId = getResources().getString(R.string.leaderboard_best_score);
        long maxLevelScore = LevelManager.getLevelMaxScore(LevelManager.getCurrentLevelIndex());
        long maxBestScore = Math.max(ScoreManager.getScore(leaderboardId), maxLevelScore);
        // Update a local and leaderboard best score.
        ScoreManager.submitScore(leaderboardId, maxBestScore);
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

    private void checkForAchievements() {
        switch (obstaclesCount) {
            case NOVICE_INFINITE_LEVEL_PLAYER_OBSTACLE_COUNT:
                achievementsManager.unlockAchievement(R.string.achievement_novice_infinite_level_player);
                break;
            case AMATEUR_INFINITE_LEVEL_PLAYER_OBSTACLE_COUNT:
                achievementsManager.unlockAchievement(R.string.achievement_amateur_infinite_level_player);
                break;
            case PRO_INFINITE_LEVEL_PLAYER_OBSTACLE_COUNT:
                achievementsManager.unlockAchievement(R.string.achievement_pro_infinite_level_player);
            case MASTER_INFINITE_LEVEL_PLAYER_OBSTACLE_COUNT:
                achievementsManager.unlockAchievement(R.string.achievement_master_infinite_level_player);
        }
        if (cactusCount == CACTUS_COMBO_COUNT) {
            achievementsManager.unlockAchievement(R.string.achievement_cactus_combo);
        }
        if (palmCount == PALM_COMBO_COUNT) {
            achievementsManager.unlockAchievement(R.string.achievement_palm_combo);
        }
        if (hardcoreComboIndex == HARDCORE_COMBO.length) {
            achievementsManager.unlockAchievement(R.string.achievement_hardcore_combo);
        }
    }
}
