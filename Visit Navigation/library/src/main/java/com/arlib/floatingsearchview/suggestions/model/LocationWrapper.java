package com.arlib.floatingsearchview.suggestions.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Copyright (c) 2015 Ari C
 * <p/>
 * Color ---
 * <p/>
 * Author Ari
 * Created on 10/19/2015.
 */

public class LocationWrapper {

    @SerializedName("symbolicID")
    @Expose
    private String symbolicID;
    @SerializedName("mapXcord")
    @Expose
    private int mapXcord;
    @SerializedName("mapYcord")
    @Expose
    private int mapYcord;
    @SerializedName("map")
    @Expose
    private Map map;
    @SerializedName("accuracy")
    @Expose
    private int accuracy;


    public String getTitle() {
        return symbolicID;
    }

    public void setTitle(String title) {
        this.symbolicID = title;
    }

    public int getx() {
        return mapXcord;
    }

    public void set(int xCord) {
        this.mapXcord = xCord;
    }

    public int gety() {
        return mapYcord;
    }

    public void sety(int yCord) {
        this.mapXcord = yCord;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public int getAccuracy() {
        return this.accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }




}