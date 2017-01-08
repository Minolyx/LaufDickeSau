package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

final public class CallbackLib {

    private static MainActivity mainActivity = null;
    private static Polyline polyline =  null;

    public static void initCallbackLib(MainActivity mainActivity) { CallbackLib.mainActivity = mainActivity; }


    protected static void gpsCallback() {

//TODO: Call your methods over here ################################################################
        polyline = Polyline.initPolyline(mainActivity);
        CallbackLib.showDebugInfo();
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

    private static void showDebugInfo() {
        mainActivity.textView.setText("\n\n\n\n         Lat: "
                + mainActivity.gps.getLatitude() + "\n\n         Long: "
                + mainActivity.gps.getLongitude() + "\n\n         Alt: "
                + mainActivity.gps.getAltitude() + "\n\n         Acc: "
                + mainActivity.gps.getAccuracy() + "\n\n         Distance: ");
                //+ String.format("%.2fm", polyline.getDistanceTotal()));
    }

}
