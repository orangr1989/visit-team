package com.inte.indoorpositiontracker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.DataHelper;
import com.arlib.floatingsearchview.suggestions.model.LocationSuggestion;
import com.arlib.floatingsearchview.suggestions.model.LocationWrapper;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import DataModel.Fingerprint;
import DataModel.Location;
import DataModel.Measurement;
import DataModel.measure.Wifi;
import Handler.Response;
import Home.EntityHomeCallback;
import Home.LocationHome;

public class MapViewActivity extends MapActivity {
    public final static String EXTRA_MESSAGE_MAP = "com.inte.indoorpositiontracker.MAP";
    public final static String EXTRA_MESSAGE_LOCATION_DEST = "com.inte.indoorpositiontracker.LOCATION_DEST";
    public final static String EXTRA_MESSAGE_X_CORD = "com.inte.indoorpositiontracker.X";
    public final static String EXTRA_MESSAGE_Y_CORD = "com.inte.indoorpositiontracker.Y";

    private final static  int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;

    private static final int MENU_ITEM_EDIT_MAP = 100;
    private static final int MENU_ITEM_CHOOSE_LOCATION = 101;

    public static final int SCAN_DELAY = 1000; // delay for the first scan (milliseconds)
    public static final int SCAN_INTERVAL = 1000; // interval between scans (milliseconds)
    public static final int MAX_SCAN_THREADS = 2; // max amount of simultaneus scans

    private int mScanThreadCount = 0;
    final Context context = this;
    private long mTouchStarted; // used for detecting tap events
    private Timer mTimer;
    private boolean btnLocClicked = false;

    // UI pointer to visualize user where he is on the map
    protected WifiPointView mLocationPointer;

    // handler for callbacks to the UI thread
    private static Handler sUpdateHandler = new Handler();

    // runnable to refresh map (called by the handler)
    private Runnable mRefreshMap = new Runnable() {
        public void run() {
             refreshMap();
        }
    };

    private boolean mPaused = false; // used to detect if the application is on map edit mode
    private FloatingSearchView mSearchView;
    private FloatingActionButton mLocationbtn;

