package com.google.droidjump;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.droidjump.GameConstants.FIRST_LEVEL_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import com.google.droidjump.models.LevelManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LevelsScreenTest {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        // Running the levels screen.
        activityTestRule.getActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.activity_wrapper, new LevelsFragment());
        onView(withId(R.id.level_button)).perform(click());

        // Predetermined value.
        int lastLevelIndex = LevelManager.getLevelsLastIndex();
        LevelManager.setLastLevelIndex(lastLevelIndex);
        int currentLevel = Math.max(lastLevelIndex - 1, FIRST_LEVEL_ID);
        LevelManager.setCurrentLevelIndex(currentLevel);
    }

    @Test
    public void checkIfHeaderIsDisplayed() {
        onView(withId(R.id.choose_level_header)).check(matches(isDisplayed()));
    }

    @Test
    public void chooseLastLevel() {
        int lastLevelIndex = LevelManager.getLastLevelIndex();
        String lastLevelName = LevelManager.getGameLevels().get(lastLevelIndex).getLevelName();
        onView(withText(lastLevelName)).perform(ViewActions.click());

        assertEquals(LevelManager.getCurrentLevelIndex(), lastLevelIndex);
        assertEquals(LevelManager.getLastLevelIndex(), lastLevelIndex);
    }

    @Test
    public void chooseAvailableLevel() {
        // Available level is the lever from range [firstLevel, lastLevel].
        int availableLevelIndex = LevelManager.getCurrentLevelIndex();
        String availableLevelName = LevelManager.getGameLevels().get(availableLevelIndex).getLevelName();
        onView(withText(String.valueOf(availableLevelName))).perform(click());

        assertEquals(LevelManager.getCurrentLevelIndex(), availableLevelIndex);
    }

    @Test
    public void chooseNotAvailableLevel() {
        int levelsCount = LevelManager.getLevelsLastIndex() + 1;
        if (levelsCount > 1) {
            LevelManager.setLastLevelIndex(FIRST_LEVEL_ID);
            int lastLevelIndex = LevelManager.getLastLevelIndex();
            int notAvailableLevelIndex = lastLevelIndex + 1;
            String notAvailableLevelName = LevelManager.getGameLevels().get(notAvailableLevelIndex).getLevelName();
            onView(withText(notAvailableLevelName)).perform(click());

            assertNotEquals(LevelManager.getCurrentLevelIndex(), notAvailableLevelIndex);
        }
    }
}
