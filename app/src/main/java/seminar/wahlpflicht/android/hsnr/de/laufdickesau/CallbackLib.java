package seminar.wahlpflicht.android.hsnr.de.laufdickesau;


import android.util.Log;

final public class CallbackLib {

    private static MainActivity mainActivity = null;
    private static Polyline polyline =  null;
    private static GPS gps = null;

    public static void initCallbackLib(MainActivity mainActivity) {
        CallbackLib.mainActivity = mainActivity;
        CallbackLib.gps = GPS.initGPSService(mainActivity);
    }


    protected static void gpsCallback() {

//TODO: Call your methods over here ################################################################
        polyline = Polyline.initPolyline(mainActivity);

            CallbackLib.addPolyline();



    }
//TODO: declare your methods over here #############################################################

    private static void addPolyline() {
        try{
            polyline.setCurrentPosition(mainActivity.gps.getLatitude(), mainActivity.gps.getLongitude());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
