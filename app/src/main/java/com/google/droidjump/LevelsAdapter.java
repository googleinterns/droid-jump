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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Represents LevelsAdapter.
 */
public class LevelsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private int[] levels;
    private int lastLevel;

    public LevelsAdapter(Context context, int[] levels, int lastLevel) {
        this.context = context;
        this.levels = levels;
        this.lastLevel = lastLevel;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return levels.length;
    }

    @Override
    public Object getItem(int index) {
        // Receives index, returns level.
        return levels[index];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.row_level_item, /* root= */ null);
        TextView textView = view.findViewById(R.id.row_level_item_text);
        textView.setText(String.valueOf(levels[index]));
        if (lastLevel > levels[index]) {
            textView.setTextColor(Color.BLACK);
        } else if (lastLevel == levels[index]) {
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.BLACK);
        }
        return view;
    }
}
