package com.james_jiang.weatherreport.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JC on 2016/8/9.
 */
public class AreasDatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_AREAS_DB = "create table areasInfo ("
            + "id integer primary key autoincrement, "
            + "area_id text, "
            + "area_name_en text, "
            + "area_name_ch text, "
            + "city text, "
            + "province text)";
    public AreasDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_AREAS_DB);        //创建表格，因为内置了数据库，所以不用创建了
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
