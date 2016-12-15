package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import static android.location.LocationProvider.AVAILABLE;
import static android.location.LocationProvider.OUT_OF_SERVICE;
import static android.location.LocationProvider.TEMPORARILY_UNAVAILABLE;

//TODO: Do NOT touch this class!!!! ################################################################

final public class GPS extends FragmentActivity {

    private static GPS gps = null;
    private MainActivity mainActivity = null;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private Location gpsLocation = null;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private double altitude = 0.0;
    private double accuracy = 0.0;
    private int REFRESH_RATE_2000 = 2000;

    private GPS(MainActivity that) {
        this.mainActivity = that;
        new PermissionHandler(that);
        initializeLocationManager();
        initializeLocationListener();
    }

    static protected GPS initGPSService(MainActivity that) {
        if (gps == null) return new GPS(that);
        return gps;
    }

    protected double getLongitude() {
        return this.longitude;
    }
    protected double getLatitude() {
        return this.latitude;
    }
    protected int getAccuracy() { return (int)this.accuracy; }
    protected double getAltitude() { return this.altitude; }

    protected void initializeLocationManager() {
        this.locationManager = (LocationManager) mainActivity.getSystemService(LOCATION_SERVICE);
    }

    protected void initializeLocationListener() {
            this.locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    accuracy = location.getAccuracy();
                    altitude = location.getAltitude();
                    CallbackLib.gpsCallback();

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
                    if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.LOCATION_HARDWARE) != PackageManager.PERMISSION_GRANTED)
                    new AlertDialog.Builder(mainActivity)
                            .setTitle("Gps service request..")
                            .setMessage("Dear fatty, this application requires gps service permission. " +
                                    "Would you like to turn on your mobile phone's gps service now?")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mainActivity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    mainActivity.finish();
                                }
                            }).create().show();
                }

            };
    }

    protected void setRefreshRate(int rate_ms) {
        this.REFRESH_RATE_2000 = rate_ms;
        Toast.makeText(mainActivity, "Refresh rate set to " + this.REFRESH_RATE_2000 + "ms!", Toast.LENGTH_SHORT).show();
    }

    protected void requestLocationUpdates() {
        try {
            if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, this.REFRESH_RATE_2000, 0, this.locationListener);
            else new PermissionHandler(mainActivity);
        }catch (SecurityException se) {

            new AlertDialog.Builder(mainActivity)
                .setTitle("GPS request denied..")
                .setMessage("Dear fatty, this app requires gps service.\n" +
                        " There's no magic in this world thou it won't work without!")
                .setCancelable(false)
                .setPositiveButton("Hmmmm, ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {System.exit(0);
                    }
                });
        }
    }

    protected void removeLocationUpdates() {
        if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        this.locationManager.removeUpdates(this.locationListener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GPS gps = (GPS) o;

        if (!mainActivity.equals(gps.mainActivity)) return false;
        if (!locationManager.equals(gps.locationManager)) return false;
        if (!locationListener.equals(gps.locationListener)) return false;
        return gpsLocation.equals(gps.gpsLocation);

    }

    @Override
    public int hashCode() {
        int result = mainActivity.hashCode();
        result = 31 * result + locationManager.hashCode();
        result = 31 * result + locationListener.hashCode();
        result = 31 * result + gpsLocation.hashCode();
        return result;
    }
}
