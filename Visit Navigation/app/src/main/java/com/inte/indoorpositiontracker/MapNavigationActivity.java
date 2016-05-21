package com.inte.indoorpositiontracker;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import DataModel.Cell;
import DataModel.Fingerprint;
import DataModel.Location;
import DataModel.Map;
import DataModel.Measurement;
import DataModel.PathRequest;
import DataModel.measure.Wifi;
import Handler.Response;
import Home.EntityHomeCallback;
import Home.LocationHome;
import Home.PathHome;

/**
 * Created by nisan on 20/05/2016.
 */
public class MapNavigationActivity extends MapViewActivity {

    private static final int MENU_ITEM_EXIT = 35;
    private static final int NEXT_MAP = 36;

    // handler for callbacks to the UI thread
    private static Handler sUpdateHandler = new Handler();

    private List<Cell> cells;
    private int cellsOver;

    // runnable to refresh map (called by the handler)
    private Runnable mRefreshMap = new Runnable() {
        public void run() {
            refreshMap();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        // get destination cell
        int locationId = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_LOCATION_DEST, 1);
        Location location = mApplication.getLocationById(locationId);
        Cell cellDest = new Cell(location.getMap().getMapFloorNumber(), location.getMapXcord(),
                location.getMapYcord());

        // get source cell
        int mapId = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_MAP, 1);
        Map map = mApplication.getMapById(mapId);
        int xCord = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_X_CORD, 1);
        int yCord = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_Y_CORD, 1);
        Cell cellSrc = new Cell(map.getMapFloorNumber(), xCord, yCord);

        PathRequest path = new PathRequest(cellSrc, cellDest, 1);

        super.setMap(map.getMapURL());

        setTitle(getTitle() + " (navigation)");

        PathHome.getPath(path,
                new EntityHomeCallback() {
                    @Override
                    public void onResponse(Response<?> response) {
                        cells = (List<Cell>) response.getData();
                        setPath(cells);
                        String nisan = "test";
                    }

                    @Override
                    public void onFailure(Response<?> response) {
                        String nisan = "test";
                    }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_EXIT, Menu.NONE, "Exit navigation mode");
        menu.add(Menu.NONE, NEXT_MAP, Menu.NONE, "Next");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_EXIT:
                finish();
                return true;
            default:
                setNextPath(cells);
                return true;
        }
    }

    public void setPath(List<Cell> cells) {
        int floor = cells.get(0).GetX();
        cellsOver = 0;

        for (int i = 0; i < cells.size(); i++) {
            if (cells.get(i).GetX() == floor) {
                mMap.createPath((float) cells.get(i).GetY(), (float) cells.get(i).GetZ());
                cells.remove(cells.get(i));
                cellsOver++;
            }
        }

        // need to refresh map through updateHandler since only UI thread is allowed to touch its views
        sUpdateHandler.post(mRefreshMap);
    }

    public void setNextPath(List<Cell> cells) {
        if (cells != null && cells.size() > cellsOver) {
            int floor = cells.get(cellsOver).GetX();
            List<Map> maps = mApplication.getMaps();
            for (Map map : maps) {
                if (map.getMapFloorNumber() == floor) {
                    super.setMap(map.getMapURL());
                    break;
                }
            }


            mMap.deletePath();
            for (int i = 0; i < cells.size(); i++) {
                if (cells.get(i).GetX() == floor) {
                    mMap.createPath((float) cells.get(i).GetY(), (float) cells.get(i).GetZ());
                    cells.remove(cells.get(i));
                    cellsOver++;
                }
            }

            // need to refresh map through updateHandler since only UI thread is allowed to touch its views
            sUpdateHandler.post(mRefreshMap);
        }
    }
}
