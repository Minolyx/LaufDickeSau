package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.util.Log;

final public class CallbackLib {

    private static MainActivity mainActivity = null;
    public static void initCallbackLib(MainActivity mainActivity)
    { CallbackLib.mainActivity = mainActivity; }

    protected static void gpsCallback() {

//TODO: Call your methods over here ################################################################

        Log.d("gps", "Coords: " + mainActivity.gps.getLongitude() + " " + mainActivity.gps.getLatitude());

    }
//TODO: declare your methods over here #############################################################

    int counter = 0;



}
