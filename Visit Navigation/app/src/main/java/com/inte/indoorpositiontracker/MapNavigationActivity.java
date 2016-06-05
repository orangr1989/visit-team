package com.inte.indoorpositiontracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import  java.lang.Math;

import DataModel.Cell;
import DataModel.Location;
import DataModel.Map;
import DataModel.PathRequest;
import Handler.Response;
import Home.EntityHomeCallback;
import Home.PathHome;



/**
 * Created by nisan on 20/05/2016.
 */
public class MapNavigationActivity extends MapViewActivity {

    private static final int MENU_ITEM_EXIT = 35;
    private static final int NEXT_MAP = 36;
    private static final int PREVIOUS_MAP = 37;

    // handler for callbacks to the UI thread
    private static Handler sUpdateHandler = new Handler();

    private List<Cell> cells;
    private int currentFloorPath;
    private Map currentMap;
    Timer CheckNavCompleteTimer;
    int distanceThreshold = 100;
    Cell destCell;

    Handler guiMsgHandler;

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
        Location destLocation = mApplication.getLocationById(locationId);

         destCell = new Cell(destLocation.getMap().getMapFloorNumber(),
                             destLocation.getMapXcord(),
                             destLocation.getMapYcord());

        // get source cell
        int mapId = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_MAP, 1);
        currentMap = mApplication.getMapById(mapId);
        int currentXCord = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_X_CORD, 1);
        int currentYCord = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_Y_CORD, 1);
        int currentFloorNumber = currentMap.getMapFloorNumber();
        Cell currentCell = new Cell(currentFloorNumber, currentXCord, currentYCord);

        PathRequest path = new PathRequest(currentCell, destCell, 1);

        super.setMap(currentMap.getMapURL());

        setTitle(getTitle() + " (navigation)");

        PathHome.getPath(path,
                         new EntityHomeCallback() {
                    @Override
                            public void onResponse(Response<?> response) {
                                cells = (List<Cell>) response.getData();
                                setPath(cells, cells.get(0).GetX());
                                String nisan = "test";
                            }

                            @Override
                            public void onFailure(Response<?> response) {
                                Toast.makeText(getApplicationContext(), "Fail to get path" , Toast.LENGTH_LONG).show();
                            }
            });

        TimerNavComplete();
    }

    private void TimerNavComplete() {
        guiMsgHandler = new Handler();
        CheckNavCompleteTimer = new Timer(true);
        CheckNavCompleteTimer.scheduleAtFixedRate(new RemindTask(), 0, 3000);
    }

    class RemindTask extends TimerTask {
        public void run() {
            if (IsUserArriveDestination()) {
                guiMsgHandler.post(new Runnable(){
                    public void run() {
                        Toast.makeText(getApplicationContext(), "You Arrive to Destination!", Toast.LENGTH_LONG).show();
                    }
                });

                CheckNavCompleteTimer.cancel(); //Terminate the timer thread
            }
        }
    }

    public boolean IsUserArriveDestination()
    {
        double distance = GetDistanceBetweenCurrentToDest();
        return distance < distanceThreshold;
    }

    public double GetDistanceBetweenCurrentToDest()
    {
        return Math.hypot(destCell.GetX() - mLocationPointer.getLocation().x ,
                           destCell.GetY() - mLocationPointer.getLocation().y);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_EXIT, Menu.NONE, "Exit navigation mode");
        menu.add(Menu.NONE, NEXT_MAP, Menu.NONE, "Next");
        menu.add(Menu.NONE, PREVIOUS_MAP, Menu.NONE, "Previous");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_EXIT:
                CheckNavCompleteTimer.cancel();
                finish();
                return true;
            case NEXT_MAP: {
                if (IsExistPath(currentFloorPath + 1))
                     setPath(cells, currentFloorPath + 1);
                else
                    Toast.makeText(getApplicationContext(), "There is no path to floor: " + (currentFloorPath + 1), Toast.LENGTH_LONG).show();
                return true;
            }
            case PREVIOUS_MAP: {
                if (IsExistPath(currentFloorPath - 1))
                    setPath(cells, currentFloorPath - 1);
                else
                    Toast.makeText(getApplicationContext(), "There is no path to floor: " + (currentFloorPath - 1), Toast.LENGTH_LONG).show();
                return true;
            }
            default:
                return true;
        }
    }

    private boolean IsExistPath(int floor)
    {
        for (int i = 0; i < cells.size(); i++)
            if (cells.get(i).GetX() == floor)
                return  true;
        return  false;
    }

    public void setPath(List<Cell> cells, int floorToDraw) {
        boolean successLoadMap = true;

        if (currentMap.getMapFloorNumber() != floorToDraw) {
            successLoadMap = false;
            List<Map> maps = mApplication.getMaps();
            for (Map map : maps) {
                if (map.getMapFloorNumber() == floorToDraw) {
                    super.setMap(map.getMapURL());
                    currentMap = mApplication.getMapById(map.getId());
                    successLoadMap = true;
                    break;
                }
            }
            if (successLoadMap)
                mMap.deletePath();
        }

        if (!successLoadMap) {
            Toast.makeText(getApplicationContext(), "Problem load map", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < cells.size(); i++) {
            if (cells.get(i).GetX() == floorToDraw) {
                WifiPointView wifiPoint = mMap.createPath((float) cells.get(i).GetY(), (float) cells.get(i).GetZ());
                if (i == cells.size() - 1) {
                    wifiPoint.pointType = WifiPointView.POINT_TYPE.destPoint;
                    wifiPoint.destName = "some";
                }
            }
        }

        currentFloorPath  = floorToDraw;

        // need to refresh map through updateHandler since only UI thread is allowed to touch its views
        sUpdateHandler.post(mRefreshMap);
    }
}
