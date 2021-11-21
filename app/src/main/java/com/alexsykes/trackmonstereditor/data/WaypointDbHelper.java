package com.alexsykes.trackmonstereditor.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class WaypointDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "monster.db";
    private static final int DATABASE_VERSION = 1;

    public WaypointDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void generateRandomData(int itemCount){
        SQLiteDatabase db = this.getWritableDatabase();
        String name;

        double lat = 22;
        double lng = 40;
        double alt;

        for (int track = 1; track < 6; track++) {
            for (int i = 0; i < itemCount; i++) {
                name = "Waypoint " + i;
                lat += (0.5 - Math.random()) / 10;
                lng += (0.5 - Math.random()) / 10;
                alt = Math.random() * 1000;
                String data = name + "::Data - lng: " + lng + " lat: " + lat;
                Log.i("Info", data);

                ContentValues values = new ContentValues();

                values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LAT, lat);
                values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LNG, lng);
                values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_NAME, name);
                values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_TRACKID, track);
                values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_ALT, alt);

                db.insertWithOnConflict("waypoints", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
    }

    public void addLocation(int trackid, double lat, double lng, double speed, double bearing, double alt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LAT, lat);
        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LNG, lng);
        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_SPEED, speed);
        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_BEARING, bearing);
        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_NAME, "");
        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_TRACKID, trackid);
        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_ALT, alt);

        db.insert("waypoints", null, values);
    }

    public void addTrackPoint(int trackid, int segment, double lat, double lng, double alt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LAT, lat);
        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LNG, lng);
        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_SEGMENT, segment);
        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_NAME, "");
        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_TRACKID, trackid);
        values.put(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_ALT, alt);

        db.insert("waypoints", null, values);
    }

    public ArrayList<LatLng> getTrackPoints(int trackID) {
        ArrayList<LatLng> theWaypoints = new ArrayList<LatLng>();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT lat, lng FROM waypoints WHERE trackid = '" + trackID + "' ORDER BY _id ASC";


        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            theWaypoints.add(new LatLng(cursor.getDouble(0), cursor.getDouble(1)));
        }

        cursor.close();
        return theWaypoints;
    }
}