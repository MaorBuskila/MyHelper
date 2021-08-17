package com.example.myhelper66;

import static android.icu.text.MessagePattern.ArgType.SELECT;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDataBase extends SQLiteOpenHelper {

    public static SQLiteDatabase sqLiteDatabase;
    private Context context;
    private static final String DATABASE_NAME = "BlackList.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_blacklist";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PHONE_NUMBER = "phoneNumber";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ISBLOCK = "is_blocked";


    public MyDataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PHONE_NUMBER + " INTEGER, " +
                COLUMN_ISBLOCK + " INTEGER);";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
    onCreate(db);
    }


    void addToBlackList (String name, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_PHONE_NUMBER, phoneNumber);
        cv.put(COLUMN_ISBLOCK, "1");




        if (!CheckIsDataAlreadyInDBorNot(phoneNumber)) {
            db.insert(TABLE_NAME, null, cv);
         }
        }

    void BlockContact(String phoneNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ISBLOCK, "0");

        long result = db.update(TABLE_NAME ,cv , "phoneNumber=?", new String[]{phoneNumber});
        if (result == -1){
            Toast.makeText(context, "failed to update", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "blocked", Toast.LENGTH_SHORT).show();
        }

    }
    void UnBlockContact(String phoneNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ISBLOCK, "1");

        long result = db.update(TABLE_NAME ,cv , "phoneNumber=?", new String[]{phoneNumber});
        if (result == -1){

            Toast.makeText(context, "failed to update", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Unblocked", Toast.LENGTH_SHORT).show();

        }

    }


    public boolean CheckIsDataAlreadyInDBorNot(String contactNumber) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + COLUMN_PHONE_NUMBER + " = " + contactNumber;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        sqldb.close();
        return true;
    }
    public boolean CheckIfContactBlocked(String contactNumber) {
        SQLiteDatabase sqldb = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + COLUMN_PHONE_NUMBER + " = " + contactNumber;
        Cursor cursor = sqldb.rawQuery(Query, null);
        cursor.moveToFirst();
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        else {
            if (cursor.getString(3).equals("0")) {
//            Toast.makeText(context, cursor.getString(3), Toast.LENGTH_SHORT).show();
                cursor.close();
                return true;
            } else {
//            Toast.makeText(context, cursor.getString(3), Toast.LENGTH_SHORT).show();
                cursor.close();
                return false;
            }
        }
    }








    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
