package com.alexsykes.trackmonstereditor;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.alexsykes.trackmonstereditor.data.TrackContract;
import com.alexsykes.trackmonstereditor.data.TrackDbHelper;
import com.alexsykes.trackmonstereditor.data.WaypointContract;
import com.alexsykes.trackmonstereditor.data.WaypointDbHelper;

import java.io.File;

public class TrackMonster extends Application {

    // Databases
    private WaypointDbHelper waypointDbHelper;
    private TrackDbHelper trackDbHelper;
    SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
//        if(BuildConfig.DEBUG)
//            StrictMode.enableDefaults();
        Log.i("Info", "TrackMonster class: OnAppStart");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasSampleData = preferences.getBoolean("hasRun", false);
        if (!hasSampleData) {
            waypointDbHelper = new WaypointDbHelper(this);
            trackDbHelper = new TrackDbHelper(this);
            // Create database connection
            dbInit();
            for (int trackid = 1; trackid < 6; trackid++) {
                String trackName = "Track " + trackid;
                trackDbHelper.insertFirstTrack(trackName, trackid);
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("hasRun", true);
            editor.apply();
        }
        // Add directory for file export
        //final File externalFileDir = getExternalFilesDir(null);
        final File externalFileDir = Environment.getExternalStoragePublicDirectory("Documents");
        String folderName = "TrackMonster";

        File createDir = new File(externalFileDir.getAbsoluteFile(), folderName);
        if (!createDir.exists()) {
            createDir.mkdir();
        }
    }

    private void dbInit() {
        // Database operations - https://www.tutorialspoint.com/android/android_sqlite_database.htm
        // First, get your database
        final String DATABASE_NAME = "monster.db";

        SQLiteDatabase db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        // Create a String that contains the SQL statement to create the scores table
        String SQL_CREATE_WAYPOINT_TABLE = "CREATE TABLE IF NOT EXISTS " + WaypointContract.WaypointEntry.TABLE_NAME + " ("
                + WaypointContract.WaypointEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_TRACKID + " INTEGER , "
                + WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_MARKER + " INTEGER , "
                + WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_SEGMENT + " INTEGER NOT NULL DEFAULT 1, "
                + WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_ALT + " REAL, "
                + WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_NAME + " TEXT NOT NULL, "
                + WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_CREATED + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_UPDATED + " TEXT , "
                + WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LNG + " REAL , "
                + WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_SPEED + " REAL , "
                + WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_BEARING + " REAL , "
                + WaypointContract.WaypointEntry.COLUMN_WAYPOINTS_LAT + " REAL );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_WAYPOINT_TABLE);

        String SQL_CREATE_TRACK_TABLE = "CREATE TABLE IF NOT EXISTS " + TrackContract.TrackEntry.TABLE_NAME + " ("
                + TrackContract.TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TrackContract.TrackEntry.COLUMN_TRACKS_NAME + " TEXT NOT NULL, "
                + TrackContract.TrackEntry.COLUMN_TRACKS_DESCRIPTION + " TEXT,  "
                + TrackContract.TrackEntry.COLUMN_TRACKS_STYLE + " TEXT,  "
                + TrackContract.TrackEntry.COLUMN_TRACKS_ISVISIBLE + " INTEGER , "
                + TrackContract.TrackEntry.COLUMN_TRACKS_ISCURRENT + " INTEGER , "
                + TrackContract.TrackEntry.COLUMN_TRACKS_CREATED + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + TrackContract.TrackEntry.COLUMN_TRACKS_UPDATED + " TEXT );";

        db.execSQL(SQL_CREATE_TRACK_TABLE);
        db.close();
        waypointDbHelper.generateRandomData(20);
    }
    protected boolean canConnect() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
