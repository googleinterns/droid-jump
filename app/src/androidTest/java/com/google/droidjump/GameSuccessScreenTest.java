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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.droidjump.GameConstants.FIRST_LEVEL_ID;
import static org.hamcrest.Matchers.not;

import android.content.Intent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.google.droidjump.models.LevelManager;
import com.google.droidjump.models.NavigationHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GameSuccessScreenTest {
    private static final Intent MAIN_ACTIVITY_INTENT =
            new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), MainActivity.class);
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        activityTestRule.launchActivity(MAIN_ACTIVITY_INTENT);
        LevelManager.init(activityTestRule.getActivity());
    }

    @Test
    public void checkIfSuccessTextIsDisplayed() {
        NavigationHelper.navigateToFragment(activityTestRule.getActivity(), new GameSuccessFragment());

        onView(withId(R.id.success_text)).check(matches(isDisplayed()));
    }

    @Test
    public void checkOnClickMenuButton() {
        NavigationHelper.navigateToFragment(activityTestRule.getActivity(), new GameSuccessFragment());

        onView(withId(R.id.menu_button)).perform(click());

        onView(withId(R.id.play_button)).check(matches(isDisplayed()));
    }

    @Test
    public void playNextLevel() {
        LevelManager.setCurrentLevelIndex(FIRST_LEVEL_ID);
        LevelManager.setLastLevelIndex(FIRST_LEVEL_ID);
        NavigationHelper.navigateToFragment(activityTestRule.getActivity(), new GameSuccessFragment());

        onView(withId(R.id.next_button)).perform(click());

        onView(withId(R.id.game_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void checkNextButtonIsDisplayedIfNextLevelIsAvailableInCaseWeHaveOnlyOneLevel() {
        LevelManager.setCurrentLevelIndex(FIRST_LEVEL_ID);
        LevelManager.setLastLevelIndex(FIRST_LEVEL_ID);
        LevelManager.setLevelsLastIndex(FIRST_LEVEL_ID);
        NavigationHelper.navigateToFragment(activityTestRule.getActivity(), new GameSuccessFragment());

        onView(withId(R.id.next_button)).check(matches(not(isDisplayed())));
    }

    @Test
    public void checkNextButtonIsDisplayedIfNextLevelIsAvailableInCaseWeHaveMoreThanOneLevel() {
        LevelManager.setCurrentLevelIndex(FIRST_LEVEL_ID);
        int lastLevelIndex = 2; // A handpicked value.
        LevelManager.setLevelsLastIndex(lastLevelIndex);
        NavigationHelper.navigateToFragment(activityTestRule.getActivity(), new GameSuccessFragment());

        onView(withId(R.id.next_button)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfNextButtonIsHiddenWhenCurrentLevelIsTheLast() {
        int lastLevelIndex = LevelManager.getLevelsLastIndex();
        LevelManager.setCurrentLevelIndex(lastLevelIndex);
        LevelManager.setLastLevelIndex(lastLevelIndex);
        NavigationHelper.navigateToFragment(activityTestRule.getActivity(), new GameSuccessFragment());

        onView(withId(R.id.next_button)).check(matches(not(isDisplayed())));
    }
}
