package com.alexsykes.trackmonstereditor.data;

import android.provider.BaseColumns;

import androidx.appcompat.app.AppCompatActivity;

public class WaypointContract extends AppCompatActivity {
    private WaypointContract() {}

    public static final class WaypointEntry implements BaseColumns {

        /** Name of database table for waypoints */
        public final static String TABLE_NAME = "waypoints";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_WAYPOINTS_TRACKID = "trackid";
        public final static String COLUMN_WAYPOINTS_ALT = "alt";
        public final static String COLUMN_WAYPOINTS_LAT = "lat";
        public final static String COLUMN_WAYPOINTS_LNG = "lng";
        public final static String COLUMN_WAYPOINTS_CREATED = "created";
        public final static String COLUMN_WAYPOINTS_UPDATED = "updated";
        public final static String COLUMN_WAYPOINTS_SEGMENT = "segment";
        public final static String COLUMN_WAYPOINTS_NAME = "name";
        public final static String COLUMN_WAYPOINTS_MARKER = "marker";
        public final static String COLUMN_WAYPOINTS_SPEED = "speed";
        public final static String COLUMN_WAYPOINTS_BEARING = "bearing";
    }
}
