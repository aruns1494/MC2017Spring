package com.group24.arun.group24;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Arun Subramanian on 06-03-2017.
 */

public class Helper extends SQLiteOpenHelper {

    static String tableName = "Name_ID_Age_Sex";
    static int DB_VERISION = 1;
    static String DB_NAME = "Group24";


    public Helper(Context context){
        super(context,DB_NAME,null,DB_VERISION);
    }

    public void addDataToTable(AccelerometerData ar, String  tablename){
        try{
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues val = new ContentValues();
            val.put("timeStamp", ar.getTimeStamp());
            val.put("xValue",ar.getxVal());
            val.put("yValue",ar.getyVal());
            val.put("zValue",ar.getzVal());
            db.insert(tablename,null,val);
        }
        catch (Exception e){
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sql){
    }

    @Override
    public void onUpgrade(SQLiteDatabase sql, int oldVersion, int newVersion){

    }
}
