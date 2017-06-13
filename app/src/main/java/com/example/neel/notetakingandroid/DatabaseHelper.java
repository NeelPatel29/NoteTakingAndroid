package com.example.neel.notetakingandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NoteTaking.db";
    public static final String TABLE_NAME = "NoteTaking_Table";
    public static final String ID = "ID";
    public static final String COL_1 = "NOTE";
    public static final String COL_2 = "LOCATION";
    public static final String COL_3 = "IMAGE";
    public static final String COL_4 = "LAT";
    public static final String COL_5 = "LON";


    private String[] columns= {COL_1,COL_2,COL_3,COL_4,COL_5};

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NOTE TEXT, LOCATION TEXT, IMAGE TEXT, LAT TEXT , LON TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData (String note, String location, String image, String lat ,String lon) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put (COL_1, note);
        contentValues.put (COL_2, location);
        contentValues.put (COL_3, image);
        contentValues.put (COL_4, lat);
        contentValues.put (COL_5, lon);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public List<NotesPojo> readAllContacts(){
        SQLiteDatabase db = this.getWritableDatabase();

        List<NotesPojo> notes = new ArrayList<NotesPojo>();

        Cursor cursor = db.query(TABLE_NAME,columns, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            NotesPojo note = new NotesPojo();
            note.setNote(cursor.getString(0));
            note.setLocation(cursor.getString(1));
            note.setImage(cursor.getString(2));
            note.setLat(cursor.getString(3));
            note.setLon(cursor.getString(4));
            notes.add(note);
            cursor.moveToNext();
        }

        cursor.close();
        return notes;
    }

    public boolean updateData(String note, String location, String image,String lat,String lon) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = true;
        List<NotesPojo> notes = new ArrayList<NotesPojo>();

        Cursor cursor = db.query(TABLE_NAME,columns, null, null, null, null, null);

        cursor.moveToFirst();
        NotesPojo n = new NotesPojo();

        while (!cursor.isAfterLast()) {
            n.setNote(cursor.getString(0));
            n.setLocation(cursor.getString(1));
            n.setImage(cursor.getString(2));
            n.setLat(cursor.getString(3));
            n.setLon(cursor.getString(4));
            notes.add(n);
            Log.d("COL1:", n.getNote());
            cursor.moveToNext();
        }
        cursor.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put (COL_1, note);
        contentValues.put (COL_2, location);
        contentValues.put (COL_3, image);
        contentValues.put (COL_4, lat);
        contentValues.put (COL_5, lon);

        result =  db.update(TABLE_NAME, contentValues, COL_1 + " = ?", new String[] {note}) > 0;

        if (result == true) {
            return false;
        } else {
            return true;
        }

    }
    public void delete_byID(String s){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_1 + " = ?",
                new String[] { String.valueOf(s) });
        db.close();
    }
}
