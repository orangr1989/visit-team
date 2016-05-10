package com.inte.indoorpositiontracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import android.content.Intent;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

public class MapViewActivity extends MapActivity {
    public final static String EXTRA_MESSAGE_FLOOR = "com.inte.indoorpositiontracker.FLOOR";
    
    private static final int MENU_ITEM_EDIT_MAP = 21;
    
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
    
    private HashMap<String, Integer> mMeasurements; // used to calculate weighted averages of signal strengths
    
    
    
    /** INSTANCE METHODS*/
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mMeasurements = new HashMap<String, Integer>();
        
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
        final ArrayList<Fingerprint> fingerprints = application.getFingerprintData(mSelectedMap);
        
        // calculating the location might take some time in case there are a lot of fingerprints (>10000),
        // so it's reasonable to limit scan thread count to make sure there are not too many of these threads
        // going on at the same time
        if(results.size() > 0 && fingerprints.size() > 0 && mScanThreadCount <= MAX_SCAN_THREADS) {
            Thread t = new Thread() {
                public void run() {
                    mScanThreadCount++;
                    
                    HashMap<String, Integer> measurements = new HashMap<String, Integer>();
                    for (ScanResult result : results) {
                        measurements.put(result.BSSID, result.level);
                    }
                    
                    TreeSet<String> keys = new TreeSet<String>();
                    keys.addAll(mMeasurements.keySet());
                    keys.addAll(measurements.keySet());
                    
                    // calculate access point signal strengths with weighted averages
                    // (adjust to suddent big changes in received signal strengths)
                    for (String key : keys) {
                        Integer value = measurements.get(key);
                        Integer oldValue = mMeasurements.get(key);
                        if(oldValue == null) {
                            mMeasurements.put(key, value);
                        } else if(value == null) {
                            mMeasurements.remove(key);
                        } else {
                            value = (int) (oldValue * 0.4f + value * 0.6f);
                            mMeasurements.put(key, value);
                        }
                    }
                    
                    
                    Fingerprint f = new Fingerprint(mMeasurements);
                    
                    // find fingerprint closest to our location (one with the smallest euclidean distance to us)
                    Fingerprint closestMatch = f.getClosestMatch(fingerprints); 
                    
                    mLocationPointer.setFingerprint(closestMatch); // translate UI pointer to new location on screen
                    
                    // need to refresh map through updateHandler since only UI thread is allowed to touch its views
                    sUpdateHandler.post(mRefreshMap); 
                    
                    mScanThreadCount--;
                }
            };
            t.start(); // start new scan thread
        }
    }
    
    public void startMapEditActivity() {
        Intent intent = new Intent(MapViewActivity.this, MapEditActivity.class);
        intent.putExtra(EXTRA_MESSAGE_FLOOR, mSelectedMap);
        startActivity(intent); // start map edit mode
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add menu items
        super.onCreateOptionsMenu(menu); // items for changing map
        menu.add(Menu.NONE, MENU_ITEM_EDIT_MAP, Menu.NONE, "Edit map"); 
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case MENU_ITEM_EDIT_MAP: // start map edit mode
                startMapEditActivity();
                return true;
        default: // change map
                return super.onOptionsItemSelected(item);
        }
    }
}
