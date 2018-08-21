package com.findit.findit;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class HuntBeginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvHuntTitle;
    private TextView tvHunDescription;
    private TextView tvHuntDistanceLeft;
    private  Button btnStopHunt;
    private Button btnBeginHunt;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean mLocationPermissionGranted;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    String sProvider;

    Context context = this;
    float distInMeters;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastKnownLocation;


    private Location targetLocation = new Location("");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_begin);

        this.tvHuntTitle = this.findViewById(R.id.tvHuntTitle);
        this.tvHunDescription = this.findViewById(R.id.tvHuntDescription);
        this.tvHuntDistanceLeft = this.findViewById(R.id.tvHuntDistanceLeft);

        Bundle bundle = getIntent().getExtras();

        final String[] targetInfo = bundle.getString("targetInfo").split(";");

        String[] targetPoint = targetInfo[3].split(":");
        this.targetLocation.setLatitude(Double.parseDouble(targetPoint[0]));
        this.targetLocation.setLongitude(Double.parseDouble(targetPoint[1]));


        this.tvHuntTitle.setText(targetInfo[0]);
        this.tvHunDescription.setText((targetInfo[1]));

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                distInMeters = location.distanceTo(targetLocation);

                if(distInMeters > 50){
                    updateDistanceLeft(""+distInMeters);
//                    locationManager.removeUpdates(locationListener);
                } else {
                    locationManager.removeUpdates(locationListener);
                    showClue(targetInfo[2]);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        };
        getPermissions();

        this.btnBeginHunt = this.findViewById(R.id.btnBeginHunt);
        this.btnBeginHunt.setOnClickListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            if(mLocationPermissionGranted){
                locationManager.requestSingleUpdate("gps",locationListener,null);
            }
        } catch (SecurityException e){
            e.printStackTrace();
        }
    }

    public void updateDistanceLeft(String distLeft){
        this.tvHuntDistanceLeft.setText("Distance Left: "+distLeft+"m");
    }

    public void showClue(String targetMsg){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Congratulations !");

        String sMsg = "You have found the clue! \n The secret hint to the treasure is : " + targetMsg;
        alertDialogBuilder
                .setMessage(sMsg)
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.dismiss();
                        goMain();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void goMain(){
        Intent mainIntent = new Intent(this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }


    private void getPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        try{
            if(mLocationPermissionGranted){
                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
            }
        } catch (SecurityException e){
            e.printStackTrace();
        }
    }
}
