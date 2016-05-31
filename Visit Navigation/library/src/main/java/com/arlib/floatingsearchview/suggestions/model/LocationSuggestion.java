package com.arlib.floatingsearchview.suggestions.model;

import android.os.Parcel;

/**
 * Copyright (c) 2015 Ari C
 * <p/>
 * ColorSuggestion ---
 * <p/>
 * Author Ari
 * Created on 10/19/2015.
 */
public  class LocationSuggestion implements SearchSuggestion {

    private LocationWrapper mLocation;

    private String mLocationName;

    public LocationSuggestion(LocationWrapper location){

        this.mLocation = location;
        this.mLocationName = location.getID();
    }

    public LocationSuggestion(Parcel source) {
        this.mLocationName = source.readString();
    }

    public LocationWrapper getLocation(){
        return mLocation;
    }

    @Override
    public String getBody() {
        return mLocation.getID();
    }

    @Override
    public Creator getCreator() {
        return CREATOR;
    }

    public String getTitle() {
        return this.mLocationName;
    }

    ///////

    public static final Creator<LocationSuggestion> CREATOR = new Creator<LocationSuggestion>() {
        @Override
        public LocationSuggestion createFromParcel(Parcel in) {
            return new LocationSuggestion(in);
        }

        @Override
        public LocationSuggestion[] newArray(int size) {
            return new LocationSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLocationName);
    }
}