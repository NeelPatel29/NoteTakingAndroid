package com.example.neel.notetakingandroid;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerDragListener {

    GoogleMap map;
    private GoogleMap mMap;


    private double longitude;
    private double latitude;
    String uLt,uLn;
    RelativeLayout relativeLayout;
    TextView textView;


    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
        Intent intent = getIntent();
        uLt = intent.getStringExtra("lat");
        uLn = intent.getStringExtra("lon");


        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    private void getCurrentLocation() {
        mMap.clear();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {

            longitude = location.getLongitude();
            latitude = location.getLatitude();


            moveMap();
        }
    }


    private void moveMap() {
        String msg = latitude + ", "+longitude;

        LatLng latLng;
        if(uLt == null || uLn == null) {
            latLng = new LatLng(latitude, longitude);
        }
            else {
            latLng = new LatLng(Double.parseDouble(uLt),Double.parseDouble(uLn));


            textView = new TextView(getApplicationContext());
            Location loc1 = new Location("");
            loc1.setLatitude(latitude);
            loc1.setLongitude(longitude);

            Location loc2 = new Location("");
            loc2.setLatitude(Double.parseDouble(uLt));
            loc2.setLongitude(Double.parseDouble(uLn));
            int distanceInMeters = (int) loc1.distanceTo(loc2);
            textView.setText("You are away from your current location is "+distanceInMeters/1000+" KM");
            textView.setTextSize(20);
            relativeLayout.addView(textView);
            PolylineOptions line=
                    new PolylineOptions().add(new LatLng(latitude,
                                    longitude),
                            new LatLng(Double.parseDouble(uLt),
                                    Double.parseDouble(uLn)))
                            .width(5).color(Color.RED);
            line.geodesic(true);

            mMap.addPolyline(line);
        }


        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("cityName"));


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));


        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        moveMap();
    }

}
