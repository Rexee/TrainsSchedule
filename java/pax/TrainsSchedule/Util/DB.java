package pax.TrainsSchedule.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import pax.TrainsSchedule.Model.Favorite;

public class DB extends SQLiteOpenHelper {

    private static final String DB_NAME    = "main.db";
    private static final int    DB_VERSION = 23;

    private static final String TABLE_FAVORITES = "favorites";

    private static final String FIELD_STATION_FROM_CODE = "from_code";
    private static final String FIELD_STATION_FROM_NAME = "from_name";
    private static final String FIELD_STATION_TO_CODE   = "to_code";
    private static final String FIELD_STATION_TO_NAME   = "to_name";

    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryString = String.format("CREATE TABLE %s (%s TEXT, %s TEXT, %s TEXT, %s TEXT)", TABLE_FAVORITES, FIELD_STATION_FROM_CODE, FIELD_STATION_FROM_NAME, FIELD_STATION_TO_CODE, FIELD_STATION_TO_NAME);
        db.execSQL(queryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
            onCreate(db);
        }
    }

    public void addToFavorite(Favorite favorite) {
        final SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues row = new ContentValues();

        row.put(FIELD_STATION_FROM_CODE, favorite.fromCode);
        row.put(FIELD_STATION_FROM_NAME, favorite.fromName);
        row.put(FIELD_STATION_TO_CODE, favorite.toCode);
        row.put(FIELD_STATION_TO_NAME, favorite.toName);

        int updated = db.update(TABLE_FAVORITES, row, FIELD_STATION_FROM_CODE + " =? AND " + FIELD_STATION_TO_CODE + " =? ",
                new String[]{favorite.fromCode, favorite.toCode});

        S.L("update: " + updated);
        if (updated == 0) {
            long res = db.insert(TABLE_FAVORITES, null, row);
            S.L("insert: " + res);
        }
    }

    public List<Favorite> getFavoritesList() {
        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT " +
                FIELD_STATION_FROM_CODE + ", " +
                FIELD_STATION_FROM_NAME + ", " +
                FIELD_STATION_TO_CODE + ", " +
                FIELD_STATION_TO_NAME +
                " FROM " + TABLE_FAVORITES +
                " ORDER BY " + FIELD_STATION_FROM_NAME + " DESC", null);

        List<Favorite> result = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            result.add(new Favorite(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }
        cursor.close();

        return result;
    }

    public void deleteFavorite(Favorite favorite) {

        final SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete(TABLE_FAVORITES, FIELD_STATION_FROM_CODE + " =? AND " + FIELD_STATION_TO_CODE + " =? ",
                new String[]{favorite.fromCode, favorite.toCode});

        S.L("delete: " + res);
    }

    public boolean isInFavorites(String codeFrom, String codeTo) {
        final SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_FAVORITES + " WHERE " + FIELD_STATION_FROM_CODE + " = ? AND " + FIELD_STATION_TO_CODE + " =? LIMIT 1",
                new String[] { codeFrom, codeTo });

        boolean res = false;
        if (cursor.getCount() > 0) {
            res = true;
        }
        cursor.close();

        return res;
    }
}
