package com.example.myfakedouyinapplication.datebases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USER = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_AVATAR_RES_ID = "avatar_res_id";
    public static final String COLUMN_AVATAR_URL = "avatar_url";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_IS_FOLLOWED = "is_followed";
    public static final String COLUMN_IS_SPECIAL = "is_special";
    public static final String COLUMN_FOLLOW_TIME = "follow_time";
    public static final String COLUMN_CREATE_TIME = "create_time";
    public static final String COLUMN_UPDATE_TIME = "update_time";
    public static final String COLUMN_SYNC_STATUS = "sync_status";

    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_AVATAR_RES_ID + " INTEGER DEFAULT 0, " +
                    COLUMN_AVATAR_URL + " TEXT, " +
                    COLUMN_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_USER_ID + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_NOTE + " TEXT, " +
                    COLUMN_IS_FOLLOWED + " INTEGER DEFAULT 1, " +
                    COLUMN_IS_SPECIAL + " INTEGER DEFAULT 0, " +
                    COLUMN_FOLLOW_TIME + " INTEGER, " +
                    COLUMN_CREATE_TIME + " INTEGER, " +
                    COLUMN_UPDATE_TIME + " INTEGER, " +
                    COLUMN_SYNC_STATUS + " INTEGER DEFAULT 0" +
                    ");";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
