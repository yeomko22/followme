package com.example.junny.followme_realbeta;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by junny on 2017. 5. 25..
 */

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void createTable(){
        SQLiteDatabase db=getWritableDatabase();
        String sql="create table history(num integer primary key autoincrement, title text, newAddress text, latitude text, longitude text);";
        db.execSQL(sql);
    }
    public void insert(String title, String newAddress, String latitude, String longitude){
        SQLiteDatabase db=getWritableDatabase();
        String write_sql="insert into history(title,newAddress,latitude,longitude) values(\""+title+"\",\""+newAddress+"\",\""+latitude+"\",\""+longitude+"\");";
        db.execSQL(write_sql);
        Log.e("인서트 실행 확인","11");
        db.close();
    }
    public void delete(String title){
        SQLiteDatabase db=getWritableDatabase();
        String delete_sql="delete from history where title="+title+";";
        db.execSQL(delete_sql);
        db.close();
    }
    public void delete_history(){
        SQLiteDatabase db=getWritableDatabase();
        String delete_sql="delete from history";
        db.execSQL(delete_sql);
        db.close();
    }

    public Cursor select(){
        SQLiteDatabase db=getReadableDatabase();
        String result="";
        Cursor cursor=db.rawQuery("select * from history", null);
        return cursor;
    }
    public Cursor select_reverse(){
        SQLiteDatabase db=getReadableDatabase();
        String result="";
        Cursor cursor=db.rawQuery("select * from history order by num desc;", null);
        Log.e("리버스 설렉트 실행 확인","11");
        return cursor;
    }
}
