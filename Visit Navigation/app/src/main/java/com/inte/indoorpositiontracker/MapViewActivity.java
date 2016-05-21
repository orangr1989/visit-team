package com.inte.indoorpositiontracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.content.Intent;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import DataModel.Fingerprint;
import DataModel.Location;
import DataModel.Measurement;
import DataModel.measure.Wifi;
import Home.EntityHomeCallback;
import Home.LocationHome;
import Handler.Response;

public class MapViewActivity extends MapActivity {
    public final static String EXTRA_MESSAGE_MAP = "com.inte.indoorpositiontracker.MAP";
    public final static String EXTRA_MESSAGE_LOCATION_DEST = "com.inte.indoorpositiontracker.LOCATION_DEST";
    public final static String EXTRA_MESSAGE_X_CORD = "com.inte.indoorpositiontracker.X";
    public final static String EXTRA_MESSAGE_Y_CORD = "com.inte.indoorpositiontracker.Y";
    
    private static final int MENU_ITEM_EDIT_MAP = 100;
    private static final int MENU_ITEM_CHOOSE_LOCATION = 101;
    
    public static final int SCAN_DELAY = 1000; // delay for the first scan (milliseconds)
    public static final int SCAN_INTERVAL = 1000; // interval between scans (milliseconds)
    public static final int MAX_SCAN_THREADS = 2; // max amount of simultaneus scans
    
    private int mScanThreadCount = 0;
    
    // UI pointer to visualize user where he is on the map
    private WifiPointView mLocationPointer;
    
    // handler for callbacks to the UI thread
    private static Handler sUpdateHandler = new Handler();

    // runnable to refresh map (called by the handler)
    private Runnable mRefreshMap = new Runnable() {
        public void run() {
             refreshMap();
        }
    };
    
    private boolean mPaused = false; // used to detect if the application is on map edit mode
    
    
    /** INSTANCE METHODS*/
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationPointer = mMap.createNewWifiPointOnMap(new PointF(-1000, -1000));
        mLocationPointer.activate();
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if(mPaused == false) { // start scan only when this activity is active
                    mWifi.startScan();
                }
            }
            
        }, SCAN_DELAY, SCAN_INTERVAL);
    }
    
    public void onResume() {
        super.onResume();

        mPaused = false;
    }
    
    public void onPause() {
        super.onPause();

        mPaused = true;
    }
    
    @Override
    public void onReceiveWifiScanResults(final List<ScanResult> results) {
        IndoorPositionTracker application = (IndoorPositionTracker) getApplication();
        final ArrayList<Fingerprint> fingerprints = application.getFingerprintData(currMap.getMapName());
        
        // calculating the location might take some time in case there are a lot of fingerprints (>10000),
        // so it's reasonable to limit scan thread count to make sure there are not too many of these threads
        // going on at the same time
        if(results.size() > 0 && fingerprints.size() > 0 && mScanThreadCount <= MAX_SCAN_THREADS) {
            Thread t = new Thread() {
                public void run() {
                    mScanThreadCount++;

                    Measurement measure = new Measurement();
                    Vector<Wifi> wifiList = new Vector<Wifi>();
                    for (ScanResult result : results) {
                        Wifi wifi = new Wifi();
                        wifi.setBssid(result.BSSID);
                        wifi.setRssi(result.level);
                        wifi.setSsid(result.SSID);

                        wifiList.add(wifi);
                    }

                    measure.setWiFiReadings(wifiList);

                    // find fingerprint closest to our location (one with the smallest euclidean distance to us)
                    LocationHome.getLocation(measure,
                            new EntityHomeCallback() {
                                @Override
                                public void onResponse(Response<?> response) {
                                    Location l = (Location) response.getData();

                                    mLocationPointer.setFingerprint(l); // translate UI pointer to new location on screen

                                    // need to refresh map through updateHandler since only UI thread is allowed to touch its views
                                    sUpdateHandler.post(mRefreshMap);
                                }

                                @Override
                                public void onFailure(Response<?> response) {
                                    Log.d("MapViewActivity", response.getMessage());
                                }
                            }
                    );

                    mScanThreadCount--;
                }
            };
            t.start(); // start new scan thread
        }
    }
    
    public void startMapEditActivity() {
        Intent intent = new Intent(MapViewActivity.this, MapEditActivity.class);
        intent.putExtra(EXTRA_MESSAGE_MAP, currMap.getId());
        startActivity(intent); // start map edit mode
    }

    public void startMapNavigationActivity(int id) {
        Intent intent = new Intent(MapViewActivity.this, MapNavigationActivity.class);
        intent.putExtra(EXTRA_MESSAGE_LOCATION_DEST, id);
        intent.putExtra(EXTRA_MESSAGE_MAP, currMap.getId());

        intent.putExtra(EXTRA_MESSAGE_X_CORD, 687);
        intent.putExtra(EXTRA_MESSAGE_Y_CORD, 410);

        startActivity(intent); // start map edit mode
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add floor items
        List<Location> locations = mApplication.getLocations();

        SubMenu subLocation = menu.addSubMenu(Menu.NONE, MENU_ITEM_CHOOSE_LOCATION, Menu.NONE, "Choose location");
        for (Location l : locations) {
            subLocation.add(Menu.NONE, 1000 + l.getId(), Menu.NONE, l.getSymbolicID());
        }

        // add menu items
        menu.add(Menu.NONE, MENU_ITEM_EDIT_MAP, Menu.NONE, "Edit map");
        super.onCreateOptionsMenu(menu); // items for changing map
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == MENU_ITEM_EDIT_MAP) { // start map edit mode
            startMapEditActivity();
            return true;
        }
        else if (item.getItemId() > 1000) {
            startMapNavigationActivity(item.getItemId() - 1000);
            return true;
        } else { // change map
            mLocationPointer.setPoint(new PointF(-1000, -1000));
            return super.onOptionsItemSelected(item);
        }
    }
}
