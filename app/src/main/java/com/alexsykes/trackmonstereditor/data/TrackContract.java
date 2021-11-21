package com.alexsykes.trackmonstereditor.data;

import android.provider.BaseColumns;

import androidx.appcompat.app.AppCompatActivity;

public class TrackContract extends AppCompatActivity {
    public TrackContract() {}

    public static final class TrackEntry implements BaseColumns {
        public final static String TABLE_NAME = "tracks";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TRACKS_CREATED = "created";
        public final static String COLUMN_TRACKS_UPDATED = "updated";
        public final static String COLUMN_TRACKS_NAME = "name";
        public final static String COLUMN_TRACKS_DESCRIPTION = "description";
        public final static String COLUMN_TRACKS_ISVISIBLE = "isvisible";
        public final static String COLUMN_TRACKS_ISCURRENT = "iscurrent";
        public final static String COLUMN_TRACKS_STYLE = "style";
    }
}
