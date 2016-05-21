package com.inte.indoorpositiontracker;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.util.Log;

import DataModel.Fingerprint;
import DataModel.Location;
import DataModel.Map;
import Handler.Response;
import Home.EntityHomeCallback;
import Home.FingerprintHome;
import Home.MapHome;
import db.LocationDatabaseHandler;
import db.MapDatabaseHandler;

public class IndoorPositionTracker extends Application {
    private MapDatabaseHandler mMapDatabaseHandler;
    private LocationDatabaseHandler mLocationDatabaseHandler;

    private ArrayList<Fingerprint> mFingerprints;

    /** INSTANCE METHODS */
    
    @Override
    public void onCreate() {
        super.onCreate();
        mFingerprints = new ArrayList<Fingerprint>();
        FingerprintHome.getFingerprintList(
                new EntityHomeCallback() {
                    @Override
                    public void onResponse(Response<?> response) {
                        List<Fingerprint> fPrints = (List<Fingerprint>)response.getData();
                        for (Fingerprint currPrint : fPrints)
                        {
                            mFingerprints.add(currPrint);
                        }
                    }

                    @Override
                    public void onFailure(Response<?> response) {
                        Log.d("GetFingerPrintList", response.getMessage());
                    }
                }
        );

        mMapDatabaseHandler = new MapDatabaseHandler(this);
        mLocationDatabaseHandler = new LocationDatabaseHandler(this);
    }

    public ArrayList<Fingerprint> getFingerprintData() {
        return mFingerprints;
    }

    public ArrayList<Fingerprint> getFingerprintData(String map) {
        ArrayList<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
        for(Fingerprint fingerprint : mFingerprints) {
            if(fingerprint.getLocation().getMap().getMapName().compareTo(map) == 0) {
                fingerprints.add(fingerprint);
            }
        }
        
        return fingerprints;
    }
    
    public void addFingerprint(Fingerprint fingerprint) {
        FingerprintHome.setFingerprint(fingerprint); // add to server
        mFingerprints.add(fingerprint); // add to fingerprint arraylist
    }
    
    public void deleteAllFingerprints() {
        mFingerprints.clear(); // delete all fingerprints from arraylist
        FingerprintHome.deleteFingerprints(); // delete from server
    }
    
    public void deleteAllFingerprints(String map) {
        ArrayList<Fingerprint> itemsToRemove = new ArrayList<Fingerprint>();
        
        // collect fingerprints that need to be deleted
        for(Fingerprint fingerprint : mFingerprints) {
            if(fingerprint.getLocation().getMap().getMapName().compareTo(map) == 0) {
                FingerprintHome.deleteFingerprint(fingerprint);
                itemsToRemove.add(fingerprint);
            }
        }

        for (Fingerprint fingerprint : itemsToRemove)
        {
            mFingerprints.remove(fingerprint);
        }
    }

    public ArrayList<Map> getMaps () {
        return mMapDatabaseHandler.getAllMaps();
    }

    public void addMap(Map map) {
        mMapDatabaseHandler.addMap(map);
    }

    public void deleteAllMaps() {
        mMapDatabaseHandler.deleteAllMaps();
    }

    public Map getMapById(int id) {
        return mMapDatabaseHandler.getMap(id);
    }

    public ArrayList<Location> getLocations () {
        return mLocationDatabaseHandler.getAllLocations();
    }

    public void addLocation(Location location) {
        mLocationDatabaseHandler.addLocation(location);
    }

    public void deleteAllLocations() {
        mLocationDatabaseHandler.deleteAllLocations();
    }

    public Location getLocationById(int id) {
        return mLocationDatabaseHandler.getLocation(id);
    }
}
