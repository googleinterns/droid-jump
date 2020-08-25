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

package com.google.droidjump.leaderboards_data;

public class LeaderboardsPlayer {
    private String username;
    private int score;
    private int rank;
    private int avatar;

    public LeaderboardsPlayer(String username, int score, int rank, int avatar) {
        this.username = username;
        this.score = score;
        this.rank = rank;
        this.avatar = avatar;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public int getRank() {
        return rank;
    }
}
