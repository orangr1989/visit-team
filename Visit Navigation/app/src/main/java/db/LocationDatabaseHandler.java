package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import DataModel.Location;
import DataModel.Map;

/**
 * Created by nisan on 20/05/2016.
 */
public class LocationDatabaseHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "visit";
    private static final String TABLE_LOCATIONS = "locations";

    // maps table columns names
    private static final String KEY_LOCATION_ID = "locationId";
    private static final String REMOTE_ID = "remoteId";
    private static final String KEY_SYMBOLIC_ID = "symbolicId";
    private static final String MAP_ID = "mapId";
    private static final String MAP_X_CODE = "mapXCord";
    private static final String MAP_Y_CODE = "mapYCord";

    private MapDatabaseHandler mMapDatabaseHandler;

    public LocationDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mMapDatabaseHandler = new MapDatabaseHandler(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mMapDatabaseHandler.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        mMapDatabaseHandler.onUpgrade(db, i, i1);
    }

    public void addLocation(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues locationValues = new ContentValues();
        locationValues.put(REMOTE_ID, location.getId());
        locationValues.put(KEY_SYMBOLIC_ID, location.getSymbolicID());
        locationValues.put(MAP_ID, location.getMap().getId());
        locationValues.put(MAP_X_CODE, location.getMapXcord());
        locationValues.put(MAP_Y_CODE, location.getMapYcord());

        // insert map into maps table
        long mapId = db.insert(TABLE_LOCATIONS, null, locationValues);

        db.close();
    }

    public Location getLocation(int id) {
        Location location = null;

        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query
        Cursor cursor = db.query(TABLE_LOCATIONS,
                new String[] {KEY_SYMBOLIC_ID, MAP_ID, MAP_X_CODE, MAP_Y_CODE},
                REMOTE_ID + " = ?", new String[] { String.valueOf(id) },
                null, null, null, null);

        if (cursor.moveToFirst()) {
            // parse map data
            String symbolicName = cursor.getString(0);
            int mapId = cursor.getInt(1);
            int mapXcode = cursor.getInt(2);
            int mapYcode = cursor.getInt(3);

            Map map = mMapDatabaseHandler.getMap(mapId);

            // create new location
            location = new Location(symbolicName, map, mapXcode, mapYcode, 100);
            location.setId(id);
        }

        cursor.close();
        db.close();
        return location;
    }

    public ArrayList<Location> getAllLocations() {
        ArrayList<Location> locations = new ArrayList<Location>();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_LOCATIONS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null); // SQL query

        // loop through all location rows and add to list
        if (cursor.moveToFirst()) {
            do {
                // parse location data
                int id = cursor.getInt(1);
                String symbolicName = cursor.getString(2);
                int mapId = cursor.getInt(3);
                int mapXcode = cursor.getInt(4);
                int mapYcode = cursor.getInt(5);

                // create new location
                Map map = mMapDatabaseHandler.getMap(mapId);

                // create new location
                Location location = new Location(symbolicName, map, mapXcode, mapYcode, 100);
                location.setId(id);

                locations.add(location); // add to list returned maps
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return locations;
    }

    public void deleteAllLocations() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATIONS, null, null); // delete all maps
        db.close();
    }

    public void addLocations(List<Location> LocationsToAdd) {
        List<Location> locationsFromDb = getAllLocations();
        HashMap<Integer, Location> location = new HashMap<Integer, Location>();
        for (Location l : locationsFromDb) {
            location.put(l.getId(), l);
        }

        for (Location l : LocationsToAdd) {
            if (!location.containsKey(l.getId()))
                addLocation(l);
        }
    }
}
