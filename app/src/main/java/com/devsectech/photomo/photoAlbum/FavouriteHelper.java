package com.devsectech.photomo.photoAlbum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class FavouriteHelper extends SQLiteOpenHelper {
    private static final String COLUMN_PATH = "c_path";
    private static final String CREATE_TABLE = "CREATE TABLE favourite(c_id INTEGER PRIMARY KEY AUTOINCREMENT,c_path TEXT)";
    private static final String DATABASE_NAME = "fav_db";
    private static final String TABLE_NAME = "favourite";

    public FavouriteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS favourite");
        onCreate(sQLiteDatabase);
    }

    public boolean isPathExists(String str) {
        boolean z = true;
        String[] strArr = {str};
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().query(true, TABLE_NAME, new String[]{COLUMN_PATH}, "c_path =? ", strArr, null, null, null, null);
            if (cursor.getCount() <= 0) {
                z = false;
            }
            return z;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public long insertPath(String str) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PATH, str);
        long insert = writableDatabase.insert(TABLE_NAME, null, contentValues);
        writableDatabase.close();
        return insert;
    }

    public ArrayList<String> getAllFav() {
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("SELECT  * FROM favourite ORDER BY c_path DESC", null);
        if (rawQuery.moveToFirst()) {
            do {
                arrayList.add(rawQuery.getString(rawQuery.getColumnIndex(COLUMN_PATH)));
            } while (rawQuery.moveToNext());
        }
        writableDatabase.close();
        return arrayList;
    }


    public void deleteFav(String str) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete(TABLE_NAME, "c_path = ?", new String[]{String.valueOf(str)});
        writableDatabase.close();
    }

    public void deleteAllFav() {
        getWritableDatabase().execSQL("delete from favourite");
    }
}
