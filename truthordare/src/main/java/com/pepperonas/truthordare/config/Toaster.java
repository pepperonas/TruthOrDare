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

package com.pepperonas.truthordare.config;

import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.truthordare.R;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class Toaster {

    public static void swipeToRemove() {
        int timesToastShown = AesPrefs.getInt("toast_swipe_to_remove", 0);
        AesPrefs.putInt("toast_swipe_to_remove", timesToastShown + 1);
        if (timesToastShown < 4) ToastUtils.toastLong(R.string.toast_swipe_to_remove);
    }
}
