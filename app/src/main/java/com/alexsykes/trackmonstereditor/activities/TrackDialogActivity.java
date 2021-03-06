package com.alexsykes.trackmonstereditor.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alexsykes.trackmonstereditor.R;
import com.alexsykes.trackmonstereditor.data.TrackData;
import com.alexsykes.trackmonstereditor.data.TrackDbHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackDialogActivity extends AppCompatActivity implements OnMapReadyCallback {
    // Request code for creating a PDF document.
    private static final int CREATE_FILE = 1;
    private static final int PICK_PDF_FILE = 2;
    private static final String TAG = "Info";

    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 30;
    private static final int PATTERN_GAP_LENGTH_PX = 30;
    private static final int PATTERN_DOT_SPACING_PX = 10;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final PatternItem GAP_SPACING = new Gap(PATTERN_DOT_SPACING_PX);
    private static final PatternItem DOT = new Dot();
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP_SPACING, DOT);
    private static final List<PatternItem> PATTERN_POLYLINE_DASHED = Arrays.asList(GAP, DASH);
    private List<PatternItem> patternItemList;



    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);


    File exportDir = new File(Environment.getExternalStoragePublicDirectory("Documents/Scoremonster"), "");
    TextInputLayout nameTextInputLayout;
    TextInputLayout descriptionTextInputLayout;
    CheckBox isVisibleCheckBox;
    CheckBox isCurrentCheckBox;
    RadioGroup trackStyleGroup;
    RadioButton undefinedButton, trackButton, roadButton, majorRoadButton;
    String name;
    int mapType;

    String style;
    String trackDescription;
    int trackID;
    TrackDbHelper trackDbHelper;
    Intent intent;
    SharedPreferences prefs;
    TrackData trackData;
    Polyline polyline;
    PolylineOptions polylineOptions;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_dialog);

        // Setup UI
        nameTextInputLayout = findViewById(R.id.track_name_text_input);
        descriptionTextInputLayout = findViewById(R.id.track_detail_text_input);
        isVisibleCheckBox = findViewById(R.id.track_visibility_checkBox);
        isCurrentCheckBox = findViewById(R.id.track_active_checkBox);
        // Map components
        View mLayout = findViewById(R.id.trackMap);
        trackStyleGroup = findViewById(R.id.trackStyleGroup);
        undefinedButton = findViewById(R.id.undefinedButton);
        trackButton = findViewById(R.id.trackButton);
        roadButton = findViewById(R.id.roadButton);
        majorRoadButton = findViewById(R.id.majorRoadButton);

        polylineOptions = new PolylineOptions();

        // Get data
        trackDbHelper = new TrackDbHelper(this);
        intent = getIntent();
        trackID = Integer.parseInt(intent.getExtras().getString("trackid", "1"));
        mapType = intent.getExtras().getInt("mapType");

        // Set initial values
        trackData = trackDbHelper.getTrackData(trackID);

        nameTextInputLayout.getEditText().setText(trackData.getName());
        descriptionTextInputLayout.getEditText().setText(trackData.getDescription());
        boolean isVisible = trackData.isVisible();
        boolean isCurrent = trackData.isCurrent();
        isVisibleCheckBox.setChecked(isVisible);
        isCurrentCheckBox.setChecked(isCurrent);
        style = trackData.getStyle();

        switch (style) {
            case "Track":
                trackButton.setChecked(true);
                break;
            case "Road":
                roadButton.setChecked(true);
                break;
            case "Major road":
                majorRoadButton.setChecked(true);
                break;
            case "Undefined":
                undefinedButton.setChecked(true);
                break;
            default:
        }

        // Add listeners for changes
        nameTextInputLayout.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                saveTrackDetails();
            }
        });
        descriptionTextInputLayout.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                saveTrackDetails();
            }
        });
        trackStyleGroup.setOnCheckedChangeListener((group, checkedId) -> saveTrackDetails());
        isVisibleCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> saveTrackDetails());
        isCurrentCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> saveTrackDetails());

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.trackMap);
        mapFragment.getMapAsync(this);


        // prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // trackIdFromPrefs = prefs.getInt("trackid", 1);

        // Get existing track details and show values
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.track_dialog_bottom_menu, menu);
        return true;
    }

    private void saveTrackDetails() {
        name = nameTextInputLayout.getEditText().getText().toString();
        trackDescription = descriptionTextInputLayout.getEditText().getText().toString();
        boolean isVisible = isVisibleCheckBox.isChecked();
        boolean isCurrent = isCurrentCheckBox.isChecked();

        int radioButtonID = trackStyleGroup.getCheckedRadioButtonId();
        RadioButton selected = trackStyleGroup.findViewById(radioButtonID);
        style = selected.getText().toString();

        trackDbHelper.updateTrack(trackID, name, trackDescription, isVisible, isCurrent, style);

        updateTrack();
        trackDbHelper.close();
    }

    @Override
    public void onStop() {
        saveTrackDetails();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        map.setMapType(mapType);
        displayTrack();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu_item:
                saveTrackData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void emailTrackData() {
        trackDataToKML(trackData);
        // openFile();
    }

    private void saveTrackData() {
        String filename = "trackdata.kml";
        trackDataToKML(trackData);
    }

    private void createFile(String filename) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.setType("Application/Vnd.google-earth.kml");
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");
        intent.putExtra(Intent.EXTRA_TITLE, filename);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, "Documents");

        startActivityForResult(intent, CREATE_FILE);
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // intent.setType("application/pdf");
        intent.setType("Application/Vnd.google-earth.kml");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_PDF_FILE);
    }

    private void displayTrack() {


        LatLngBounds latLngBounds = trackData.getLatLngBounds();
        ArrayList<LatLng> latLngs = trackData.getLatLngs();
        // Create polyline options with existing LatLng ArrayList
        polylineOptions.addAll(latLngs);
        updateTrack();

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 1000, 1000, 3));
    }

    private void updateTrack() {
        map.clear();
        polyline = map.addPolyline(polylineOptions);

        int strokeWidth = 4;
        int strokeColour = Color.BLACK;
        patternItemList = null;
        switch (style) {
            case "Undefined":
                strokeColour = getResources().getColor(R.color.COLOR_UNDEFINED_ARGB);
                strokeWidth = 6;
                patternItemList = PATTERN_POLYLINE_DOTTED;
                break;
            case "Track":
                strokeColour = getResources().getColor(R.color.COLOR_TRACK_ARGB);
                patternItemList = PATTERN_POLYLINE_DASHED;
                strokeWidth = 6;
                break;
            case "Road":
                strokeColour = getResources().getColor(R.color.COLOR_ROAD_ARGB);
                break;
            case "Major road":
                strokeColour = getResources().getColor(R.color.COLOR_MAJOR_ARGB);
                strokeWidth = 6;
                break;
        }
        polyline.setColor(strokeColour);
        polyline.setWidth(strokeWidth);
        polyline.setPattern(patternItemList);
    }

    private boolean fileWrite(String data, String filename) {
        String name;
        name = filename;
        File dir = Environment.getExternalStoragePublicDirectory("Documents/TrackMonster");
        try {
            exportDir = new File(dir, filename);
            exportDir.createNewFile();
            FileWriter fileWriter = new FileWriter(exportDir);
            String header = data;
            fileWriter.write(header);
            fileWriter.close();
            Context context = getApplicationContext();

            CharSequence text = "Track data saved to Documents/TrackMonster";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();


            return true;

        } catch (
                IOException e) {
            Log.e("Child", e.getMessage(), e);
            Context context = getApplicationContext();
            CharSequence text = e.getMessage();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            return false;
        }

    }

    private String trackDataToKML(TrackData trackData) {
        ArrayList<LatLng> latLngs = trackData.getLatLngs();
        int numPoints = latLngs.size();
        String name = trackData.getName();
        String trackType = trackData.getStyle();
        String filename = name + ".kml";
        String description = trackData.getDescription();

        String pointsString = "";
        StringBuilder stringBuilder = new StringBuilder();
        // Add header lines
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
                "<Document>");

        // Add pathname and description
        stringBuilder.append("\n" +
                "    <name>" + name + "</name>\n" +
                "    <open>1</open>");
        stringBuilder.append("\n" +
                "    <description>" + description + "</description>\n");

        // Add style data
        stringBuilder.append("<Style id=\"blueline\">\n" +
                "    <LineStyle>\n" +
                "        <color>7fff0000</color>\n" +
                "        <width>4</width>\n" +
                "    </LineStyle>\n" +
                "    <PolyStyle>\n" +
                "        <color>7fff0000</color>\n" +
                "     </PolyStyle>\n" +
                "</Style>\n");

        stringBuilder.append("<Folder>\n" +
                "    <name>Paths</name>\n" +
                "    <visibility>1</visibility>\n" +
                "    <description>" + trackType + "</description>\n" +
                "<Placemark>   \n" +
                "   <name>Something else goes here</name>\n" +
                "   <visibility>1</visibility>" +
                "    \n   <styleUrl>#blueline</styleUrl>");

        stringBuilder.append("\n<LineString>\n" +
                "<coordinates>\n");

        for (int i = 0; i < numPoints; i++) {
            LatLng latLng = latLngs.get(i);
            String lat = String.valueOf(latLng.latitude);
            String lng = String.valueOf(latLng.longitude);
            stringBuilder.append(lng);
            stringBuilder.append(",");
            stringBuilder.append(lat);
            stringBuilder.append(",");
            stringBuilder.append("0");
            stringBuilder.append("\n");
        }
        stringBuilder.append("</coordinates>\n" +
                "</LineString>\n" +
                "</Placemark>\n" +
                "</Folder>\n" +
                "</Document>\n" +
                "</kml>");

        fileWrite(stringBuilder.toString(), filename);
        return stringBuilder.toString();
    }

}