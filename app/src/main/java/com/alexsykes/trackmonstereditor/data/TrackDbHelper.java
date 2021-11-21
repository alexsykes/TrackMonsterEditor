package com.alexsykes.trackmonstereditor.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TrackDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "monster.db";
    private static final int DATABASE_VERSION = 1;
    private int trackid;

    public TrackDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @SuppressLint("Range")
    public TrackData getTrackData(int trackid){
        // Declare fields
        String description = "";
        String name = "";
        String style = "Road";
        boolean isVisible = true;
        boolean isCurrent = true;

        // Get database and return data
        SQLiteDatabase db = this.getWritableDatabase();

        // Get track data
        String trackQuery = "SELECT * FROM tracks WHERE _id = " + trackid ;
        Cursor cursor = db.rawQuery(trackQuery, null);
        int count = cursor.getCount();
        if(count>0) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_NAME));
            description = cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_DESCRIPTION));
            isVisible = cursor.getInt(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_ISVISIBLE)) > 0;
            isCurrent = cursor.getInt(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_ISCURRENT)) > 0;
            style = cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_STYLE));
        }
        cursor.close();

        // Get waypoint data
        String waypointQuery = "SELECT * FROM waypoints WHERE trackid = " + trackid + " ORDER BY _id ASC";
        cursor = db.rawQuery(waypointQuery, null);

        // Check for valid data
        count = cursor.getCount();
        double westmost = 0;
        double eastmost = 0;
        double southmost = 0;
        double northmost = 0;
        LatLngBounds latLngBounds;
        ArrayList<LatLng> latLngs = new ArrayList<>();
        if(count > 0){
            cursor.moveToFirst();
            // Get initial values
            westmost = cursor.getDouble(cursor.getColumnIndex(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LNG));
            eastmost = cursor.getDouble(cursor.getColumnIndex(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LNG));
            northmost = cursor.getDouble(cursor.getColumnIndex(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LAT));
            southmost = cursor.getDouble(cursor.getColumnIndex(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LAT));

            latLngs.add((new LatLng(northmost,westmost)));

            while (cursor.moveToNext()) {
                // Compare map extremes
                double lng = cursor.getDouble(cursor.getColumnIndex(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LNG));
                double lat = cursor.getDouble(cursor.getColumnIndex(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LAT));

                // Add LatLng to Arraylist
                latLngs.add(new LatLng(lat, lng));

                if (lat > northmost) {
                    northmost = lat;
                }
                if (lat < southmost) {
                    southmost = lat;
                }
                if (lng > eastmost) {
                    eastmost = lng;
                }
                if (lng < westmost) {
                    westmost = lng;
                }
            }
        }

        LatLng northeast = new LatLng(northmost, eastmost);
        LatLng southwest = new LatLng(southmost, westmost);
        latLngBounds = new LatLngBounds(southwest, northeast);
        cursor.close();
        return new TrackData(trackid, latLngs, name, description,
                northmost, southmost, eastmost, westmost, latLngBounds, isVisible, isCurrent, style);
    }

    public int getCurrentTrackID() {
        int trackid;
        // Get database and return data
        SQLiteDatabase db = this.getWritableDatabase();

        // Get track data
        String trackQuery = "SELECT _id FROM tracks WHERE iscurrent = 1";
        Cursor cursor = db.rawQuery(trackQuery, null);

        if (cursor.getCount() == 0) {
            trackid = 0;
        } else {
            cursor.moveToFirst();
            trackid = cursor.getInt(0);
        }
        cursor.close();
        return trackid;
    }

    @SuppressLint("Range")
    public TrackData getCurrentTrackData() {
        // Declare fields

        // Get database and return data
        SQLiteDatabase db = this.getWritableDatabase();

        // Get track data
        String trackQuery = "SELECT * FROM tracks WHERE iscurrent = 1";
        Cursor cursor = db.rawQuery(trackQuery, null);
        int count = cursor.getCount();

        int trackid = -999;
        String description = "";
        String name = "";
        String style = "Road";
        boolean isVisible = true;
        boolean isCurrent = true;
        if (count > 0) {
            cursor.moveToFirst();
            trackid = cursor.getInt(cursor.getColumnIndex(TrackContract.TrackEntry._ID));
            name = cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_NAME));
            description = cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_DESCRIPTION));
            isVisible = cursor.getInt(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_ISVISIBLE)) > 0;
            isCurrent = cursor.getInt(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_ISCURRENT)) > 0;
            style = cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_STYLE));
        }

        cursor.close();
        // Get waypoint data
        String waypointQuery = "SELECT * FROM waypoints WHERE trackid = " + trackid + " ORDER BY _id ASC";
        cursor = db.rawQuery(waypointQuery, null);

        // Check for valid data
        count = cursor.getCount();
        double westmost = 0;
        double eastmost = 0;
        double southmost = 0;
        double northmost = 0;
        LatLngBounds latLngBounds;
        ArrayList<LatLng> latLngs = new ArrayList<>();
        if (count > 0) {
            cursor.moveToFirst();
            // Get initial values
            westmost = cursor.getDouble(cursor.getColumnIndex(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LNG));
            eastmost = cursor.getDouble(cursor.getColumnIndex(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LNG));
            northmost = cursor.getDouble(cursor.getColumnIndex(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LAT));
            southmost = cursor.getDouble(cursor.getColumnIndex(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LAT));

            latLngs.add((new LatLng(northmost, westmost)));

            while (cursor.moveToNext()) {
                // Compare map extremes
                double lng = cursor.getDouble(cursor.getColumnIndex(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LNG));
                double lat = cursor.getDouble(cursor.getColumnIndex(WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LAT));

                // Add LatLng to Arraylist
                latLngs.add(new LatLng(lat, lng));

                if (lat > northmost) {
                    northmost = lat;
                }
                if (lat < southmost) {
                    southmost = lat;
                }
                if (lng > eastmost) {
                    eastmost = lng;
                }
                if (lng < westmost) {
                    westmost = lng;
                }
            }
            // cursor.close();
        }
        cursor.close();
        LatLng northeast = new LatLng(northmost, eastmost);
        LatLng southwest = new LatLng(southmost, westmost);
        latLngBounds = new LatLngBounds(southwest, northeast);

        int _id = trackid;
        return new TrackData(_id, latLngs, name, description, northmost, southmost, eastmost, westmost, latLngBounds, isVisible, isCurrent, style);
    }

    public TrackData[] getAllTrackData() {
        TrackData[] trackDataArray;
        ArrayList<HashMap<String, String>> trackList;

        trackList = getShortTrackList();
        trackDataArray = new TrackData[trackList.size()];

        for (int i = 0; i < trackList.size(); i++) {
            int trackid = Integer.parseInt(trackList.get(i).get("id"));
            trackDataArray[i] = getTrackData(trackid);
        }
        return trackDataArray;
    }


    public TrackData[] getAllVisibleTrackData() {
        TrackData[] trackDataArray;
        ArrayList<HashMap<String, String>> trackList;

        trackList = getShortVisibleTrackList();
        trackDataArray = new TrackData[trackList.size()];
        // Initialise database - get count of tracks
        // SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < trackList.size(); i++) {
            int trackid = Integer.parseInt(trackList.get(i).get("id"));
            trackDataArray[i] = getTrackData(trackid);
        }
        return trackDataArray;
    }

    @SuppressLint("Range")
    public ArrayList<HashMap<String, String>> getShortTrackList() {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT _id , name, isCurrent, isVisible FROM tracks  ORDER BY _id ASC";

        // query = "SELECT * FROM tracks";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<HashMap<String, String>> theTrackList = new ArrayList<>();
        while (cursor.moveToNext()) {
            HashMap<String, String> tracks = new HashMap<>();
            tracks.put("id", cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry._ID)));
            tracks.put("name", cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_NAME)));
            tracks.put("isVisible", cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_ISVISIBLE)));
            tracks.put("isCurrent", cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_ISCURRENT)));
            theTrackList.add(tracks);
        }
        cursor.close();
        return theTrackList;
    }

    @SuppressLint("Range")
    public ArrayList<HashMap<String, String>> getShortVisibleTrackList() {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT _id , name, isVisible FROM tracks WHERE isVisible = '1' ORDER BY _id ASC";

        // query = "SELECT * FROM tracks";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<HashMap<String, String>> theTrackList = new ArrayList<>();
        while (cursor.moveToNext()) {
            HashMap<String, String> tracks = new HashMap<>();
            tracks.put("id", cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry._ID)));
            tracks.put("name", cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_NAME)));
            tracks.put("isVisible", cursor.getString(cursor.getColumnIndex(TrackContract.TrackEntry.COLUMN_TRACKS_ISVISIBLE)));
            theTrackList.add(tracks);
        }
        cursor.close();
        return theTrackList;
    }

    public int insertNewTrack(boolean isCurrent, String name, String trackDescription, boolean isVisible, String style) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (isCurrent) {
            String setNoneCurrent = "UPDATE tracks SET iscurrent = 0";
            db.execSQL(setNoneCurrent);
        }
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_NAME, name);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_DESCRIPTION, trackDescription);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_ISVISIBLE, 0);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_ISCURRENT, 0);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_STYLE, style);

        db.insert("tracks", null, values);
        String sql = "SELECT last_insert_rowid()";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int trackid = cursor.getInt(0);
        cursor.close();
        return trackid;
    }

    public int insertTrackFromGPX(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Add new track
        ContentValues values = new ContentValues();
        int trackid;

        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_NAME, name);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_ISVISIBLE, 1);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_ISCURRENT, 0);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_STYLE, "Track");

        db.insert("tracks", null, values);

        String sql = "SELECT last_insert_rowid()";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        trackid = cursor.getInt(0);
        cursor.close();
        return trackid;
    }

    public int insertNewTrack() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Clear current track
        String setNoneCurrent = "UPDATE tracks SET iscurrent = 0";
        db.execSQL(setNoneCurrent);

        // Add new track
        ContentValues values = new ContentValues();
        int trackid;

        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_NAME, "New track");
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_ISVISIBLE, true);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_ISCURRENT, true);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_STYLE, "Track");

        db.insert("tracks", null, values);

        String sql = "SELECT last_insert_rowid()";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        trackid = cursor.getInt(0);
        cursor.close();
        return trackid;
    }

    public void insertFirstTrack(String name, int trackid) {
        this.trackid = trackid;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT  OR IGNORE INTO tracks  (_id, name, isvisible, iscurrent, style ) VALUES ('" + trackid + "','" + name + "', 1, 1, 'Track')";
        db.execSQL(query);
    }

    public void updateTrack(int trackID, String name, String trackDescription, boolean isVisible, boolean isCurrent, String style) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (isCurrent) {
            String setNoneCurrent = "UPDATE tracks SET iscurrent = 0";
            db.execSQL(setNoneCurrent);
        }

        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_NAME, name);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_DESCRIPTION, trackDescription);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_ISVISIBLE, isVisible);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_ISCURRENT, isCurrent);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_STYLE, style);
        values.put(TrackContract.TrackEntry.COLUMN_TRACKS_UPDATED, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        values.put(TrackContract.TrackEntry._ID, trackID);

        String[] whereArgs = new String[]{String.valueOf(trackID)};
        String where = "_id=?";
        db.update("tracks", values, where, whereArgs);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<ArrayList<LatLng>> getAllTrackPoints() {

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT _id  FROM tracks WHERE isVisible = true ORDER BY _id ASC";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Integer> theTrackIndices = new ArrayList<>();
        while (cursor.moveToNext()) {
            theTrackIndices.add(cursor.getInt(0));
        }
        cursor.close();

        int count = theTrackIndices.size();

        ArrayList<ArrayList<LatLng>> allTrackData = new ArrayList<>();
        for(int i = 0; i<count; i++) {
            int index = theTrackIndices.get(i);
            query = "SELECT lat, lng  FROM waypoints WHERE trackid = " + index + " ORDER BY _id ASC";

            cursor = db.rawQuery(query, null);
            ArrayList<LatLng> theTrackData = new ArrayList<>();
            while (cursor.moveToNext()) {
                theTrackData.add(new LatLng(cursor.getDouble(0), cursor.getDouble(1)));
            }
            allTrackData.add(theTrackData);
        }
        cursor.close();
        return allTrackData;
    }

    public void setVisible(int trackid, boolean visibility) {
        SQLiteDatabase db = this.getWritableDatabase();
        int vis = (visibility) ? 1 : 0;
        String query = "UPDATE tracks SET isvisible = " + vis + " WHERE _id = " + trackid;
        db.execSQL(query);
    }

    public void setCurrent(int trackid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE tracks SET iscurrent = false";
        db.execSQL(query);
        query = "UPDATE tracks SET iscurrent = true WHERE _id = " + trackid;
        db.execSQL(query);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
    }
}