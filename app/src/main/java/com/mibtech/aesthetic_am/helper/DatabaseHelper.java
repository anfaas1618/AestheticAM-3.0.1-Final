package com.mibtech.aesthetic_am.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "ekart.db";
    public static final String TABLE_FAVOURITE_NAME = "tblfavourite";
    public static final String KEY_ID = "pid";

    final String TABLE_ORDER_NAME = "tblorder";
    final String PID = "pid";
    final String VID = "vid";
    final String QTY = "qty";
    final String FavouriteTableInfo = TABLE_FAVOURITE_NAME + "(" + KEY_ID + " TEXT" + ")";
    final String OrderTableInfo = TABLE_ORDER_NAME + "(" + VID + " TEXT ," + PID + " TEXT ," + QTY + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + FavouriteTableInfo);
        db.execSQL("CREATE TABLE " + OrderTableInfo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        replaceDataToNewTable(db, TABLE_FAVOURITE_NAME, FavouriteTableInfo);
        replaceDataToNewTable(db, TABLE_ORDER_NAME, OrderTableInfo);
        onCreate(db);
    }

    void replaceDataToNewTable(SQLiteDatabase db, String tableName, String tableString) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableString);

        List<String> columns = getColumns(db, tableName);
        db.execSQL("ALTER TABLE " + tableName + " RENAME TO temp_" + tableName);
        db.execSQL("CREATE TABLE " + tableString);

        columns.retainAll(getColumns(db, tableName));
        String cols = join(columns);
        db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s from temp_%s",
                tableName, cols, cols, tableName));
        db.execSQL("DROP TABLE temp_" + tableName);
    }

    List<String> getColumns(SQLiteDatabase db, String tableName) {
        List<String> ar = null;
        try (Cursor c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null)) {
            if (c != null) {
                ar = new ArrayList<>(Arrays.asList(c.getColumnNames()));
            }
        } catch (Exception e) {

        }
        return ar;
    }

    String join(List<String> list) {
        StringBuilder buf = new StringBuilder();
        int num = list.size();
        for (int i = 0; i < num; i++) {
            if (i != 0)
                buf.append(",");
            buf.append(list.get(i));
        }
        return buf.toString();
    }

    public boolean getFavouriteById(String pid) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{pid};
        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + " FROM " + TABLE_FAVOURITE_NAME + " WHERE " + KEY_ID + "=? ", args);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }

    public void AddOrRemoveFavorite(String id, boolean isAdd) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (isAdd) {
            addFavourite(id);
        } else {
            db.execSQL("DELETE FROM  " + TABLE_FAVOURITE_NAME + " WHERE " + KEY_ID + " = " + id);
        }
        db.close();
    }

    public void addFavourite(String id) {
        ContentValues fav = new ContentValues();
        fav.put(DatabaseHelper.KEY_ID, id);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_FAVOURITE_NAME, null, fav);
    }

    public ArrayList<String> getFavourite() {
        final ArrayList<String> ids = new ArrayList<>();
        String selectQuery = "SELECT *  FROM " + TABLE_FAVOURITE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return ids;
    }

    public ArrayList<String> getCartList() {
        final ArrayList<String> ids = new ArrayList<>();
        String selectQuery = "SELECT *  FROM " + TABLE_ORDER_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String count = cursor.getString(cursor.getColumnIndex(QTY));
                if (count.equals("0")) {
                    db.execSQL("DELETE FROM " + TABLE_ORDER_NAME + " WHERE " + VID + " = ? AND " + PID + " = ?", new String[]{cursor.getString(cursor.getColumnIndexOrThrow(VID)), cursor.getString(cursor.getColumnIndexOrThrow(PID))});

                } else
                    ids.add(cursor.getString(cursor.getColumnIndexOrThrow(VID)));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return ids;
    }

    public HashMap<String, String> getDataCartList() {
        final HashMap<String, String> ids = new HashMap<>();
        String selectQuery = "SELECT *  FROM " + TABLE_ORDER_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String count = cursor.getString(cursor.getColumnIndex(QTY));
                if (count.equals("0")) {
                    db.execSQL("DELETE FROM " + TABLE_ORDER_NAME + " WHERE " + VID + " = ? AND " + PID + " = ?", new String[]{cursor.getString(cursor.getColumnIndexOrThrow(VID)), cursor.getString(cursor.getColumnIndexOrThrow(PID))});
                } else
                    ids.put(cursor.getString(cursor.getColumnIndexOrThrow(VID)), cursor.getString(cursor.getColumnIndexOrThrow(QTY)));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return ids;
    }

    public int getTotalItemOfCart(Activity activity) {
        String countQuery = "SELECT  * FROM " + TABLE_ORDER_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        Constant.TOTAL_CART_ITEM = count;
        activity.invalidateOptionsMenu();
        return count;
    }

    public void AddOrderData(String vid, String pid, String qty) {
        try {
            if (!CheckOrderExists(vid, pid).equalsIgnoreCase("0")) {
                UpdateOrderData(vid, pid, qty);
            } else {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(VID, vid);
                values.put(PID, pid);
                values.put(QTY, qty);
                db.insert(TABLE_ORDER_NAME, null, values);
                db.close();
            }
        } catch (Exception e) {

        }
    }

    public void UpdateOrderData(String vid, String pid, String qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (qty.equals("0")) {
            DeleteOrderData(vid, pid);
        } else {
            ContentValues values = new ContentValues();
            values.put(QTY, qty);
            db.update(TABLE_ORDER_NAME, values, VID + " = ? AND " + PID + " = ?", new String[]{vid, pid});
        }
        db.close();
    }

    public void DeleteOrderData(String vid, String pid) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_ORDER_NAME + " WHERE " + VID + " = ? AND " + PID + " = ?", new String[]{vid, pid});
        database.close();
    }

    public String CheckOrderExists(String vid, String pid) {

        String count = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDER_NAME + " WHERE " + VID + " = ? AND " + PID + " = ?", new String[]{vid, pid});
        if (cursor.moveToFirst()) {
            count = cursor.getString(cursor.getColumnIndex(QTY));
            if (count.equals("0")) {
                db.execSQL("DELETE FROM " + TABLE_ORDER_NAME + " WHERE " + VID + " = ? AND " + PID + " = ?", new String[]{vid, pid});

            }
        }
        cursor.close();
        db.close();

        return count;
    }

    public void DeleteAllOrderData() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_ORDER_NAME);
        database.close();

    }

    public void DeleteAllFavoriteData() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_FAVOURITE_NAME);
        database.close();

    }
}