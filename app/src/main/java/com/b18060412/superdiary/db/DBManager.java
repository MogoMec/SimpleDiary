package com.b18060412.superdiary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    protected static final int DB_VERSION = 1;
    protected static final String DB_NAME = "diary_db";

    protected static final String DB_PRIMARY_KEY = "_id";
    protected static final String DB_TABLE_NAME = "diary";

    protected static final String DB_TABLE_COLUMN_MOOD = "mood";
    protected static final String DB_TABLE_COLUMN_CONTENT = "content";
    protected static final String DB_TABLE_COLUMN_DATE = "date";
    protected static final String DB_TABLE_COLUMN_WEATHER = "weather";
    protected static final String DB_TABLE_COLUMN_LOCATION = "location";

    protected static final String DB_DEFAULT_ORDERBY = DB_TABLE_COLUMN_DATE + " DESC";//日记默认按日期降序
    private static final String TAG = "DBManager";

    private SQLiteDatabase db;
    private  DBHelper mDBHelper;
    protected static final DBManager mInstance = new DBManager();

    private DBManager(){}//单例

    public static DBManager getInstance() {//单例
        return mInstance;
    }

    public void open(Context context){
        mDBHelper = new DBHelper(context,DB_NAME,null,DB_VERSION);
        db = mDBHelper.getWritableDatabase();
    }


    public long insert(DiaryItem diaryItem){
        ContentValues values = new ContentValues();
        values.put(DB_TABLE_COLUMN_MOOD,diaryItem.getMood());
        values.put(DB_TABLE_COLUMN_CONTENT,diaryItem.getContent());
        values.put(DB_TABLE_COLUMN_DATE,diaryItem.getDate());
        values.put(DB_TABLE_COLUMN_WEATHER,diaryItem.getWeather());
        values.put(DB_TABLE_COLUMN_LOCATION,diaryItem.getLocation());
        return db.insert(DB_TABLE_NAME,null,values);
    }

    public Cursor selectAllDiary(){
        Cursor cursor = db.query(DB_TABLE_NAME,null,null,
                null,null,null,DB_DEFAULT_ORDERBY,null);
        if (cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void closeDB(){
        mDBHelper.close();
    }

    public int size(){
        int size = 0;
        Cursor cursor = db.query(DB_TABLE_NAME,new String[]{DB_PRIMARY_KEY},null,
                null,null,null,null,null);
        if(cursor!=null){
            size = cursor.getCount();
        }
        cursor.close();
        return size;
    }

    public int deleteById(String selectedDiaryId) { //依据ID删除记录
        return db.delete(DB_TABLE_NAME,DB_PRIMARY_KEY+"=?",new String[]{selectedDiaryId});
    }

    public void update(DiaryItem diaryItem){//更新数据
        ContentValues values = new ContentValues();
        values.put(DB_TABLE_COLUMN_MOOD,diaryItem.getMood());
        values.put(DB_TABLE_COLUMN_CONTENT,diaryItem.getContent());
        values.put(DB_TABLE_COLUMN_DATE,diaryItem.getDate());
        values.put(DB_TABLE_COLUMN_WEATHER,diaryItem.getWeather());
        values.put(DB_TABLE_COLUMN_LOCATION,diaryItem.getLocation());
        String condition = DB_PRIMARY_KEY + "=" +  "\'" + diaryItem.getId() + "\'";
        db.update(DB_TABLE_NAME,values,condition,null);
    }

    public DiaryItem getById(String diaryId) {  //依据ID查询记录
        Cursor cursor = db.query(DB_TABLE_NAME, null, DB_PRIMARY_KEY + "=" + "\'" + diaryId + "\'", null, null, null,
                DB_DEFAULT_ORDERBY, null);
        DiaryItem diaryItem = new DiaryItem();
        if (cursor.moveToFirst()) {
                diaryItem = new DiaryItem(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2), cursor.getLong(3),
                    cursor.getString(4), cursor.getString(5));
        }
        return diaryItem;
    }
    public DiaryItem getByDate(long date) {  //依据时间戳查询记录
        Cursor cursor = db.query(DB_TABLE_NAME, null, DB_TABLE_COLUMN_DATE + "=" + "\'" + date +"\'" , null, null, null,
                null, null);
        DiaryItem diaryItem = new DiaryItem();
        if (cursor.moveToFirst()) {
            diaryItem = new DiaryItem(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2), cursor.getLong(3),
                    cursor.getString(4), cursor.getString(5));
        }
        return diaryItem;
    }
}
