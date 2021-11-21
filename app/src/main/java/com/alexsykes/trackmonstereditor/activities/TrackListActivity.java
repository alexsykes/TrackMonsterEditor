package com.alexsykes.trackmonstereditor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.trackmonstereditor.R;
import com.alexsykes.trackmonstereditor.data.TrackDbHelper;
import com.alexsykes.trackmonstereditor.handlers.TrackListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class TrackListActivity extends AppCompatActivity {
    TrackDbHelper trackDbHelper;
    ArrayList<HashMap<String, String>> theTrackList;
    RecyclerView trackView;
    private static final String TAG = "Info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        trackDbHelper = new TrackDbHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), TrackDialogActivity.class);
            intent.putExtra("task", "new");
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateTrackList();
        Log.i(TAG, "TrackListActivity: onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "TrackListActivity: onResume: ");
    }

    public void onClickCalled(String trackid) {
        // Call another activity here and pass some arguments to it.
        Intent intent = new Intent(this, TrackDialogActivity.class);
        intent.putExtra("trackid", trackid);
        intent.putExtra("task", "update");
        startActivity(intent);
    }

    private void populateTrackList() {
        theTrackList = trackDbHelper.getShortTrackList();
        trackView = findViewById(R.id.trackView);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        trackView.setLayoutManager(llm);
        trackView.setHasFixedSize(true);
        initializeAdapter();
    }

    private void initializeAdapter() {
        TrackListAdapter adapter = new TrackListAdapter(theTrackList);
        trackView.setAdapter(adapter);
    }

    public void updateVisible(int trackid, boolean visibility) {
        trackDbHelper.setVisible(trackid, visibility);
        populateTrackList();
    }

    public void updateCurrent(int trackid) {
        trackDbHelper.setCurrent(trackid);
        populateTrackList();
    }
}