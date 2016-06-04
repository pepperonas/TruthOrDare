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

package com.pepperonas.truthordare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.jbasx.base.BaseUtils;
import com.pepperonas.truthordare.R;
import com.pepperonas.truthordare.model.Dare;
import com.pepperonas.truthordare.model.Gender;
import com.pepperonas.truthordare.model.GenderType;
import com.pepperonas.truthordare.model.Player;
import com.pepperonas.truthordare.model.Truth;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DARES = "dares";
    private static final String TRUTHS = "truths";
    private static final String SEP = ", ";
    public static final int NON_GROUP = 0;
    public static final int DEFAULT_DIRTNESS = 0;
    public static final int DEFAULT_PLAYED_BY = -1;

    private static final int PLAYER = 0;
    private static final int DARE = 1;
    private static final int TRUTH = 2;

    private static String DATABASE_NAME = "truthordare.db";
    private SQLiteDatabase mDatabase;

    private String TBL_PLAYERS = "players";
    private String TBL_DARES = "dares";
    private String TBL_TRUTHS = "truths";

    private String P_ID = "p_id";
    private String P_TMP_ID = "p_tmp_id";
    private String D_ID = "d_id";
    private String T_ID = "t_id";

    private String P_NAME = "p_name";
    private String P_GENDER = "p_gender";
    private String P_ACTIVE = "p_active";

    private String DESC = "desc";
    private String DIRT = "dirt";
    private String PLAYED_BY = "played_by";
    private String IS_GROUP = "group";
    private String GENDER = "gender";
    private String MULTI = "multi";
    private String LAST_UPDATE = "last_update";

    private String D_DESC = "d_" + DESC;
    private String D_DIRT = "d_" + DIRT;
    private String D_PLAYED_BY = "d_" + PLAYED_BY;
    private String D_IS_GROUP = "d_" + IS_GROUP;
    private String D_GENDER = "d_" + GENDER;
    private String D_MULTI = "d_" + MULTI;
    private String D_LAST_UPDATE = "d_" + LAST_UPDATE;

    private String T_DESC = "t_" + DESC;
    private String T_DIRT = "t_" + DIRT;
    private String T_PLAYED_BY = "t_" + PLAYED_BY;
    private String T_IS_GROUP = "t_" + IS_GROUP;
    private String T_GENDER = "t_" + GENDER;
    private String T_MULTI = "t_" + MULTI;
    private String T_LAST_UPDATE = "t_" + LAST_UPDATE;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public synchronized void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Players
        db.execSQL("CREATE TABLE " + TBL_PLAYERS + " (" + P_ID + " integer primary key" + SEP + P_TMP_ID + " integer" + SEP +
                   P_NAME + " text" + SEP + P_GENDER + " integer" + SEP +
                   P_ACTIVE + " integer);");

        // Dares
        db.execSQL("CREATE TABLE " + TBL_DARES + " (" + D_ID + " integer primary key" + SEP + D_DESC + " text" + SEP +
                   D_DIRT + " integer" + SEP + D_PLAYED_BY + " integer" + SEP + D_IS_GROUP + " integer" + SEP +
                   D_GENDER + " text" + SEP + D_MULTI + " integer" + SEP +
                   D_LAST_UPDATE + " text);");

        // Truths
        db.execSQL("CREATE TABLE " + TBL_TRUTHS + " (" + T_ID + " integer primary key" + SEP + T_DESC + " text" + SEP +
                   T_DIRT + " integer" + SEP + T_PLAYED_BY + " integer" + SEP + T_IS_GROUP + " integer" + SEP +
                   T_GENDER + " text" + SEP + T_MULTI + " integer" + SEP +
                   T_LAST_UPDATE + " text);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_PLAYERS);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_DARES);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_TRUTHS);
        onCreate(db);
    }


    public int numberOfRows(int which) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (which) {
            case DARE: return (int) DatabaseUtils.queryNumEntries(db, TBL_DARES);
            case TRUTH: return (int) DatabaseUtils.queryNumEntries(db, TBL_TRUTHS);
            default: return (int) DatabaseUtils.queryNumEntries(db, TBL_PLAYERS);
        }
    }


    public Dare getDare(Player player, boolean firstRun) {
        int random = BaseUtils.randomInt(1, numberOfRows(DARE));

        String selectQuery;

        if (firstRun) {
            selectQuery = "SELECT * FROM " + DARES + " WHERE d_id = " + random + " AND d_played_by != " + player.getId();
        } else {
            selectQuery = "SELECT * FROM " + DARES + " WHERE d_id = " + random;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                GenderType genderType;
                if (c.getString(5).equals("MALE")) {
                    genderType = GenderType.MALE_ONLY;
                } else if (c.getString(5).equals("FEMALE")) {
                    genderType = GenderType.FEMALE_ONLY;
                } else {
                    genderType = GenderType.BOTH;
                }
                return new Dare(c.getInt(0), c.getString(1), genderType, c.getString(6).equals("true"), player.getId());
            } while (c.moveToNext());
        }
        c.close();

        ToastUtils.toastShort(R.string.card_is_replayed_by_player);

        getDare(player, false);

        return null;
    }


    public Truth getTruth(Player player, boolean firstRun) {
        int random = BaseUtils.randomInt(1, numberOfRows(TRUTH));

        String selectQuery;

        if (firstRun) {
            selectQuery = "SELECT * FROM " + TRUTHS + " WHERE t_id = " + random + " AND t_played_by != " + player.getId();
        } else {
            selectQuery = "SELECT * FROM " + TRUTHS + " WHERE t_id = " + random;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                GenderType genderType;
                if (c.getString(5).equals("MALE")) {
                    genderType = GenderType.MALE_ONLY;
                } else if (c.getString(5).equals("FEMALE")) {
                    genderType = GenderType.FEMALE_ONLY;
                } else {
                    genderType = GenderType.BOTH;
                }
                return new Truth(c.getInt(0), c.getString(1), genderType, c.getString(6).equals("true"), player.getId());
            } while (c.moveToNext());
        }
        c.close();

        ToastUtils.toastShort(R.string.card_is_replayed_by_player);

        getTruth(player, false);

        return null;
    }


    public void addPlayer(int tmpId, String name, Gender gender, boolean isActive) {
        String row = "INSERT OR REPLACE INTO " + "players " + " ("
                     + P_ID + SEP +
                     P_TMP_ID + SEP +
                     P_NAME + SEP +
                     P_GENDER + SEP +
                     P_ACTIVE +
                     ") VALUES (" +
                     "" + null + ", " +
                     "" + tmpId + ", " +
                     "'" + name + "', " +
                     "'" + gender.toString() + "', " +
                     "'" + isActive + "')";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(row);
    }


    public void addDare(int i, String text, Gender gender, boolean isMultipleTimesPlayable) {
        String row = "INSERT OR REPLACE INTO " + "dares " + " ("
                     + D_ID + SEP +
                     D_DESC + SEP +
                     D_DIRT + SEP +
                     D_PLAYED_BY + SEP +
                     D_IS_GROUP + SEP +
                     D_GENDER + SEP +
                     D_MULTI + SEP +
                     D_LAST_UPDATE +
                     ") VALUES (" +
                     "" + (i != -1 ? i : null) + ", " +
                     "'" + text + "', " +
                     "'" + DEFAULT_DIRTNESS + "', " +
                     "'" + DEFAULT_PLAYED_BY + "', " +
                     "'" + NON_GROUP + "', " +
                     "'" + gender + "', " +
                     "'" + isMultipleTimesPlayable + "', " +
                     "'" + System.currentTimeMillis() + "');";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(row);
    }


    public void addTruth(int i, String text, Gender gender, boolean isGroup, boolean isMultipleTimesPlayable) {
        String row = "INSERT OR REPLACE INTO " + "truths " + " ("
                     + T_ID + SEP +
                     T_DESC + SEP +
                     T_DIRT + SEP +
                     T_PLAYED_BY + SEP +
                     T_IS_GROUP + SEP +
                     T_GENDER + SEP +
                     T_MULTI + SEP +
                     T_LAST_UPDATE +
                     ") VALUES (" +
                     "" + (i != -1 ? i : null) + ", " +
                     "'" + text + "', " +
                     "'" + DEFAULT_DIRTNESS + "', " +
                     "'" + DEFAULT_PLAYED_BY + "', " +
                     "'" + isGroup + "', " +
                     "'" + gender.toString() + "', " +
                     "'" + isMultipleTimesPlayable + "', " +
                     "'" + System.currentTimeMillis() + "');";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(row);
    }


    public void updateDare(int id, int playedById) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(D_PLAYED_BY, playedById);
        contentValues.put(D_LAST_UPDATE, System.currentTimeMillis());
        db.update("dares ", contentValues, "d_id = " + id, null);
    }


    public void updateTruth(int id, int playedById) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_PLAYED_BY, playedById);
        contentValues.put(T_LAST_UPDATE, System.currentTimeMillis());
        db.update("truths ", contentValues, "t_id = " + id, null);
    }


    public List<Dare> getAllDares() {
        List<Dare> dares = new ArrayList<Dare>();
        String selectQuery = "SELECT * FROM dares ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                dares.add(new Dare(c.getInt(0), c.getString(1),
                                   c.getString(2).equals(Gender.FEMALE.name()) ? GenderType.FEMALE_ONLY :
                                   GenderType.MALE_ONLY,
                                   true, 0
                ));
            } while (c.moveToNext());
        }
        c.close();

        return dares;

    }


    public List<Truth> getAllTruths() {
        List<Truth> truths = new ArrayList<Truth>();
        String selectQuery = "SELECT * FROM truths ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                truths.add(new Truth(c.getInt(0), c.getString(1),
                                     c.getString(2).equals(Gender.FEMALE.name()) ? GenderType.FEMALE_ONLY
                                                                                 : GenderType.MALE_ONLY,
                                     true, 0));
            } while (c.moveToNext());
        }
        c.close();

        return truths;

    }


    public void deletePlayers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TBL_PLAYERS);
        ToastUtils.toastShort("Player table wiped");
    }
}


