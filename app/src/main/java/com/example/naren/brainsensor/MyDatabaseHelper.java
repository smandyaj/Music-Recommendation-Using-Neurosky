package com.example.naren.brainsensor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Naren on 3/3/2017.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Group26.db";

    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    //private static final String DATABASE_CREATE_FRIDGE_ITEM = "create table FridgeItem(id integer primary key autoincrement,f_id text not null,food_item text not null,quantity text not null,measurement text not null,expiration_date text not null,current_date text not null,flag text not null,location text not null);";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        //database.execSQL(DATABASE_CREATE_FRIDGE_ITEM);

        database.execSQL("CREATE TABLE IF NOT EXISTS " + "brainSensor" +
                " ( data1 int,data2 int,data3 int,data4 int);");
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        Log.w(MyDatabaseHelper.class.getName(),"Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS FridgeItem");
        onCreate(database);
    }
}