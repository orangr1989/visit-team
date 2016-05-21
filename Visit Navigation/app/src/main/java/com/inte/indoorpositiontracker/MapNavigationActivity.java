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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        // get destination cell
        int locationId = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_LOCATION_DEST, 1);
        Location location = mApplication.getLocationById(locationId);
        Cell cellDest = new Cell(location.getMap().getMapFloorNumber() + 1, location.getMapXcord(),
                location.getMapYcord());

        // get source cell
        int mapId = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_MAP, 1);
        Map map = mApplication.getMapById(mapId);
        int xCord = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_X_CORD, 1);
        int yCord = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_Y_CORD, 1);
        Cell cellSrc = new Cell(map.getMapFloorNumber() + 1, xCord, yCord);

        PathRequest path = new PathRequest(cellSrc, cellDest, 1);

        super.setMap(map.getMapURL());

        setTitle(getTitle() + " (navigation)");

        PathHome.getPath(path,
                new EntityHomeCallback() {
                    @Override
                    public void onResponse(Response<?> response) {
                        List<Cell> cells = (List<Cell>) response.getData();
                        setPath(cells);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void setPath(List<Cell> cells) {
        for (Cell c : cells) {
            mMap.createPath((float) c.GetY(), (float) c.GetZ());
        }
    }
}
