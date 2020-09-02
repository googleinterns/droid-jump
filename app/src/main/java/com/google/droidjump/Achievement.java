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

public enum Achievement {
    NOVICE_INFINITE_LEVEL_PLAYER(R.string.achievement_novice_infinite_level_player),
    AMATEUR_INFINITE_LEVEL_PLAYER(R.string.achievement_amateur_infinite_level_player),
    PRO_INFINITE_LEVEL_PLAYER(R.string.achievement_pro_infinite_level_player),
    MASTER_INFINITE_LEVEL_PLAYER(R.string.achievement_master_infinite_level_player),
    GREAT_START(R.string.achievement_great_start),
    NEW_STAR(R.string.achievement_new_star),
    PROF_PLAYER(R.string.achievement_profi_player),
    CACTUS_COMBO(R.string.achievement_cactus_combo),
    PALM_COMBO(R.string.achievement_palm_combo),
    HARDCORE_COMBO(R.string.achievement_hardcore_combo);

    public final Integer id;

    Achievement(Integer id) {
        this.id = id;
    }
}
