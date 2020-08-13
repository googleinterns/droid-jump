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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.junit.Assert.assertEquals;

import android.content.Intent;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.google.droidjump.models.LevelManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StartScreenTest {
    private static final Intent MAIN_ACTIVITY_INTENT = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), MainActivity.class);

    @Before
    public void setUp() throws Exception {
        activityTestRule.launchActivity(MAIN_ACTIVITY_INTENT);
    }

    @Test
    public void navigateToGameScreen() {
        onView(ViewMatchers.withId(R.id.play_button)).perform(ViewActions.click());

    }

    @Test
    public void navigateToLevelsScreen() {
        onView(ViewMatchers.withId(R.id.level_button)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.choose_level_header)).check(matches(isDisplayed()));
    }

    @Test
    public void startNewGame() {
        // Set the handpicked current level
        int testingCurrentLevel = GameConstants.GAME_LEVELS_COUNT / 2;
        LevelManager.setCurrentLevel(testingCurrentLevel);
        // Click on new game button
        onView(ViewMatchers.withId(R.id.new_game_button)).perform(ViewActions.click());
        assertEquals(LevelManager.getCurrentLevel(), GameConstants.FIRST_LEVEL_ID);
        assertEquals(LevelManager.getLastLevel(), GameConstants.FIRST_LEVEL_ID);
    }

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);
}
