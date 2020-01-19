package com.example.taskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    //DATABASE PARAMETERS
    private static final String DATABASE_NAME = "tasklist.db";
    private static final String TABLE_NAME = "TASK_LIST";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "NAME";
    private static final String COL_3 = "STATUS";

    DataBaseHelper(@Nullable Context context) {
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
            contentValues.put(COL_3, TaskStatus.OPEN);
            db.insert(TABLE_NAME, null, contentValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    private Cursor findRecordByID(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_1 + " = ?", new String[] {String.valueOf(id)});
    }

     Cursor getAllRecords(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    Cursor checkForStatus(String status){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COL_1 + " FROM " + TABLE_NAME + " WHERE " + COL_3 + " = ?", new String[] {status});
    }

    Cursor getAllStatuses(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " +COL_1 + ", " + COL_3 + " FROM " + TABLE_NAME, null);
    }

    Cursor findStatusByID(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COL_3 + " FROM " + TABLE_NAME + " WHERE " + COL_1 + " =?", new String[] {String.valueOf(id)});
    }

    boolean changeStatusOnID(int id, String newStatus){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = findRecordByID(id);
        cursor.moveToNext();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, cursor.getString(0));
        contentValues.put(COL_2, cursor.getString(1));
        contentValues.put(COL_3, newStatus);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[] {String.valueOf(id)});
        return true;
    }


}
