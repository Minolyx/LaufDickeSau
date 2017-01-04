package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

final public class CallbackLib {

    private static MainActivity mainActivity = null;
    private static Polyline polyline = null;

    public static void initCallbackLib(MainActivity mainActivity)
    { CallbackLib.mainActivity = mainActivity; }

    protected static void gpsCallback() {

//TODO: Call your methods over here ################################################################

        Log.d("gps", "Coords: " + mainActivity.gps.getLongitude() + " " + mainActivity.gps.getLatitude());

        polyline = Polyline.initPolyline(mainActivity);
        try{
            polyline.setCurrentPosition(mainActivity.gps.getLatitude(), mainActivity.gps.getLongitude());
        }catch (Exception e){
            e.printStackTrace();
        }


    }
//TODO: declare your methods over here #############################################################

    int counter = 0;

}
