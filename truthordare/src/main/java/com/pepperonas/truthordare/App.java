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

package com.pepperonas.truthordare;

import android.app.Application;

import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.jbasx.Jbasx;
import com.pepperonas.jbasx.log.Log;

/**
 * @author Martin Pfeffer (pepperonas)
 *         <p/>
 *         This Application class will initialize the libraries
 */
public class App extends Application {

    private static final String TAG = "App";


    @Override
    public void onCreate() {
        super.onCreate();

        AesPrefs.init(this, "pref_config", "l0ng&Passw0rd", AesPrefs.LogMode.ALL);
        AndBasx.init(this);

        Log.i(TAG, "onCreate " + Jbasx.Version.getVersionInfo());
        Log.i(TAG, "onCreate " + AndBasx.Version.getVersionInfo());
    }

}
