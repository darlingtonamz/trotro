package com.amanzed.beacon;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.amanzed.beacon.tro.StopsListAdapter;

/**
 * Created by Amanze on 5/28/2016.
 */
public class DriverActivity extends AppCompatActivity {
    FloatingActionButton fabAlarm_off, fabAlarm_on;
    Boolean isAlarmOn;
    ListView list;
    StopsListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_main);

        adapter = new StopsListAdapter(this, TrotroActivity.stops);
        list = (ListView) findViewById(R.id.stops_list_view);
        list.setAdapter(adapter);
        //list.setOnItemClickListener(this);



        fabAlarm_off = (FloatingActionButton)findViewById(R.id.floatalarm_off);
        fabAlarm_on = (FloatingActionButton)findViewById(R.id.floatalarm_on);
        isAlarmOn = false;

        fabAlarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabAlarm_off.setVisibility(View.GONE);
                fabAlarm_on.setVisibility(View.VISIBLE);
            }
        });
        fabAlarm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabAlarm_on.setVisibility(View.GONE);
                fabAlarm_off.setVisibility(View.VISIBLE);
            }
        });

    }
}
