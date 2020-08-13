package com.google.droidjump;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LevelsScreenTest {
    private static final Intent MAIN_ACTIVITY_INTENT =
            new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), MainActivity.class);
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        LevelsFragment fragment = new LevelsFragment();
        activityTestRule.getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_wrapper, fragment).commit();
    }

    @Test
    public void checkIfHeaderIsDisplayed() {
        onView(withId(R.id.choose_level_header)).check(matches(isDisplayed()));
    }

    @Test
    public void chooseLastLevel() {}

    @Test
    public void chooseAvailableLevel() {}

    @Test
    public void chooseNotAvailableLevel() {}
}
