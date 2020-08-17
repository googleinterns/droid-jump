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

import android.content.res.Resources;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Reads JSON files and converts data to JSONObjects.
 */
public class JSONReader {
    public static JSONObject getJSONObjectFromResource(int fileId, Resources resources) {
        String jsonString = "";
        JSONObject jsonObject = null;
        try (InputStream is = resources.openRawResource(fileId)) {
            Scanner scanner = new Scanner(is);
            jsonString = scanner.useDelimiter("\\A").next();
            jsonObject = new JSONObject(jsonString);
        }
        catch (Resources.NotFoundException | IOException e) {
            Log.e(JSONReader.class.getName(), "Failed to open resource: " + e.getMessage());
        }
        catch (JSONException e) {
            Log.e(JSONReader.class.getName(), "Failed to parse JSON: " + e.getMessage());
        }
        return jsonObject;
    }
}
