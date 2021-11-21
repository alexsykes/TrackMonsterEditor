package com.alexsykes.trackmonstereditor.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.alexsykes.trackmonstereditor.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        SharedPreferences prefs;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            setup();
        }

        private void setup() {
            ListPreference intervalPref = findPreference("intervalString");
            assert intervalPref != null;

            intervalPref.setOnPreferenceChangeListener((preference, newValue) -> {
                SharedPreferences.Editor editor = prefs.edit();
                intervalPref.setValue(newValue.toString());
                int samplingInterval = Integer.parseInt(newValue.toString());
                editor.putInt("interval", samplingInterval);
                editor.putString("intervalString", newValue.toString());
                editor.apply();
                return false;
            });
        }

    }
}