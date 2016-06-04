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

package com.pepperonas.truthordare.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.andbasx.concurrency.LoaderTaskUtils;
import com.pepperonas.andbasx.interfaces.LoaderTaskListener;
import com.pepperonas.jbasx.base.XmlUtils;
import com.pepperonas.jbasx.io.IoUtils;
import com.pepperonas.truthordare.BuildConfig;
import com.pepperonas.truthordare.MainActivity;
import com.pepperonas.truthordare.R;
import com.pepperonas.truthordare.model.Dare;
import com.pepperonas.truthordare.model.Truth;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class FragmentSettings
        extends com.github.machinarius.preferencefragment.PreferenceFragment
        implements Preference.OnPreferenceClickListener {

    private static final String TAG = "FragmentSettings";

    private MainActivity mMain;


    public static FragmentSettings newInstance(int i) {
        FragmentSettings fragment = new FragmentSettings();

        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);

        addCbxPref();

        addBuildPref();

        addDatabaseExchangePrefs();
    }


    private void addDatabaseExchangePrefs() {
        Preference sdExport = findPreference(getString(R.string.AP_EXPORT));
        Preference webImport = findPreference(getString(R.string.AP_IMPORT_FROM_WEB));
        Preference wipePlayer = findPreference(getString(R.string.AP_DB_WIPE_PLAYER));
        sdExport.setOnPreferenceClickListener(this);
        webImport.setOnPreferenceClickListener(this);
        wipePlayer.setOnPreferenceClickListener(this);
    }


    private void addBuildPref() {
        Preference buildVersion = findPreference(getString(R.string.AP_BUILD_VERSION));
        buildVersion.setTitle(R.string.build_version);
        buildVersion.setSummary(BuildConfig.VERSION_NAME);
    }


    private void addCbxPref() {
        CheckBoxPreference cbkTouchTwiceToExit = (CheckBoxPreference) findPreference(getString(R.string.AP_TOUCH_TWICE_TO_EXIT));
        cbkTouchTwiceToExit.setOnPreferenceClickListener(this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMain = (MainActivity) getActivity();
        mMain.setTitle(getString(R.string.settings));

        updateSummaries();

        AesPrefs.registerOnSharedPreferenceChangeListener(
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                    }
                });
    }


    private void updateSummaries() {

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated()");
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.AP_TOUCH_TWICE_TO_EXIT))) {
            CheckBoxPreference cbkTouchTwiceToExit = (CheckBoxPreference) findPreference(getString(R.string.AP_TOUCH_TWICE_TO_EXIT));
            AesPrefs.putBoolean(R.string.AP_TOUCH_TWICE_TO_EXIT, cbkTouchTwiceToExit.isChecked());
        } else if (preference.getKey().equals(getString(R.string.AP_BUILD_VERSION))) {
            //
        } else if (preference.getKey().equals(getString(R.string.AP_EXPORT))) {
            exportToSd();
        } else if (preference.getKey().equals(getString(R.string.AP_IMPORT_FROM_WEB))) {
            importFromWeb();
        } else if (preference.getKey().equals(getString(R.string.AP_DB_WIPE_PLAYER))) {
            mMain.getDatabase().deletePlayers();
        }
        return true;
    }


    private void exportToSd() {
        File sdCard = Environment.getExternalStorageDirectory();
        File targetDir = new File(sdCard.getPath() + File.separator + "TruthOrDare");
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        File targetFile = new File(targetDir + File.separator + "db-export-vers." + BuildConfig.VERSION_NAME + ".xml");
        if (!targetFile.exists()) {
            try {
                boolean success = targetFile.createNewFile();
                Log.i(TAG, "exportToSd " + (success ? "directory successfully created" : "directory found..."));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "exportToSd: Error while creating file.");
            }
        }

        List<Dare> dares = mMain.getDatabase().getAllDares();
        List<Truth> truths = mMain.getDatabase().getAllTruths();

        StringBuilder builder = new StringBuilder();

        for (Dare dare : dares) {
            builder.append(XmlUtils.wrap("object_tyoe", String.valueOf(dare.getId())));
            builder.append(XmlUtils.wrap("text", dare.getText()));
            builder.append(XmlUtils.wrap("gender", dare.getGenderType().name()));
            builder.append(XmlUtils.wrap("playedById", String.valueOf(dare.getPlayedById())));
        }

        for (Truth truth : truths) {
            builder.append(XmlUtils.wrap("object_type", String.valueOf(truth.getId())));
            builder.append(XmlUtils.wrap("id", String.valueOf(truth.getId())));
            builder.append(XmlUtils.wrap("text", truth.getText()));
            builder.append(XmlUtils.wrap("gender", truth.getGenderType().name()));
            builder.append(XmlUtils.wrap("playedById", String.valueOf(truth.getPlayedById())));
        }

        IoUtils.write(targetFile, builder.toString());
    }


    private void importFromWeb() {
        Log.d(TAG, "importFromWeb  " + "");
        new LoaderTaskUtils.Builder(getActivity(), new LoaderTaskListener() {
            @Override
            public void onLoaderTaskSuccess(LoaderTaskUtils.Action action, String s) {
                // inserting the content straight into the database
                Log.d(TAG, "onLoaderTaskSuccess  " + action.name());

                mMain.storeContentInDb(s);
            }


            @Override
            public void onLoaderTaskFailed(LoaderTaskUtils.Action action, String s) {

            }
            // launching the task, to get the content of the Dropbox file.
        }, "https://dl.dropboxusercontent.com/u/28089422/truth-or-dare.csv").launch();
    }

}
