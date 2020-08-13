package com.example.myuber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DriverRequestList extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private Button d1;
    LocationManager mLocationManager;
    LocationListener mListener;
    private ListView listView;
    private ArrayList<String> nearByDriveRequests;
    private ArrayAdapter adapter;
    private ArrayList<Double> passengersLatitudes;
    private ArrayList<Double> passengersLongitudes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_request_list);
d1=findViewById(R.id.btnupdate);
        listView = findViewById(R.id.requestListView);
        nearByDriveRequests = new ArrayList<>();
        passengersLatitudes = new ArrayList<>();
        passengersLongitudes = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nearByDriveRequests);
        listView.setAdapter(adapter);
        nearByDriveRequests.clear();

d1.setOnClickListener(new View.OnClickListener() {
    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {



        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

             updateRequestsListView(location);
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

            if (ContextCompat.checkSelfPermission(DriverRequestList.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(DriverRequestList.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);


            } else {

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);

                Location currentDriverLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               updateRequestsListView(currentDriverLocation);

            }
        }


    }
});
listView.setOnItemClickListener(this);
    }

    private void updateRequestsListView(Location driverLocation) {
        if(driverLocation !=null){



            final ParseGeoPoint driverCurrentLocation = new ParseGeoPoint(driverLocation.getLatitude(), driverLocation.getLongitude());

            ParseQuery<ParseObject> requestCarQuery = ParseQuery.getQuery("Requestcar");
            requestCarQuery.whereNear("passengerLocation", driverCurrentLocation);
            requestCarQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (  nearByDriveRequests.size()>0) {

                            nearByDriveRequests.clear();

                        }
                        if(passengersLatitudes.size()>0) {
                            passengersLatitudes.clear();
                        }

                        if(passengersLongitudes.size()>0) {
                            passengersLongitudes.clear();
                        }

                            for (ParseObject nearRequest : objects){
                                ParseGeoPoint pLocation = (ParseGeoPoint) nearRequest.get("passengerLocation");
                                Double milesDistanceToPassenger = driverCurrentLocation.distanceInMilesTo(pLocation);

                                // 5.87594834787398943 * 10

                                //  58.246789 // Result
                                // 5

                                    float roundedDistanceValue = Math.round(milesDistanceToPassenger * 10) / 10;


                                    nearByDriveRequests.add("There are " + roundedDistanceValue + " miles to " + nearRequest.get("username"));
                                    passengersLatitudes.add(pLocation.getLatitude());
                                    passengersLongitudes.add(pLocation.getLongitude());

                                }


                        } else {
                            Toast.makeText(getApplicationContext(), "no requests yet", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    }




            });


        }


}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.driverlogout) {

            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        finish();

                    }
                }
            });

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(DriverRequestList.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);

               Location currentDriverLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               updateRequestsListView(currentDriverLocation);


            }
        }

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        

    }
}
