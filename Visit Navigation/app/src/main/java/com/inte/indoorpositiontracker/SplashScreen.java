package com.inte.indoorpositiontracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import DataModel.Map;

import DataModel.Location;
import Handler.Response;
import Home.EntityHomeCallback;
import Home.LocationHome;
import Home.MapHome;
import db.LocationDatabaseHandler;
import db.MapDatabaseHandler;

public class SplashScreen extends Activity {
    private static boolean splashLoaded = false;

    private static final String TAG = SplashScreen.class.getSimpleName();
    private static boolean isSynced = false;
    public static boolean syncInProgress = false;

    public static MapDatabaseHandler mMapDatabaseHandler;
    public static LocationDatabaseHandler mLocationDatabaseHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        TextView txtView = (TextView)findViewById(R.id.txt_title);

        Typeface roboto = Typeface.createFromAsset(getAssets(),
                "font/Roboto-Thin.ttf"); //use this.getAssets if you are calling from an Activity

        txtView.setTypeface(roboto);

        MapDatabaseHandler mapDb = new MapDatabaseHandler(this);
        if (mapDb.getAllMaps().size() > 0)
            splashLoaded = true;

        if (!splashLoaded) {
            SyncData();
            splashLoaded = true;
        }
        else {
            startService(new Intent(SplashScreen.this,
                    SynchronizationManager.class));

            int secondsDelayed = 3;
            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent goToMainActivity = new Intent(SplashScreen.this, MapViewActivity.class);
                    goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(goToMainActivity);
                    finish();
                    findViewById(R.id.loadingPanel).setVisibility(TextView.GONE);
                }
            }, secondsDelayed * 1000);
        }
    }

    private  void SyncData(){

        mMapDatabaseHandler = new MapDatabaseHandler(this);
        mLocationDatabaseHandler = new LocationDatabaseHandler(this);

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
                        startActivity(new Intent(SplashScreen.this, MapViewActivity.class));
                        finish();
                        findViewById(R.id.loadingPanel).setVisibility(TextView.GONE);
                    }

                    @Override
                    public void onFailure(Response<?> response) {
                        Log.v(TAG, "database location synchronized failed");
                        syncInProgress = false;
                        startActivity(new Intent(SplashScreen.this, MapViewActivity.class));
                        finish();
                        findViewById(R.id.loadingPanel).setVisibility(TextView.GONE);
                    }
                });
            }

            @Override
            public void onFailure(Response<?> response) {
                Log.v(TAG, "database map synchronized failed");
                syncInProgress = false;
                startActivity(new Intent(SplashScreen.this, MapViewActivity.class));
                finish();
                findViewById(R.id.loadingPanel).setVisibility(TextView.GONE);
            }
        });
    }
}