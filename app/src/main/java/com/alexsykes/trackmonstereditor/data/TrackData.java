package com.alexsykes.trackmonstereditor.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

public class TrackData {
    private final int _id;
    private final ArrayList<LatLng> latLngs;
    private final String name;
    private final String description;
    private final double north;
    private final double south;
    private final double east;
    private final double west;
    private final LatLngBounds latLngBounds;
    private final boolean isVisible;
    private final boolean isCurrent;
    private final String style;

    public TrackData(int _id, ArrayList<LatLng> latLngs, String name, String description, double north, double south, double east, double west, LatLngBounds latLngBounds, boolean isVisible, boolean isCurrent, String style) {
        this._id = _id;
        this.latLngs = latLngs;
        this.name = name;
        this.description = description;
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.latLngBounds = latLngBounds;
        this.isVisible = isVisible;
        this.isCurrent = isCurrent;
        this.style = style;
    }


    public String getStyle() {
        return style;
    }

    public int get_id() {
        return _id;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isCurrent() {
        return isCurrent;
    }


    public ArrayList<LatLng> getLatLngs() {
        return latLngs;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getNorth() {
        return north;
    }

    public double getSouth() {
        return south;
    }

    public double getEast() {
        return east;
    }

    public double getWest() {
        return west;
    }

    public LatLngBounds getLatLngBounds() {
        return latLngBounds;
    }
}
