package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static android.location.LocationProvider.AVAILABLE;
import static android.location.LocationProvider.OUT_OF_SERVICE;
import static android.location.LocationProvider.TEMPORARILY_UNAVAILABLE;

//TODO: Do NOT touch this class!!!! ################################################################

final public class GPS extends Service{

    private Polyline polyline = null;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private ArrayList<Double> geoPointLat = null;
    private ArrayList<Double> geoPointLon = null;
    private ArrayList<Double> geoPointAlt = null;
    private ArrayList<Double> geoPointAcc = null;

    private int REFRESH_RATE = 100;
    private final int SIZE = 20;

    public GPS() {
        initializeLocationListener();
        polyline = Polyline.initPolyline();
        this.geoPointLat = new ArrayList<>(SIZE);
        this.geoPointLon = new ArrayList<>(SIZE);
        this.geoPointAlt = new ArrayList<>(SIZE);
        this.geoPointAcc = new ArrayList<>(SIZE);
    }

    protected void initializeLocationManager() {
        this.locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    private void locationListenerHelper(Location location) {

        if(geoPointAcc.size() < SIZE) {

            geoPointLat.add(location.getLatitude());
            geoPointLon.add(location.getLongitude());
            geoPointAlt.add(location.getAltitude());
            geoPointAcc.add((double)location.getAccuracy());

        } else {

            Double lat[] = new Double[SIZE];
            Double lon[] = new Double[SIZE];
            Double alt[] = new Double[SIZE];
            Double acc[] = new Double[SIZE];
            double tmp = 0.0;
            int marker = 0;

            geoPointLat.toArray(lat);
            geoPointLon.toArray(lon);
            geoPointAlt.toArray(alt);
            geoPointAcc.toArray(acc);

            geoPointLat.clear();
            geoPointLon.clear();
            geoPointAlt.clear();
            geoPointAcc.clear();

            geoPointLat.add(location.getLatitude());
            geoPointLon.add(location.getLongitude());
            geoPointAlt.add(location.getAltitude());
            geoPointAcc.add((double)location.getAccuracy());

            for(int i = 0; i < lat.length; i++) {
                if(lat[i] >= tmp) {
                    tmp = lat[i];
                    marker = i;
                }
            }

            Intent i = new Intent("GPS");
            i.putExtra("lat", lat[marker]);
            i.putExtra("lon", lon[marker]);
            i.putExtra("alt", alt[marker]);
            i.putExtra("acc", acc[marker]);
            i.putExtra("dist", polyline.getDistanceTotal());
            i.putExtra("speed", polyline.distance);
            sendBroadcast(i);

            polyline.setCurrentPosition(lat[marker], lon[marker]);

            marker = 0;

            CallbackLib.gpsCallback();

        }

        initializeLocationManager();
    }

    protected void initializeLocationListener() {
            this.locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    locationListenerHelper(location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                    switch (i) {
                        case AVAILABLE:
                            break;

                        case TEMPORARILY_UNAVAILABLE:
                            break;

                        case OUT_OF_SERVICE:
                            break;
                    }
                }

                @Override
                public void onProviderEnabled(String s) { }

                @Override
                public void onProviderDisabled(String s) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }

            };
    }

    protected void requestLocationUpdates() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, this.REFRESH_RATE, 0, this.locationListener);
    }

    protected void removeLocationUpdates() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        this.locationManager.removeUpdates(this.locationListener);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY; //Service got to be killed manually!
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(locationManager != null) removeLocationUpdates();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeLocationManager();
        initializeLocationListener();
        requestLocationUpdates();

    }
}
