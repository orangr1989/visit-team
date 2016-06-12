package com.inte.indoorpositiontracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private static final int MINIMAL_DISTANCE = 100;
    private static final int NAXIMAL_DISTANCE = 250;
    private static final int INDICATE_PATH_INTERVAL = 1000;

    // handler for callbacks to the UI thread
    private static Handler sUpdateHandler = new Handler();
    private final Context navContext = this;

    private List<Cell> cells;
    private int currentFloorPath;
    private Map currentMap;
    Timer CheckNavCompleteTimer;
    private Timer mPathIndicateTimer;
    int distanceThreshold = 100;
    Cell destCell;
    Location destLocation;
    int actualFloorNum;
    boolean isPathAccepted = false;
    boolean btnLocClicked = false;
    Cell currSourceCell;

    Handler guiMsgHandler;

    FloatingActionButton up;
    FloatingActionButton down;
    FloatingActionButton mLocationbtn;

    // runnable to refresh map (called by the handler)
    private Runnable mRefreshMap = new Runnable() {
        public void run() {
            refreshMap();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        up = (FloatingActionButton) findViewById(R.id.upButton);
        down = (FloatingActionButton) findViewById(R.id.downButton);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (cells == null || cells.size() ==0) return;
                TryGoUp();
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (cells == null || cells.size() ==0) return;
                TryGoDown();
            }
        });

        // uvisible search view + floating action button
        FloatingSearchView bar = (FloatingSearchView)findViewById(R.id.floating_search_view);
        bar.setVisibility(View.GONE);

        mLocationbtn = (FloatingActionButton) findViewById(R.id.myLocationButton);

        // hide current wifi fingerprint
        mLocationPointer.setVisible(false);

        Intent intent = getIntent();
        // get destination cell
        int locationId = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_LOCATION_DEST, 1);
        destLocation = mApplication.getLocationById(locationId);

        if (destLocation != null) {
            destCell = new Cell(destLocation.getMap().getMapFloorNumber(),
                    destLocation.getMapXcord(),
                    destLocation.getMapYcord());
        }

        // get source cell
        int mapId = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_MAP, 1);
        currentMap = mApplication.getMapById(mapId);
        actualFloorNum = GetActualFloorNum(currentMap.getMapName());
        int currentXCord = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_X_CORD, 1);
        int currentYCord = intent.getIntExtra(MapViewActivity.EXTRA_MESSAGE_Y_CORD, 1);
        currSourceCell = new Cell(currentMap.getMapFloorNumber(), currentXCord, currentYCord);

        super.setMap(currentMap.getMapURL());

        setTitle(getTitle() + " (navigation)");

        // Request path from server
        PathRequest path = new PathRequest(currSourceCell, destCell, 1);
        getPathFromServer(path);

        mPathIndicateTimer = new Timer();
        mPathIndicateTimer.schedule(new TimerTask() {

            private Handler updateUI = new Handler() {

              @Override
              public void dispatchMessage(Message msg){
                  if (isPathAccepted && mLocationPointer.getLocation().x > -1000) {
                      // check the closed path's point.
                      int mCloseCellId = getCloseCell(mLocationPointer.getLocation());

                      // check if lower than max distance (100)
                      if (calcDistanceFromPath(mLocationPointer.getLocation(),
                              cells.get(mCloseCellId).GetY(),
                              cells.get(mCloseCellId).GetZ()) < MINIMAL_DISTANCE) {

                          completePathPointsToCell(mCloseCellId);
                      }
                      else if (calcDistanceFromPath(mLocationPointer.getLocation(),
                              cells.get(mCloseCellId).GetY(),
                              cells.get(mCloseCellId).GetZ()) > NAXIMAL_DISTANCE){

                              Cell currentCell = new Cell(currentMap.getMapFloorNumber(),
                                      (int)mLocationPointer.getLocation().x,
                                      (int)mLocationPointer.getLocation().y);

                              if (!currSourceCell.IsEqual(currentCell)) {

                                  // set new current source
                                  currSourceCell = currentCell;

                                  // show green point (current location)
                                  changeMyLocationVisibilty(true);

                                  // delete old path
                                  mMap.deletePath();

                                  // request new Path from server..
                                  PathRequest path = new PathRequest(currentCell, destCell, 1);

                                  getPathFromServer(path);
                              }
                      }
                  }
              }
            };
                @Override
                public void run() {
                    updateUI.sendEmptyMessage(0);
                }
        }, 0, INDICATE_PATH_INTERVAL);

        mLocationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!btnLocClicked) {
                    Toast.makeText(getApplicationContext(), "Show current location",
                            Toast.LENGTH_SHORT).show();

                    changeMyLocationVisibilty(true);
                } else {

                    Toast.makeText(getApplicationContext(), "Hide current location",
                            Toast.LENGTH_SHORT).show();

                    changeMyLocationVisibilty(false);
                }
            }
        });
    }

    private void getPathFromServer(PathRequest path) {

        PathHome.getPath(path,
                new EntityHomeCallback() {
                    @Override
                    public void onResponse(Response<?> response) {
                        cells = (List<Cell>) response.getData();
                        setPath(cells, cells.get(0).GetX());
                        if (!isPathAccepted) isPathAccepted = true;
                        else {
                            Toast.makeText(getApplicationContext(),
                                    "Your path has been updated..",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Response<?> response) {
                        Toast.makeText(getApplicationContext(),
                                "Fail to get path",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void changeMyLocationVisibilty(boolean visibility) {

        // show/ hide green point
        mLocationPointer.setVisible(visibility);

        if (visibility){
            // the myLocation option changed to visible
            mLocationbtn.setBackgroundTintList(getResources().getColorStateList(R.color.green));
            btnLocClicked = visibility;
        }
        else
        {
            // the myLocation option changed to hide
            mLocationbtn.setBackgroundTintList(getResources().getColorStateList(R.color.accent));
            btnLocClicked = false;
        }
    }

    private void completePathPointsToCell(int cellId) {

        for (int i = 0; i <= cellId; i++) {
            // update the completed cells
            if (cells.get(i).GetX() == currentFloorPath) {
                WifiPointView wifiPoint = mMap.updatePath((float) cells.get(i).GetY(),
                        (float) cells.get(i).GetZ(), i);
            }
        }

        if (cellId == cells.size() - 1)
        {
            showFinishPathAlert();
            isPathAccepted = false;

            /*Toast.makeText(getApplicationContext(), "You arrived your destination!",
                    Toast.LENGTH_SHORT).show();*/
        }
    }

    private void showFinishPathAlert() {
        // Select location manually - create alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Finish navigation");

        // set dialog message
        alertDialogBuilder
                .setMessage("You arrived your destination!")
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onBackPressed();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private int getCloseCell(PointF location) {

        int cellId = 0;
        double minDistance = MINIMAL_DISTANCE;
        double currentCellDistance = 0;

        for (int i = 0; i < cells.size(); i++)
        {
            currentCellDistance = calcDistanceFromPath(location,
                                                       cells.get(i).GetY(),
                                                       cells.get(i).GetZ());

            if (minDistance >= currentCellDistance){
                minDistance = currentCellDistance;
                cellId = i;
            }
        }
        return cellId;
    }

    private double calcDistanceFromPath(PointF currentLocation, int currCellX, int currCellY) {

        ///////// d=√((x1-x2)^2+(y1-y2)^2)
        return Math.sqrt(Math.pow((currCellX - currentLocation.x), 2) +
                         Math.pow((currCellY - currentLocation.y), 2));
    }

    private  void TryGoDown()
    {
        if (IsExistPath(currentFloorPath - 1))
            setPath(cells, currentFloorPath - 1);
        else
            Toast.makeText(getApplicationContext(), "There is no path to floor: " + (actualFloorNum - 1), Toast.LENGTH_SHORT).show();
    }

    private  void TryGoUp()
    {
        if (IsExistPath(currentFloorPath + 1))
            setPath(cells, currentFloorPath + 1);
        else
            Toast.makeText(getApplicationContext(), "There is no path to floor: " + (actualFloorNum + 1), Toast.LENGTH_SHORT).show();
    }

    public double GetDistanceBetweenCurrentToDest()
    {
        if (destCell != null) {
            return Math.hypot(destCell.GetX() - mLocationPointer.getLocation().x,
                    destCell.GetY() - mLocationPointer.getLocation().y);
        }

        return Double.MAX_VALUE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add(Menu.NONE, MENU_ITEM_EXIT, Menu.NONE, "Exit navigation mode");
        //menu.add(Menu.NONE, NEXT_MAP, Menu.NONE, "Next");
        //menu.add(Menu.NONE, PREVIOUS_MAP, Menu.NONE, "Previous");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                onBackPressed();
                return true;
            default:
                return true;
        }
    }

    private  int GetActualFloorNum(String floorPattern) //Expected to get pattern: "floor {num}"
    {
        String[] split = floorPattern.split("\\s+");
        return Integer.parseInt(split[1]);
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
                    actualFloorNum = GetActualFloorNum(currentMap.getMapName());
                    successLoadMap = true;
                    break;
                }
            }
            if (successLoadMap)
                mMap.deletePath();
        }

        if (!successLoadMap) {
            Toast.makeText(getApplicationContext(), "Problem to load map", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < cells.size(); i++) {
            Cell currentCell = cells.get(i);
            if (currentCell.GetX() == floorToDraw) {
                WifiPointView wifiPoint = mMap.createPath((float) currentCell.GetY(), (float) currentCell.GetZ());
                if (i == cells.size() - 1) {
                    wifiPoint.pointType = WifiPointView.POINT_TYPE.destPoint;
                    wifiPoint.destName = destLocation.getSymbolicID();
                    break;
                }

                if (IsMoveBetweenFloor(currentCell.GetX() , cells.get(i + 1).GetX())) {
                    up.setVisibility(View.VISIBLE);
                    down.setVisibility(View.VISIBLE);
                    if (currentCell.GetX() < cells.get(i + 1).GetX())
                        wifiPoint.info = "Go up to floor: " + (actualFloorNum + 1);
                    else
                        wifiPoint.info = "Go down to floor: " + (actualFloorNum - 1);
                }
            }
        }

        currentFloorPath  = floorToDraw;

        // need to refresh map through updateHandler since only UI thread is allowed to touch its views
        sUpdateHandler.post(mRefreshMap);
    }

    private boolean IsMoveBetweenFloor(int currentFloorCell, int nextFloorCell)
    {
        return currentFloorCell != nextFloorCell;
    }
}