/**
 * Copy existing database from assets into app.
 */
//    /**
//     * Creates a empty database on the system and rewrites it with your own database.
//     */
//    public void createDatabase() throws IOException {
//        boolean dbExist = checkDatabase();
//
//        if (dbExist) {
//            // do nothing - database already exist
//        } else {
//            // By calling this method and empty database will be created into the default system path
//            // of your application so we are gonna be able to overwrite that database with our database.
//            this.getReadableDatabase();
//            try {
//                copyDatabase();
//            } catch (IOException e) {
//                throw new Error("Error copying database");
//            }
//        }
//    }


//    /**
//     * Check if the database already exist to avoid re-copying the file each time you open the application.
//     *
//     * @return true if it exists, false if it doesn't
//     */
//    private boolean checkDatabase() {
//        SQLiteDatabase existingDb = null;
//
//        try {
//            existingDb = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
//
//        } catch (SQLiteException e) {
//            Log.e(TAG, "Database does not exist!");
//        }
//
//        if (existingDb != null) {
//            existingDb.close();
//        }
//
//        return existingDb != null;
//    }


//    /**
//     * Copies your database from your local assets-folder to the just created empty database in the
//     * system folder, from where it can be accessed and handled.
//     * This is done by transfering bytestream.
//     */
//    private void copyDatabase() throws IOException {
//        InputStream myInput = mCtx.getAssets().open(DATABASE_NAME);
//
//        // Open the empty db as the output stream
//        OutputStream os = new FileOutputStream(DB_PATH);
//
//        // transfer bytes from the inputfile to the outputfile
//        byte[] buffer = new byte[1024];
//        int length;
//        while ((length = myInput.read(buffer)) > 0) {
//            os.write(buffer, 0, length);
//        }
//
//        // closing the streams
//        os.flush();
//        os.close();
//        myInput.close();
//    }


//    public void openDatabase() throws SQLException {
//        mDatabase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
//    }