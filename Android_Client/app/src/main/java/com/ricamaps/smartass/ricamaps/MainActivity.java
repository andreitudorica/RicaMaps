package com.ricamaps.smartass.ricamaps;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoibHVjaWFubHV0YXMiLCJhIjoiY2oybmp2enNvMDAzOTJxcWtlb3Rnem1raCJ9.SPYOA-LW7SyjLRH1utNweg");
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);



        final Button btnStart = findViewById(R.id.btnAddStart);
        final Button btnEnd = findViewById(R.id.btnAddEnd);

        final Marker[] startP = new Marker[1];
        final Marker[] endP = new Marker[1];

        final TimePicker timePicker = findViewById(R.id.timePicker);
        final Button btnOk = findViewById(R.id.btnOk);
        final Button btnTime = findViewById(R.id.btnSel);
        final Button btnNav = findViewById(R.id.startButton);

        final int[] time = new int[2];

        Calendar rightNow = Calendar.getInstance();

        time[0] =  rightNow.get(Calendar.HOUR_OF_DAY);
        time[1] = rightNow.get(Calendar.MINUTE);

        timePicker.setVisibility(View.INVISIBLE);
        btnOk.setVisibility(View.INVISIBLE);

        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnStart.setEnabled(false);
                btnEnd.setEnabled(true);
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnEnd.setEnabled(false);
                btnStart.setEnabled(true);
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnEnd.setEnabled(false);
                btnStart.setEnabled(false);
                btnTime.setEnabled(false);
                btnNav.setVisibility(View.INVISIBLE);
                timePicker.setVisibility(View.VISIBLE);
                btnOk.setVisibility(View.VISIBLE);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                btnEnd.setEnabled(true);
                btnStart.setEnabled(true);
                btnTime.setEnabled(true);
                btnNav.setVisibility(View.VISIBLE);
                timePicker.setVisibility(View.INVISIBLE);
                btnOk.setVisibility(View.INVISIBLE);
                time[0] = timePicker.getHour();
                time[1] = timePicker.getMinute();
                String str;
                if(time[1]<10){
                    str = String.valueOf(time[0]) + ":0" + String.valueOf(time[1]);
                } else {
                    str = String.valueOf(time[0]) + ":" + String.valueOf(time[1]);
                }
                btnTime.setText(str);
                //Toast.makeText(MainActivity.this, String.valueOf(time[0]) + ":" + String.valueOf(time[1]), Toast.LENGTH_LONG).show();
            }
        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

                mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override


                    public void onMapClick(@NonNull LatLng point) {
                        //String string = String.format(Locale.US, "User clicked at: %s", point.toString());
                        //Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
                        if(!btnStart.isEnabled()) {
                            btnStart.setEnabled(true);
                            if(startP[0] != null){
                                mapboxMap.removeMarker(startP[0]);
                            }

                            startP[0] = mapboxMap.addMarker(new MarkerViewOptions()
                                    .position(new LatLng(point.getLatitude(), point.getLongitude()))
                                    .title("Start")
                            );
                            if(endP[0] != null){
                                btnNav.setEnabled(true);
                            } else{
                                btnNav.setEnabled(false);
                            }
                        }
                        if(!btnEnd.isEnabled()) {
                            btnEnd.setEnabled(true);

                            if(endP[0] != null){
                                mapboxMap.removeMarker(endP[0]);
                            }

                            endP[0] = mapboxMap.addMarker(new MarkerViewOptions()
                                    .position(new LatLng(point.getLatitude(), point.getLongitude()))
                                    .title("Start")
                            );
                            if(startP[0] != null){
                                btnNav.setEnabled(true);
                            } else{
                                btnNav.setEnabled(false);
                            }
                        }
                    };
                });

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}