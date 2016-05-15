package com.inte.indoorpositiontracker;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.util.Log;

import DataModel.Fingerprint;
import DataModel.Map;
import Handler.Response;
import Home.EntityHomeCallback;
import Home.FingerprintHome;
import Home.MapHome;

public class IndoorPositionTracker extends Application {
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

                        Log.d("GetFingerPrintList success", response.getMessage());
                    }

                    @Override
                    public void onFailure(Response<?> response) {
                        Log.d("GetFingerPrintList", response.getMessage());
                    }
                }
        );
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
                mFingerprints.remove(fingerprint);
            }
        }
    }
}
