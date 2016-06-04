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

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.andbasx.animation.SplashView;
import com.pepperonas.andbasx.base.AndroidStorageUtils;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.andbasx.concurrency.ThreadUtils;
import com.pepperonas.jbasx.io.IoUtils;
import com.pepperonas.truthordare.config.Const;
import com.pepperonas.truthordare.model.Gender;
import com.pepperonas.truthordare.model.Player;
import com.pepperonas.truthordare.database.DatabaseHelper;
import com.pepperonas.truthordare.dialogs.DialogGameIsRunning;
import com.pepperonas.truthordare.fragments.FragmentInstruction;
import com.pepperonas.truthordare.fragments.FragmentMultiplayer;
import com.pepperonas.truthordare.fragments.FragmentRestore;
import com.pepperonas.truthordare.fragments.FragmentSelectAction;
import com.pepperonas.truthordare.fragments.FragmentSettings;
import com.pepperonas.truthordare.fragments.FragmentTwoPlayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    /* Toolbar & NavigationView */
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;

    /* Fragment */
    private Fragment mFragment;
    private int mLastSelectedNavItemPos = 0;

    /* Leaving the app */
    private boolean mIsExitPressedOnce = false;

    /* Player */
    private List<Player> mPlayers = new ArrayList<Player>();
    private int mCurrentPlayer = 0;
    private DatabaseHelper mDb;
    private TextView navViewSubTitle;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    /**
     * Checks if the app has permission to write to device storage.
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions.
     *
     * @param activity The calling activity.
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }


    /**
     * App is launched and Activity get created the first time
     * NOTE: the Context which is needed from the libs is already passed to them.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SplashView mSplashView = (SplashView) findViewById(R.id.splash_view);

        ThreadUtils.runDelayed(2000, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mSplashView.finish();

                verifyStoragePermissions(MainActivity.this);

                mDb = new DatabaseHelper(MainActivity.this);

                navViewSubTitle = (TextView) findViewById(R.id.nav_view_header_subtitle);

                initToolbar();

                initNavView();

                initNavDrawer();

                csvDatabaseImport();

                return null;
            }
        });

    }


    private void csvDatabaseImport() {
        File sd = new File(AndroidStorageUtils.getExternalRootDir());
        File source = new File(sd.getPath() + File.separator + "Download" + File.separator + "Android.ToWimport.csv");
        Log.d(TAG, "csvDatabaseImport  " + source.getPath());

        String content = null;
        try {
            content = IoUtils.readFileIso8859_1(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "csvDatabaseImport  " + content);

        storeContentInDb(content);
    }


    public void storeContentInDb(String content) {
        String[] lines = content.split("\n");
        int d, t;
        d = t = 1;
        for (String line : lines) {
            if (line.contains("text;art;mann")) continue;
            String[] args = line.split(";");
            String text = args[0];
            boolean isTruth = args[1].equals("w");
            boolean isMale = args[2].equals("j");
            boolean isFemale = args[3].equals("j");
            boolean isGroup = args[4].equals("j");
            boolean isMultiple = args[5].equals("j");
            if (isTruth) {
                mDb.addTruth(t++, text, isFemale ? Gender.FEMALE : Gender.MALE, isGroup, isMultiple);
            } else {
                mDb.addDare(d++, text, isFemale ? Gender.FEMALE : Gender.MALE, isMultiple);
            }
        }
    }


    /**
     * Activity gets resumed (called after onCreate or when the Activity comes to the foreground after it was left see {@link #onPause})
     */
    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * Activity gets in to the background (when Home-Button was pressed, or we received a phone-call)
     */
    @Override
    protected void onPause() {
        super.onPause();
        mDb.close();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * Saving the configuration (e.g. when the device was rotated)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selection", mLastSelectedNavItemPos);
    }


    /**
     * Restoring the configuration (e.g. when the device was rotated)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastSelectedNavItemPos = savedInstanceState.getInt("selection");
        selectNavViewItem(mNavView.getMenu().getItem(0).getSubMenu().getItem(mLastSelectedNavItemPos));
    }


    @Override
    public void onBackPressed() {
        touchTwiceToExit();
    }


    private void initNavView() {
        mNavView = (NavigationView) findViewById(R.id.navigation_view);
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(menuItem.isChecked());
                mDrawerLayout.closeDrawers();

                return selectNavViewItem(menuItem);
            }
        });

        // selecting the first item
        selectNavViewItem(getNavigationView().getMenu().getItem(0).getSubMenu().getItem(0));
    }


    private void initNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.navDrawerLayout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // updating Drawer's state
        mDrawerToggle.syncState();
    }


    public boolean selectNavViewItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_item_instruction: {
                mLastSelectedNavItemPos = 0;
                if (!ensureGameIsNotRunning()) return false;
                if (mFragment instanceof FragmentInstruction) return true;

                makeFragmentTransaction(FragmentInstruction.newInstance(0));
                return true;
            }

            case R.id.nav_item_two_player: {
                mLastSelectedNavItemPos = 1;
                if (!ensureGameIsNotRunning()) return false;
                if (mFragment instanceof FragmentTwoPlayer) return true;

                makeFragmentTransaction(FragmentTwoPlayer.newInstance(1));
                return true;
            }

            case R.id.nav_item_multiplayer: {
                mLastSelectedNavItemPos = 2;
                if (!ensureGameIsNotRunning()) return false;
                if (mFragment instanceof FragmentMultiplayer) return true;

                makeFragmentTransaction(FragmentMultiplayer.newInstance(2));
                return true;
            }

            case R.id.nav_item_settings: {
                mLastSelectedNavItemPos = 3;
                if (!ensureGameIsNotRunning()) return false;
                if (mFragment instanceof FragmentSettings) return true;

                makeFragmentTransaction(FragmentSettings.newInstance(3));
                return true;
            }

            case R.id.nav_item_restore: {
                mLastSelectedNavItemPos = 4;
                if (!ensureGameIsNotRunning()) return false;
                if (mFragment instanceof FragmentRestore) return true;

                makeFragmentTransaction(FragmentRestore.newInstance(4));
                return true;
            }

        }

        return false;
    }


    private boolean ensureGameIsNotRunning() {
        if (FragmentSelectAction.getRoundCounter() != 0) {
            new DialogGameIsRunning(this, mLastSelectedNavItemPos);
            return false;
        }
        return true;
    }


    /**
     * Replacing the Fragment by a nice animation
     */
    public void makeFragmentTransaction(Fragment fragment) {
        mFragment = fragment;
        android.support.v4.app.FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (mFragment instanceof FragmentSettings) {
            fragmentTransaction.setCustomAnimations(R.anim.anim_fadein, R.anim.anim_fadeout);
        } else fragmentTransaction.setCustomAnimations(R.anim.anim_fadein, R.anim.anim_fadeout);

        fragmentTransaction.replace(R.id.main_frame, mFragment);
        fragmentTransaction.commit();
    }


    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }


    public void setTitle(String title) {
        if (mToolbar == null) initToolbar();
        mToolbar.setTitle(title);
    }


    /**
     * Showing the current app-state in the NavigationView's header
     */
    public void setNavigationViewSubtitle(String navViewSubtitle) {
        if (navViewSubTitle == null) {
            Log.e(TAG, "setNavigationViewSubtitle?");
            navViewSubTitle = (TextView) findViewById(R.id.nav_view_header_subtitle);
        }
        if (navViewSubTitle != null) {
            navViewSubTitle.setText(navViewSubtitle);
        } else Log.wtf(TAG, "setNavigationViewSubtitle WTF");
    }


    /**
     * Prevents the user from accidently closing the app
     */
    private void touchTwiceToExit() {
        if (!AesPrefs.getBoolean(R.string.AP_TOUCH_TWICE_TO_EXIT, true)) mIsExitPressedOnce = true;
        if (mIsExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        ToastUtils.toastShort(R.string.touch_twice_to_close);

        mIsExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsExitPressedOnce = false;
            }
        }, Const.DELAY_ON_BACK_PRESSED);
    }


    public void updateCurrentPlayer() {
        if (mCurrentPlayer >= mPlayers.size() - 1) {
            mCurrentPlayer = 0;
        } else mCurrentPlayer++;
    }


    public Player getCurrentPlayer() {
        if (mPlayers.get(mCurrentPlayer) == null) {
            return new Player();
        }
        return mPlayers.get(mCurrentPlayer);
    }


    public List<Player> getPlayers() {
        return mPlayers;
    }


    public Toolbar getToolbar() {
        return mToolbar;
    }


    public NavigationView getNavigationView() {
        return mNavView;
    }


    public DatabaseHelper getDatabase() {
        return mDb;
    }


    public int getCurrentPlayerCounter() {
        return mCurrentPlayer;
    }


    public void setCurrentPlayerCounter(int i) {
        mCurrentPlayer = 0;
    }
}
