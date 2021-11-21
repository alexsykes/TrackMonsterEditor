package com.alexsykes.trackmonstereditor.data;


import com.google.android.gms.maps.model.Marker;

public class Waypoint {

    int _id;
    int trackid;
    int segment;
    String name;
    String created;
    String updated;
    float lat;
    float lng;
    float alt;
    Marker marker;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
