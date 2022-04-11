package com.garden_assistant.gardenassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLite extends SQLiteOpenHelper {

    public static final String VEGETABLES_TABLE = "Vegetables";
    public static final String KEY_ID = "_id";
    public static final String KEY_IMAGE_ID = "IMAGE_ID";
    public static final String KEY_VEGETABLE_NAME = "VEGETABLE_NAME";
    public static final String KEY_DAYS = "DAYS";
    public static final String KEY_START_DATE = "START_DATE";
    public static final int DATABASE_VERSION = 1;

    public MySQLite(Context context) {
        super(context, VEGETABLES_TABLE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + VEGETABLES_TABLE + "(" + KEY_ID
                + " integer primary key," + KEY_VEGETABLE_NAME + " text," + KEY_IMAGE_ID + " integer,"
                + KEY_START_DATE + " text," + KEY_DAYS + " integer" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + VEGETABLES_TABLE);
        onCreate(sqLiteDatabase);
    }
    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + VEGETABLES_TABLE);
        onCreate(sqLiteDatabase);
    }
}