    /** INSTANCE METHODS*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermission();

        mLocationPointer = mMap.createNewWifiPointOnMap(new PointF(-1000, -1000));
        mLocationPointer.activate();
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mLocationbtn = (FloatingActionButton) findViewById(R.id.myLocationButton);

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (mPaused == false) { // start scan only when this activity is active
                    mWifi.startScan();
                }
            }

        }, SCAN_DELAY, SCAN_INTERVAL);

        // floating search bar events
        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {

            @Override
            public void onFocus() {
                //this shows the top left circular progress
                //you can call it where ever you want, but
                //it makes sense to do it when loading something in
                //the background.
                mSearchView.showProgress();

                //simulates a query call to a data source
                //with a new query.
                List<Location> locs = mApplication.getLocations();

                String sLocations = new Gson().toJson(locs);

                mSearchView.swapSuggestions(getAllSuggestions(sLocations));

                mSearchView.hideProgress();
            }

            @Override
            public void onFocusCleared() {

            }
        });

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    List<Location> locs = mApplication.getLocations();

                    String sLocations = new Gson().toJson(locs);

                    DataHelper.find(sLocations, newQuery, new DataHelper.OnFindResultsListener() {

                        @Override
                        public void onResults(List<LocationSuggestion> results) {

                            //this will swap the data and
                            //render the collapse/expand animations as necessary
                            mSearchView.swapSuggestions(results);

                            //let the users know that the background
                            //process has completed
                            mSearchView.hideProgress();
                        }
                    });
                }
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

                LocationSuggestion locationSuggestion = (LocationSuggestion) searchSuggestion;

                mSearchView.setSearchText(locationSuggestion.getBody());

                Log.d("TAG", "onSuggestionClicked()");
                // CALL TO SERVER WITH SELECTED LOCATION

                Intent intent = new Intent(MapViewActivity.this, MapNavigationActivity.class);

                intent.putExtra(EXTRA_MESSAGE_LOCATION_DEST, locationSuggestion.getId());
                intent.putExtra(EXTRA_MESSAGE_X_CORD, (int) mLocationPointer.getLocation().x);
                intent.putExtra(EXTRA_MESSAGE_Y_CORD, (int) mLocationPointer.getLocation().y);
                intent.putExtra(EXTRA_MESSAGE_MAP, currMap.getId()); // needs to be curMap.getId()
                startActivity(intent); // send dest location id + current location(x + y + floorNum)

                mMap.setOnTouchListener(null); // remove manual pick location
                mPaused = false;
            }

            @Override
            public void onSearchAction() {
                Log.d("TAG", "onSearchAction()");
            }

        });

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                Log.d("TAG", "onActionMenuItemSelected()");
            }
        });

        // floating action button myLocation click event
        mLocationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!btnLocClicked) {
                    Toast.makeText(context, "Choose location manually",
                            Toast.LENGTH_SHORT).show();

                    // paused wifi scan, cos' user not interesed it.
                    mPaused = true;
                    locationPicker();

                    // change button background
                    mLocationbtn.setBackgroundTintList(getResources().getColorStateList(R.color.green));
                    btnLocClicked = true;
                } else {

                    Toast.makeText(context, "Wifi current location",
                            Toast.LENGTH_SHORT).show();

                    mPaused = false;

                    // change button background
                    mLocationbtn.setBackgroundTintList(getResources().getColorStateList(R.color.accent));
                    btnLocClicked = false;

                }
            }
        });

        // check if wifi turned off, and ask the userto turn it
        //WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (!mWifi.isWifiEnabled()){
            startActivateWifiDialog();
        }

    }

    private boolean checkPermission() {

        List<String> permissionsList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private List<? extends SearchSuggestion> getAllSuggestions(String locs) {

        List<LocationWrapper> sLocationWrappers = new ArrayList<LocationWrapper>();
        List<LocationSuggestion> suggestionList = new ArrayList<LocationSuggestion>();

        if(sLocationWrappers.isEmpty()) {
            sLocationWrappers = com.arlib.floatingsearchview.suggestions.model.DataHelper.deserializeLocations(locs);
        }

        for(LocationWrapper location: sLocationWrappers){

                suggestionList.add(new LocationSuggestion(location));
        }

        return  suggestionList;
    }

    public void onResume() {
        super.onResume();

        mMap.setOnTouchListener(null); // remove manual pick location
        mPaused = false;
        mLocationbtn.setBackgroundTintList(getResources().getColorStateList(R.color.accent));
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

                                    if (l.getMap().getId() == currMap.getId()) {
                                        mLocationPointer.setFingerprint(l); // translate UI pointer to new location on screen

                                        // need to refresh map through updateHandler since only UI thread is allowed to touch its views
                                        sUpdateHandler.post(mRefreshMap);
                                    }
                                }

                                @Override
                                public void onFailure(Response<?> response) {
                                    Log.d("MapViewActivity", response.getMessage());

                                    // cos' the wifi failed to calculate current location, ask the user what to do.
                                    //startLocationPickerDialog(); TODO: make many messages, need to disable wifi scan while user get message
                                }
                            }
                    );

                    mScanThreadCount--;
                }
            };
            t.start(); // start new scan thread
        }
    }

    public void startActivateWifiDialog(){
        // Select location manually - create alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Use wifi?");

        // set dialog message
        alertDialogBuilder
                .setMessage("This app wants to change " +
                            "your device wifi settings")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // activate scan again..aka wait for another wifi scan results
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void startLocationPickerDialog(){
        // Select location manually - create alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Current location");

        // set dialog message
        alertDialogBuilder
                .setMessage("Your current location was not found. " +
                        "Scan again, or select your " +
                        "location manually")
                .setCancelable(false)
                .setPositiveButton("Scan again",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // activate scan again..aka wait for another wifi scan results
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Pin your location",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        dialog.cancel();

                        // paused wifi scan, cos' user not interesed it.
                        // mTimer.cancel();
                       // mTimer = null;

                        // choose your location like edit map
                        locationPicker();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void locationPicker(){
        mMap.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Handle touch events here...
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStarted = event.getEventTime(); // calculate tap start
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getEventTime() - mTouchStarted < 150) { // user tapped the screen
                            PointF location = new PointF(event.getX(), event.getY()); // get touch location

                            mLocationPointer.setLocation(location);

                            // add pointer on screen where the user tapped and start wifi scan
                            if(mLocationPointer == null) {
                                mLocationPointer = mMap.createNewWifiPointOnMap(location);
                                mLocationPointer.activate();
                            } else {
                                mMap.setWifiPointViewPosition(mLocationPointer, location);
                            }

                            refreshMap(); // redraw map
                        }
                        break;
                }

                return false;
            }
        });
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

        intent.putExtra(EXTRA_MESSAGE_X_CORD, 175);
        intent.putExtra(EXTRA_MESSAGE_Y_CORD, 196);

        startActivity(intent); // start map edit mode
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
