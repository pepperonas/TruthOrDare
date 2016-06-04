/*
 * Copyright (c) 2016 Martin Pfeffer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pepperonas.truthordare.model;

import com.pepperonas.andbasx.base.ToastUtils;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class Strike {

    private static final String TAG = "Strike";

    private Player player;
    private int dareCtr = 0;
    private int truthCtr = 0;


    public Strike(Player player) {
        this.player = player;
    }


    public void setPlayable(Action action) {
        if (action instanceof Dare) {
            truthCtr = 0;
            dareCtr++;
        } else if (action instanceof Truth) {
            dareCtr = 0;
            truthCtr++;
        }

        if (truthCtr >= 3) {
            truthCtr = 0;
            ToastUtils.toastShort(player.getName() + " TRUTH STRIKE!");
        }
        if (dareCtr >= 3) {
            dareCtr = 0;
            ToastUtils.toastShort(player.getName() + " DARE STRIKE!");
        }
    }

}
