package com.inte.indoorpositiontracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import DataModel.Map;
import Handler.Response;
import Home.EntityHomeCallback;
import Home.MapHome;

/**
 * {@link SynchronizationManager} synchronizes the local database with the server
 * Created by nisan on 16/05/2016.
 */
public class SynchronizationManager extends Service {

    private static final String TAG = SynchronizationManager.class.getSimpleName();
    private boolean isSynced = false;
    private boolean syncInProgress = false;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        sync();
    }

    private void sync() {

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
                isSynced = true;
                Log.v(TAG, "database synchronized");
                syncInProgress = false;
            }

            @Override
            public void onFailure(Response<?> response) {
                Log.v(TAG, "database map synchronized failed: "
                        + response.getMessage());
                syncInProgress = false;
            }
        });
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
}
