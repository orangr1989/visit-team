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

    private String mLocationSymbolicId;

    private Integer mLocationId;

    public LocationSuggestion(LocationWrapper location){

        this.mLocation = location;
        this.mLocationSymbolicId = location.getsymbolicID();
        this.mLocationId = location.getID();
    }

    public LocationSuggestion(Parcel source) {
        this.mLocationSymbolicId = source.readString();
    }

    public LocationWrapper getLocation(){
        return mLocation;
    }

    @Override
    public String getBody() {
        return mLocation.getsymbolicID();
    }

    @Override
    public Creator getCreator() {
        return CREATOR;
    }

    public Integer getId() {
        return this.mLocationId;
    }

    public String getSymbolicId() {
        return this.mLocationSymbolicId;
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
        dest.writeString(mLocationSymbolicId);
    }
}