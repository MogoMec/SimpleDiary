package com.b18060412.superdiary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {


    protected static final String DB_PRIMARY_KEY = "_id";
    protected static final String DB_TABLE_NAME = "diary";

    protected static final String DB_TABLE_COLUMN_MOOD = "mood";
    protected static final String DB_TABLE_COLUMN_CONTENT = "content";
    protected static final String DB_TABLE_COLUMN_DATE = "date";
    protected static final String DB_TABLE_COLUMN_WEATHER = "weather";
    protected static final String DB_TABLE_COLUMN_LOCATION = "location";
    public static final String SQL_CREATE_TABLE_DIARY =
            "CREATE TABLE " + DB_TABLE_NAME + "("+ DB_PRIMARY_KEY +" integer primary key autoincrement,"//主键，自增长
            +DB_TABLE_COLUMN_MOOD+" text,"
            +DB_TABLE_COLUMN_CONTENT+" text,"
            +DB_TABLE_COLUMN_DATE +" integer,"
            +DB_TABLE_COLUMN_WEATHER+" text,"
            +DB_TABLE_COLUMN_LOCATION+" text);";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_DIARY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        onCreate(db);
    }
}
