package com.inte.indoorpositiontracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.List;

import DataModel.Map;
import Handler.DownloadImageTask;

public class MapActivity extends Activity implements OnTouchListener {
    private static final int MENU_ITEM_CHOOSE_FLOOR = 0;
    private static final int MENU_ITEM_BASEMENT = 2;
    private static final int MENU_ITEM_1STFLOOR = 3;
    private static final int MENU_ITEM_2NDFLOOR = 4;
    private static final int MENU_ITEM_3RDFLOOR = 5;
    private static final int MENU_ITEM_4THFLOOR = 6;


    protected WifiManager mWifi;
    protected MapView mMap; // map object
    protected BroadcastReceiver mReceiver; // for receiving wifi scan results
    protected IndoorPositionTracker mApplication;

    protected String mSelectedMap; // id of the map which is currently being displayed

    // TODO: get dynamic map name
    protected Map currMap = new Map();
    //protected List<Map> maps;

    /** INSTANCE METHODS */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMap = (MapView) findViewById(R.id.mapView);
        mMap.setOnTouchListener(this);

        startService(new Intent(MapActivity.this,
                SynchronizationManager.class));

        mApplication = (IndoorPositionTracker) getApplication();
        mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        //addMap();
        //mApplication.deleteAllMaps();
        List<Map> maps = mApplication.getMaps();
        if (maps.size() > 0) {
            currMap = maps.get(3);
            this.setMap(currMap.getMapURL());
        }
    }

    public void onStart() {
        super.onStart();

        mReceiver = new BroadcastReceiver ()
        {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                onReceiveWifiScanResults(mWifi.getScanResults());

            }
        };

        registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    protected void addMap()
    {
        Map m = new Map();
        m.setId(2);
        m.setMapFloorNumber(1);
        m.setMapName("home");
        m.setMapURL("http://10.0.0.11/1.jpg");

        //maps.add(m);
        mApplication.addMap(m);
    }

    public void onReceiveWifiScanResults(List<ScanResult> results) {

    }

    public boolean onTouch(View v, MotionEvent event) {
    	v.onTouchEvent(event);

        return true; // indicate event was handled
    }

    @Override
    protected void onStop()
    {
        unregisterReceiver(mReceiver);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add floor items
        List<Map> maps = mApplication.getMaps();

    	super.onCreateOptionsMenu(menu);
    	SubMenu subFloor = menu.addSubMenu(Menu.NONE, MENU_ITEM_CHOOSE_FLOOR, Menu.NONE, "Choose floor");

        for (Map m : maps) {
            subFloor.add(Menu.NONE, m.getId(), Menu.NONE, m.getMapName());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Map option = mApplication.getMapById(item.getItemId());
        if (option != null) {
            currMap = option;
            setMap(currMap.getMapURL());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshMap() {
        mMap.invalidate(); // redraws the map screen
    }

    public void setMap(int resId) {
        mSelectedMap = String.valueOf(resId);
        mMap.setImageResource(resId); // change map image
    }

    public void setMap(String url) {
        DownloadImageTask task = new DownloadImageTask(this, new DownloadImageTask.DownloadImageTaskCallback() {
            @Override
            public void onImageDownloaded(String url, String path) {
                Bitmap bm = BitmapFactory.decodeFile(path);
                mMap.setImageBitmap(bm);
            }

            @Override
            public void onImageDownloadFailure(String url) {

            }
        });
        task.execute(url);
    }
}

