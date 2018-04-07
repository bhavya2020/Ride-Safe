package com.example.bhavya.safego.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bhavya on 7/4/18.
 */

public class accelerometerDbHelper extends SQLiteOpenHelper{

    // The database name
    private static final String DATABASE_NAME = "accelerometer.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 2;

    // Constructor
    public accelerometerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " + accelerometerContract.accelerometer.TABLE_NAME + " (" +
                accelerometerContract.accelerometer.X+ " FLOAT ," +
                accelerometerContract.accelerometer.Y + " FLOAT , " +
                accelerometerContract.accelerometer.Z + " FLOAT , " +
                accelerometerContract.accelerometer.COLUMN_TIMESTAMP + " STRING " +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +accelerometerContract.accelerometer.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
