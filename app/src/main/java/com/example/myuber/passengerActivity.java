package com.example.myuber;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class passengerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationListener mListener;
    Button RequestCar;
    private boolean isUberCancelled=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        RequestCar = findViewById(R.id.b1);
        RequestCar.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              if (isUberCancelled) {
                                                  if (ContextCompat.checkSelfPermission(passengerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                                                      mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);
                                                      Location PassengerLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                                      updateCameraPassengerLocation(PassengerLocation);
                                                      if (PassengerLocation != null) {
                                                          ParseObject requestcar = new ParseObject("Requestcar");
                                                          requestcar.put("username", ParseUser.getCurrentUser().getUsername());
                                                          ParseGeoPoint userLocation = new ParseGeoPoint(PassengerLocation.getLatitude(), PassengerLocation.getLongitude());
                                                          requestcar.put("passengerLocation", userLocation);
                                                          requestcar.saveInBackground(new SaveCallback() {
                                                              @Override
                                                              public void done(ParseException e) {
                                                                  if (e == null) {
                                                                      Toast.makeText(getApplicationContext(), "A car request is sent", Toast.LENGTH_SHORT).show();
                                                                      RequestCar.setText("Cancel ride");
                                                                      isUberCancelled = false;
                                                                  }
                                                              }
                                                          });
                                                      } else {
                                                          Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_SHORT);

                                                      }

                                                  }
                                              }
                                              else{


                                                  ParseQuery<ParseObject> carRequestQuery = ParseQuery.getQuery("Requestcar");
                                                  carRequestQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                                                  carRequestQuery.findInBackground(new FindCallback<ParseObject>() {
                                                      @Override
                                                      public void done(List<ParseObject> requestList, ParseException e) {
                                                          if(requestList.size()>0 && e==null){
                                                              isUberCancelled = true;
                                                              RequestCar.setText("Request a new uber");
                                                              for(ParseObject uberRequest:requestList){
                                                                  uberRequest.deleteEventually(new DeleteCallback() {
                                                                      @Override
                                                                      public void done(ParseException e) {
                                                                          if (e == null) {
                                                                              Toast.makeText(passengerActivity.this, "Request/s deleted", Toast.LENGTH_SHORT).show();
                                                                          }
                                                                      }
                                                                  });
                                                              }

                                                          }
                                                      }
                                                  });







                                              }
                                          }






                                      });




        findViewById(R.id.logoutpass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            finish();
                        }
                    }
                });
            }
        });
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                updateCameraPassengerLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);

        } else if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(passengerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(passengerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);


            } else {

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);

                Location currentPassengerLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateCameraPassengerLocation(currentPassengerLocation);

            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(passengerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);

                Location currentPassengerLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateCameraPassengerLocation(currentPassengerLocation);

            }
        }

    }

    private void updateCameraPassengerLocation(Location pLocation) {


        LatLng passengerLocation = new LatLng(pLocation.getLatitude(), pLocation.getLongitude());
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(passengerLocation, 15));

        mMap.addMarker(new MarkerOptions().position(passengerLocation).title("You are here!!!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

    }

}