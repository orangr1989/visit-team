package com.inte.indoorpositiontracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import DataModel.Location;
import DataModel.Map;
import Handler.Response;
import Home.EntityHomeCallback;
import Home.LocationHome;
import Home.MapHome;
import db.LocationDatabaseHandler;
import db.MapDatabaseHandler;

/**
 * {@link SynchronizationManager} synchronizes the local database with the server
 * Created by nisan on 16/05/2016.
 */
public class SynchronizationManager extends Service {

    private static final String TAG = SynchronizationManager.class.getSimpleName();
    private static boolean isSynced = false;
    public static boolean syncInProgress = false;

    public static MapDatabaseHandler mMapDatabaseHandler;
    public static LocationDatabaseHandler mLocationDatabaseHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        mMapDatabaseHandler = new MapDatabaseHandler(this);
        mLocationDatabaseHandler = new LocationDatabaseHandler(this);
        sync();
    }

    public void sync() {

        Log.d("SynchronizationManager", "Start sync function");

        if (isSynced) {
            stopSelf();
            return;
        }

        if (syncInProgress)
            return;

        syncInProgress = true;

        MapHome.getMapList(new EntityHomeCallback() {

            @Override
            public void onResponse(Response<?> response) {
                List<Map> maps = (List<Map>) response.getData();

                mMapDatabaseHandler.addMaps(maps);
                Log.v(TAG, "database map synchronized");


                LocationHome.getLocationList(new EntityHomeCallback() {
                    @Override
                    public void onResponse(Response<?> response) {
                        List<Location> locations = (List<Location>) response.getData();

                        mLocationDatabaseHandler.addLocations(locations);
                        Log.v(TAG, "database location synchronized");
                        isSynced = true;
                        syncInProgress = false;
                    }

                    @Override
                    public void onFailure(Response<?> response) {
                        Log.v(TAG, "database location synchronized failed");
                        syncInProgress = false;
                    }
                });
            }

            @Override
            public void onFailure(Response<?> response) {
                Log.v(TAG, "database map synchronized failed");
                syncInProgress = false;
            }
        });

        Log.d("SynchronizationManager", "Finish sync function");
    }

    public class LocalBinder extends Binder {
        /**
         *
         * @return {@link SynchronizationManager}
         */
        SynchronizationManager getService() {
            return SynchronizationManager.this;
        }
    }

    private final LocalBinder mBinder = new LocalBinder();

/*    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_REDELIVER_INTENT;
    }*/
}
