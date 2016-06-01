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

    @SerializedName("id")
    @Expose
    private Integer id;
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


    public Integer getID() {
        return this.id;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public String getsymbolicID() {
        return symbolicID;
    }

    public void setsymbolicID(String symbolic) {
        this.symbolicID = symbolic;
    }

    public int getx() {
        return mapXcord;
    }

    public void setx(int xCord) {
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