package com.inte.indoorpositiontracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import DataModel.Fingerprint;
import DataModel.Location;
import DataModel.Map;
import DataModel.Measurement;
import DataModel.measure.Wifi;

public class MapEditActivity extends MapActivity {
    private static final int MENU_ITEM_SCAN = 31;
    private static final int MENU_ITEM_FINGERPRINTS = 32;
    private static final int MENU_ITEM_SHOW_FINGERPRINTS = 33;
    private static final int MENU_ITEM_DELETE_FINGERPRINTS = 34;
    private static final int MENU_ITEM_EXIT = 35;
    
    private static final int MIN_SCAN_COUNT = 3; // minimum amount of scans required the scan to be successful
    private static final int SCAN_COUNT = 3; // how many scans will be done for calculating average for scan results
    private static final int SCAN_INTERVAL = 500; // interval between scans (milliseconds)
    
    private int mScansLeft = 0;
    
    // UI pointer to visualize user where in the screen a new fingerprint will be added after scan
    private WifiPointView mPointer; 
    
    private long mTouchStarted; // used for detecting tap events
    
    private ProgressDialog mLoadingDialog; // loading bar which is shown while scanning access points
    
    private HashMap<String, Integer> mMeasurements; // for storing measurement data during the scan
    
    private boolean mShowFingerprints = true;
    
    
    
    /** INSTANCE METHODS */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        //String floor = intent.getStringExtra(MapViewActivity.EXTRA_MESSAGE_FLOOR);
        setMap(currMap.getMapURL());
        
        setTitle(getTitle() + " (edit mode)");
    }
    
    @Override
    public void onReceiveWifiScanResults(List<ScanResult> results) {
        if(mScansLeft != 0 && mPointer != null) { // get scan results only when scan was started from this activity
            if(results.size() >= MIN_SCAN_COUNT) { // accept only scans with enough found access points
                mScansLeft--;
                
                // add scan results to hashmap
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
                
                if(mScansLeft > 0) { // keep on scanning
                    scanNext();
                } else {
                    Location l = new Location("", currMap, (int)mPointer.getLocation().x, (int)mPointer.getLocation().y, 100);

                    Fingerprint f = new Fingerprint(l, measure); // create fingerprint with the calculated measurement averages
                    mMap.createNewWifiPointOnMap(f, mShowFingerprints); // add to map UI
                    
                    mApplication.addFingerprint(f); // add to database
                    mLoadingDialog.dismiss(); // hide loading bar
                }
            } else { // did not find enough access points, show error to user
                mLoadingDialog.dismiss(); // hide loading bar
                Toast.makeText(getApplicationContext(), "Failed to create fingerprint. Could not find enough access points (found "
                        + results.size() + ", need at least " + MIN_SCAN_COUNT + ").", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    public boolean onTouch(View v, MotionEvent event) {
        v.onTouchEvent(event); // handle map etc touch events
        
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mTouchStarted = event.getEventTime(); // calculate tap start
                break;
            case MotionEvent.ACTION_UP:
                if (event.getEventTime() - mTouchStarted < 150) { // user tapped the screen
                    PointF location = new PointF(event.getX(), event.getY()); // get touch location
                    
                    // add pointer on screen where the user tapped and start wifi scan
                    if(mPointer == null) {
                        mPointer = mMap.createNewWifiPointOnMap(location);
                        mPointer.activate();
                    } else {
                        mMap.setWifiPointViewPosition(mPointer, location);
                    }
                    refreshMap(); // redraw map
                }
                break;
        }
        
        return true; // indicate event was handled
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add menu items
        menu.add(Menu.NONE, MENU_ITEM_SCAN, Menu.NONE, "Scan");
        
        SubMenu sub = menu.addSubMenu(Menu.NONE, MENU_ITEM_FINGERPRINTS, Menu.NONE, "Fingerprints");
        sub.add(Menu.NONE, MENU_ITEM_SHOW_FINGERPRINTS, Menu.NONE, (mShowFingerprints ? "Hide fingerprints" : "Show fingerprints"));
        sub.add(Menu.NONE, MENU_ITEM_DELETE_FINGERPRINTS, Menu.NONE, "Delete all fingerprints");
   
        menu.add(Menu.NONE, MENU_ITEM_EXIT, Menu.NONE, "Exit edit mode");
        super.onCreateOptionsMenu(menu); // items for changing map
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case MENU_ITEM_SCAN: // start scan
                if(mPointer == null) {
                    // notify user that UI pointer must be positioned first before the scan can be started
                    Toast.makeText(getApplicationContext(), "Failed to start scan. Tap on the map first" +
                    		" to place the position marker.", Toast.LENGTH_LONG).show();
                } else {
                    startScan(); // show loading dialog and start wifi scan
                }
                return true;
            case MENU_ITEM_SHOW_FINGERPRINTS: // show/hide fingerprints
                setFingerprintVisibility(!mShowFingerprints);
                item.setTitle(mShowFingerprints ? "Hide fingerprints" : "Show fingerprints");
                return true;
            case MENU_ITEM_DELETE_FINGERPRINTS: // delete all fingerprints (from screen and database)
                deleteAllFingerprints();
                return true;
            case MENU_ITEM_EXIT: // exit edit mode
                finish();
                return true;
            default: // change map
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void startScan() {
        mScansLeft = SCAN_COUNT;
        mMeasurements = new HashMap<String, Integer>();
        mLoadingDialog = ProgressDialog.show(this, "", "Scanning..", true); // show loading bar
        mWifi.startScan();
    }
    
    public void scanNext() {
        Timer timer = new Timer();
        
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                mWifi.startScan(); 
            }
            
        }, SCAN_INTERVAL);
    }
    
    public void setFingerprintVisibility(boolean visible) {
        mShowFingerprints = visible;
        mMap.setWifiPointsVisibility(visible);
        
        if (mPointer != null) {
            mPointer.setVisible(true); // pointer is always visible
        }
        
        refreshMap(); // redraw map
    }
    
    public void deleteAllFingerprints() {
        // create alert dialog and delete all fingerprints only after user has confirmed he wants to delete them
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Are you sure?");
        alertDialogBuilder.setMessage("Are sure you want to delete all fingerprints?");
        
        // add yes button to dialog
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                mMap.deleteFingerprints(); // delete fingerprints from the screen
                mApplication.deleteAllFingerprints(currMap.getMapName()); // delete fingerprints from the database
            }
        });
        
        // add no button to dialog
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel(); // close the dialog and do nothing
            }
        });

        // create the dialog and show it
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    
    @Override
    public void setMap(int resId) {
        super.setMap(resId);
        mMap.deleteFingerprints(); // clear screen from fingerprints
        
        ArrayList<Fingerprint> fingerprints = mApplication.getFingerprintData(currMap.getMapName()); // load fingerprints from the database
        
        // add WifiPointViews on map with fingerprint data loaded from the database
        for(Fingerprint fingerprint : fingerprints) {
            mMap.createNewWifiPointOnMap(fingerprint, mShowFingerprints);
        }
        
    }

    @Override
    public void setMap(String url) {
        super.setMap(url);
        mMap.deleteFingerprints(); // clear screen from fingerprints

        ArrayList<Fingerprint> fingerprints = mApplication.getFingerprintData(currMap.getMapName()); // load fingerprints from the database

        // add WifiPointViews on map with fingerprint data loaded from the database
        for(Fingerprint fingerprint : fingerprints) {
            mMap.createNewWifiPointOnMap(fingerprint, mShowFingerprints);
        }

    }
}
