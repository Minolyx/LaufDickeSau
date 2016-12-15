package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

//TODO: Do NOT touch this class!!!! ################################################################

public class PermissionHandler extends FragmentActivity {

    private MainActivity mainActivity;

    PermissionHandler(MainActivity that) {
        mainActivity = that;
        if(ContextCompat.checkSelfPermission(that, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(that, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 0);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) mainActivity.gps.requestLocationUpdates();
    }
}
