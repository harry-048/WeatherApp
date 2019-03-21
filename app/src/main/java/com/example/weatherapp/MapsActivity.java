package com.example.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    // updateLocation(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // Toast.makeText(getBaseContext(),"its not null",Toast.LENGTH_SHORT).show();
            if (lastKnownLocation != null) {

                //updateLocation(lastKnownLocation);
            }
        }


    }

    public void updateLocation(Location location) {
        if (location != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(userLocation)      // Sets the center of the map to location user
                    .zoom(13)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.addMarker(new MarkerOptions().position(userLocation));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    public void startListening() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Location lastloc;
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else{
            lastloc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if(lastloc!=null){
            LatLng curPosition = new LatLng(lastloc.getLatitude(), lastloc.getLongitude());
            //  mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(curPosition)      // Sets the center of the map to location user
                    .zoom(13)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.addMarker(new MarkerOptions().position(curPosition).title("Here you are!"));
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(latLng!=null){
            mMap.addMarker(new MarkerOptions().position(latLng).title("Get weather of place"));
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            Log.d("latandlong",latitude+","+longitude);
        //    Toast.makeText(getBaseContext(),latitude+","+longitude,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("latitude",latitude);
            intent.putExtra("longitude",longitude);
            startActivity(intent);
            finish();

        }
      //  Toast.makeText(getBaseContext(),"no location find",Toast.LENGTH_SHORT).show();
    }
}
