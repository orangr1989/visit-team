package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import DataModel.Map;

/**
 * Created by nisan on 16/05/2016.
 */
public class MapDatabaseHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "visit";
    private static final String TABLE_MAPS = "maps";

    // maps table columns names
    private static final String KEY_MAP_ID = "mapId";
    private static final String REMOTE_ID = "remoteId";
    private static final String KEY_BUILDING = "buildingId";
    private static final String MAP_NAME = "mapName";
    private static final String MAP_URL = "mapURL";
    private static final String FLOOR_NUMBER = "floorNum";

    public MapDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create tables

        String CREATE_MAPS_TABLE = "CREATE TABLE " + TABLE_MAPS + "("
                + KEY_MAP_ID + " INTEGER PRIMARY KEY,"
                + REMOTE_ID + " INTEGER,"
                + KEY_BUILDING + " INTEGER,"
                + MAP_NAME + " TEXT,"
                + FLOOR_NUMBER + " INTEGER,"
                + MAP_URL + " TEXT" + ")";
        db.execSQL(CREATE_MAPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAPS);

        // Create table again
        onCreate(db);
    }

    public void addMap(Map map) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues mapValues = new ContentValues();
        mapValues.put(REMOTE_ID, map.getId());
        //mapValues.put(KEY_BUILDING, 1);
        mapValues.put(MAP_NAME, map.getMapName());
        mapValues.put(MAP_URL, map.getMapURL());
        mapValues.put(FLOOR_NUMBER, map.getMapFloorNumber());

        // insert map into maps table
        long mapId = db.insert(TABLE_MAPS, null, mapValues);

        db.close();
    }

    public Map getMap(int id) {
        Map map = null;

        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query
        Cursor cursor = db.query(TABLE_MAPS,
                new String[] {KEY_MAP_ID, MAP_NAME, MAP_URL, FLOOR_NUMBER},
                REMOTE_ID + " = ?", new String[] { String.valueOf(id) },
                null, null, null, null);

        if (cursor.moveToFirst()) {
            // parse map data
            String mapName = cursor.getString(1);
            String mapUrl = cursor.getString(2);
            int floorNumber = cursor.getInt(3);

            // create new map
            map = new Map(mapName, mapUrl, floorNumber);
            map.setId(id);
        }

        cursor.close();
        db.close();
        return map;
    }

    public ArrayList<Map> getAllMaps() {
        ArrayList<Map> maps = new ArrayList<Map>();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_MAPS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null); // SQL query

        // loop through all fingerprint rows and add to list
        if (cursor.moveToFirst()) {
            do {
                // parse map data
                int id = cursor.getInt(1);
                int building = cursor.getInt(2);
                String mapName = cursor.getString(3);
                int floorNumber = cursor.getInt(4);
                String mapUrl = cursor.getString(5);

                // create new map
                Map m = new Map(mapName, mapUrl, floorNumber);
                m.setId(id);

                maps.add(m); // add to list returned maps
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return maps;
    }

    public void deleteAllFingerprints() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MAPS, null, null); // delete all maps
        db.close();
    }
}
