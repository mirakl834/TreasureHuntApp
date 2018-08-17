package com.findit.findit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HuntBeginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvHuntTitle;
    private Button btnBeginHunt;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean mLocationPermissionGranted;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private Location targetLocation = new Location("");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_begin);

        this.tvHuntTitle = this.findViewById(R.id.tvHuntTitle);

        Bundle bundle = getIntent().getExtras();

        final String[] targetInfo = bundle.getString("targetInfo").split(";");

        String[] targetPoint = targetInfo[3].split(":");
        this.targetLocation.setLatitude(Double.parseDouble(targetPoint[0]));
        this.targetLocation.setLongitude(Double.parseDouble(targetPoint[1]));


        this.tvHuntTitle.append("\n "+ bundle.getString("targetInfo"));

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float distInMeters = location.distanceTo(targetLocation);

                if(distInMeters > 50){
                    tvHuntTitle.append("\n "+location.getLatitude()+
                            ", "+location.getLongitude());
                    tvHuntTitle.append("\n Meters left: "+ distInMeters);
                } else {
                    locationManager.removeUpdates(locationListener);
                    showToast(targetInfo[2]);
                    goMain();
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

    }

    public void showToast(String targetMsg){
        Toast.makeText(this, targetMsg, Toast.LENGTH_SHORT).show();
    }

    public void goMain(){
        Intent mainIntent = new Intent(this,MainActivity.class);
        startActivity(mainIntent);
    }


    private void getPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        if(mLocationPermissionGranted){
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        }
    }
}
