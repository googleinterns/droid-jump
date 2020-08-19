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

package com.google.droidjump.leveldata;

import static com.google.droidjump.leveldata.JSONKeys.BASE_SPEED_KEY;
import static com.google.droidjump.leveldata.JSONKeys.INTERVAL_KEY;
import static com.google.droidjump.leveldata.JSONKeys.TIMELINE_KEY;
import static com.google.droidjump.leveldata.JSONKeys.TYPE_KEY;
import static com.google.droidjump.leveldata.ObstacleType.BAT;
import static com.google.droidjump.leveldata.ObstacleType.CACTUS;
import static com.google.droidjump.leveldata.ObstacleType.PALM;
import static org.junit.Assert.*;
import android.content.res.Resources;
import androidx.test.rule.ActivityTestRule;
import com.google.droidjump.MainActivity;
import com.google.droidjump.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class JSONReaderTest {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void getJSONObjectFromResource() throws JSONException {
        Resources resources = activityTestRule.getActivity().getResources();
        int testfileId = R.raw.test;

        // Create test JSON object R.raw.test content.
        JSONObject testJSONObject = new JSONObject();
        testJSONObject.put(BASE_SPEED_KEY, 50);
        JSONArray timelineArray = new JSONArray();
        timelineArray.put(new JSONObject().put(INTERVAL_KEY, 50).put(TYPE_KEY, CACTUS.toString()));
        timelineArray.put(new JSONObject().put(INTERVAL_KEY, 60).put(TYPE_KEY, PALM.toString()));
        timelineArray.put(new JSONObject().put(INTERVAL_KEY, 40).put(TYPE_KEY, BAT.toString()));
        testJSONObject.put(TIMELINE_KEY, timelineArray);

        // Get JSON object using tested method.
        JSONObject resultJSONObject = JSONReader.getJSONObjectFromResource(testfileId, resources);

        Assert.assertArrayEquals(new JSONObject[]{testJSONObject}, new JSONObject[]{resultJSONObject});
    }

}
