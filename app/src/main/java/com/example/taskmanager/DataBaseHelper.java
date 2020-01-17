package com.example.taskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    //DATABASE PARAMETERS
    private static final String DATABASE_NAME = "tasklist.db";
    private static final String TABLE_NAME = "task_list";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "NAME";
    private static final String COL_3 = "STATUS";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creating table
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, STATUS TEXT)");
        //task names examples
        String[] tasksNames = {"Get nominated for an award", "Have a dream job",
                "Have a seat in government", "Become a landlord,", "Become a divemaster",
                "Become a lifeguard", "Open a coffe shop", "Become a legend",
                "Have a job that travels", "Become a dancer", "Do some time in the pease corps",
                "Become a travel Writer", "Work as a Wald Disney imagineer",
                "Become a Professional Cook", "Become a dentist", "Work for a political party",
                "Get a job that i love", "Work in healthcare", "Dramatically quit the job i hate",
                "Ask for a pay rise"};
        //inserting tasks examples to DB
        for(String task: tasksNames){
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_2, task);
            contentValues.put(COL_3, "OPEN");
            db.insert(TABLE_NAME, null, contentValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData (String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, "OPEN");
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
        {
            return false;
        }
        else {
            return true;
        }
    }




}
