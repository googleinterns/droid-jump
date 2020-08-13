package com.google.droidjump;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Intent;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.google.droidjump.models.LevelManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LevelsScreenTest {
    private static final Intent MAIN_ACTIVITY_INTENT =
            new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), MainActivity.class);
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        activityTestRule.launchActivity(MAIN_ACTIVITY_INTENT);
        onView(withId(R.id.level_button)).perform(click());
        int lastLevel = (int) (Math.random() * (LevelManager.getLevelsCount()
                - GameConstants.FIRST_LEVEL_ID + 1) + GameConstants.FIRST_LEVEL_ID);
        LevelManager.setLastLevel(lastLevel);
        int currentLevel = (int) (Math.random() *
                (lastLevel - GameConstants.FIRST_LEVEL_ID + 1) + GameConstants.FIRST_LEVEL_ID);
        LevelManager.setCurrentLevel(currentLevel);
    }

    @Test
    public void checkIfHeaderIsDisplayed() {
        onView(withId(R.id.choose_level_header)).check(matches(isDisplayed()));
    }

    @Test
    public void chooseLastLevel() {
        int lastLevel = LevelManager.getLastLevel();
        onView(withText(String.valueOf(lastLevel))).perform(ViewActions.click());
        assertEquals(LevelManager.getCurrentLevel(), lastLevel);
        assertEquals(LevelManager.getLastLevel(), lastLevel);
    }

    @Test
    public void chooseAvailableLevel() {
        int lastLevel = LevelManager.getLastLevel();
        // Available level is the lever from range [firstLevel, lastLevel].
        int availableLevel = (int) (Math.random() *
                (lastLevel - GameConstants.FIRST_LEVEL_ID + 1) + GameConstants.FIRST_LEVEL_ID);
        onView(withText(String.valueOf(availableLevel))).perform(click());
        assertEquals(LevelManager.getCurrentLevel(), availableLevel);
    }

    @Test
    public void chooseNotAvailableLevel() {
        // Set the last and the current level to 1.
        LevelManager.resetGameData();
        int biggestLevel = LevelManager.getLevelsCount();
        onView(withText(String.valueOf(biggestLevel))).perform(click());
        assertNotEquals(LevelManager.getCurrentLevel(), biggestLevel);
    }
}
