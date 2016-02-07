package com.example.schedulerproject;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 현욱 on 2016-02-03.
 */
public class MyHelper extends SQLiteOpenHelper {

    LogManager logManager;

    public static final String COL_NAME = "name";
    Context context;

    public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table lists ( _id integer primary key autoincrement" +
                ", toDo text, memo text, dday integer, alarm integer, theDay text);";
        try {
            db.execSQL(sql);
//            확인을 위한 임의적인 초기화작업
            logManager.logPrint("create success");
        } catch(SQLException e) {
            logManager.logPrint("error : " + e);
        }
        logManager.logPrint("onCreate");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        logManager.logPrint("onOpen");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        logManager.logPrint(String.format("onUpgrade %d, newVersion %d", oldVersion, newVersion));
    }
}
