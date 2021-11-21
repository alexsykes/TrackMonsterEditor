package com.alexsykes.trackmonstereditor.handlers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.trackmonstereditor.R;
import com.alexsykes.trackmonstereditor.activities.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackHolder> {
    ArrayList<HashMap<String, String>> theTrackList;
    HashMap<String, String> theTrack;
    static SharedPreferences preferences;

    public TrackListAdapter(ArrayList<HashMap<String, String>> theTrackList) {
        this.theTrackList = theTrackList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public TrackHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        preferences  = PreferenceManager.getDefaultSharedPreferences(viewGroup.getContext());

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.track_row, viewGroup, false);
        return new TrackHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackHolder holder, @SuppressLint("RecyclerView") int position) {
        theTrack = theTrackList.get(position);
        holder.isVisCheckbox.setChecked(1 == Integer.parseInt(Objects.requireNonNull(theTrack.get("isVisible"))));
        holder.nameTextView.setText(theTrack.get("name"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Info", "onClick: " + theTrackList.get(position).get("id"));
                String id = theTrackList.get(position).get("id");
                Context context = view.getContext();
                ((MainActivity) context).onClickCalled(id);
            }
        });

        holder.isVisCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int trackid = Integer.valueOf(theTrackList.get(position).get("id"));
                Log.i("Info", "isVisCheckbox.onCheckedChanged: " + trackid);
                boolean checked;
                checked = holder.isVisCheckbox.isChecked();
                Context context = buttonView.getContext();
                ((MainActivity) context).updateVisible(trackid, checked);
            }
        });

    }

    @Override
    public int getItemCount() { return theTrackList.size(); }

    public static class TrackHolder extends RecyclerView.ViewHolder {
        TextView idTextView, nameTextView;
        CheckBox isVisCheckbox;
        RadioButton isCurrentRadioButton;

        public TrackHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name);
            // isCurrentRadioButton = itemView.findViewById(R.id.isCurrentRadioButton);
            isVisCheckbox = itemView.findViewById(R.id.isVisCheckbox);
        }

        public void bind(final HashMap<String, String> theTrial, final AdapterView.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = theTrial.get("id");
                    Context context = v.getContext();
                    ((MainActivity) context).onClickCalled(id);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String id = theTrial.get("id");
                    setActive(id);
                    Log.i("Info", "onLongClick: " + id);
                    itemView.setBackgroundColor(Color.GREEN);
                    return true; // Halt execution of normal click
                }
            });
        }

        private void setActive(String id) {
            int trackID = Integer.valueOf(id);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("trackid", trackID);
            editor.apply();
        }
    }
}