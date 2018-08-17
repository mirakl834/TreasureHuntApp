package com.findit.findit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements CreateTargetFragment.OnFragmentInteractionListener, TargetListFragment.OnFragmentInteractionListener {

    final String FCREATE = "FCREATE";
    final String FLIST = "FLIST";

    String currentFragment = FCREATE;

    FragmentManager fm;
    CreateTargetFragment fCreateTarget;
    TargetListFragment fTargetList;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiating top fragment
        fm = getSupportFragmentManager();
        FragmentTransaction ftContainer = fm.beginTransaction();
        this.fTargetList= new TargetListFragment();
        ftContainer.replace(R.id.flContainer,fTargetList, FLIST);
        ftContainer.commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Intent intent = new Intent();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(currentFragment != FLIST){
                        currentFragment = FLIST;
                        switchTargetListFragment();
                        return true;
                    }
                    break;
                case R.id.navigation_dashboard:
                    if(currentFragment != FCREATE){
                        currentFragment = FCREATE;
                        switchCreateTargetFragment();
                        return true;
                    }
                    break;
                case R.id.navigation_notifications:
                    return true;
            }
            //startActivity(intent);

            return false;
        }
    };

    private void switchTargetListFragment(){
        if(fTargetList!=null){
            FragmentTransaction ftContainer = fm.beginTransaction();
            ftContainer.replace(R.id.flContainer,fTargetList, FLIST);
            ftContainer.commit();
        } else {
            FragmentTransaction ftContainer = fm.beginTransaction();
            this.fTargetList= new TargetListFragment();
            ftContainer.replace(R.id.flContainer,fTargetList, FLIST);
            ftContainer.commit();
        }
    }

    private void switchCreateTargetFragment(){
        if(fCreateTarget!=null){
            FragmentTransaction ftContainer = fm.beginTransaction();
            ftContainer.replace(R.id.flContainer,fCreateTarget, FCREATE);
            ftContainer.commit();
        } else {
            FragmentTransaction ftContainer = fm.beginTransaction();
            this.fCreateTarget= new CreateTargetFragment();
            ftContainer.replace(R.id.flContainer,fCreateTarget, FCREATE);
            ftContainer.commit();
        }
    }

    @Override
    public void goCreateTarget(String sMsg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Task Created !");

        alertDialogBuilder
                .setMessage(sMsg)
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.dismiss();
                        switchTargetListFragment();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void goChangeMarkerLocation(LatLng llMarker) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Target Location changed !");

        String sMsg =
                "Location: Latitude - " + llMarker.latitude + "\nLongitude - " + llMarker.longitude;
        alertDialogBuilder
                .setMessage(sMsg)
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public void goDeleteTarget(String sTargetList) {
        FileOutputStream fileOutputStream = null;
        try{
            fileOutputStream = openFileOutput("hunt.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(sTargetList.getBytes());
            fileOutputStream.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try{
                fileOutputStream.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        switchCreateTargetFragment();
    }

    @Override
    public void goBeginHunt(String sTargetInfo) {
        Intent huntBeginIntent = new Intent(this,HuntBeginActivity.class);
        huntBeginIntent.putExtra("targetInfo", sTargetInfo);
        startActivity(huntBeginIntent);
    }
}
