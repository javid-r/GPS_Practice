package com.practice.javid.gps_practice;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final int permissionRequestCode = 1;

    private AppCompatActivity context;
    private static Button getOriginButton;
    private static Button getDestinationButton;
    private static TextView distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = MainActivity.this;
        getOriginButton = (Button) findViewById(R.id.btn_origin);
        getDestinationButton = (Button) findViewById(R.id.btn_destination);
        distance = (TextView) findViewById(R.id.txt_distance);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(context, GPS_Service.class));
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode);
            }
        } else {
            startService(new Intent(context, GPS_Service.class));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == permissionRequestCode) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(context, GPS_Service.class));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(context, GPS_Service.class));
        super.onDestroy();
    }


    public static class GPS_Service extends Service implements LocationListener {

        private Context context;
        private LocationListener locationListener;
        private LocationManager locationManager;

        private Location originLocation;
        private Location destinationLocation;



        public GPS_Service(){
            super();
        }

        @Override
        public void onCreate() {

            context = GPS_Service.this;
            locationListener = GPS_Service.this;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            super.onCreate();
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {


            try {

                if (Build.VERSION.SDK_INT >= 23) {

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            setListeners();
                        }
                    }

                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                }

            } catch (Exception e) {
                Log.e("My Code: ", "class -> GPS_Service in method -> onStartCommand -> " + e.toString());
            }

            return START_STICKY;
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i("location","Lat="+location.getLatitude()+" -Long="+location.getLongitude()+ " -Altitude="+location.getAltitude());
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

        @Override
        public void onDestroy() {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (Exception e) {
                Log.e("My Code: ", "class -> GPS_Service in method -> onDestroy -> " + e.toString());
            }

            super.onDestroy();
        }

        private void setListeners() {

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                getOriginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        originLocation = getCurrentLocation();
                    }
                });

                getDestinationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        destinationLocation = getCurrentLocation();

                        if (destinationLocation != null && originLocation != null) {
                            float f_Distance =destinationLocation.distanceTo(originLocation);
                            String s_Distance = f_Distance + "";
                            distance.setText(s_Distance);
                        }
                    }
                });

            }
        }

        private Location getCurrentLocation() {
            try {
                if (Build.VERSION.SDK_INT >= 23) {

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }

                } else {
                    return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            } catch (Exception e) {
                Log.e("My Code: ", "class -> GPS_Service in method -> getCurrentLocation -> " + e.toString());
            }

            return null;
        }
    }
}
