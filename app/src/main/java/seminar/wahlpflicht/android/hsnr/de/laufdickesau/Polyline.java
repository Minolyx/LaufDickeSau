package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.renderscript.Double2;
import android.util.Log;


/**
 * Created by Mino on 22.12.2016.
 */

public class Polyline {

    private static Polyline polyline =null;
    protected static LatLng currentPosition = null;
    protected static LatLng previousPosition = null;
    protected double distance = 0;
    protected static double distanceTotal = 0;
    protected static PolylineOptions polylineOpt = null;


    static protected Polyline initPolyline(){
        if(polyline == null) {
            polyline = new Polyline();
            polylineOpt = new PolylineOptions().color(R.color.sky);
        }

        return polyline;
    }

    public LatLng getCurrentPosition(){return currentPosition;}
    public LatLng getPreviousPosition(){return previousPosition;}
    public double getDistanceTotal(){return distanceTotal;}

    public PolylineOptions getPolylineOpt(){
        for(int i = 0; i < this.polylineOpt.getPoints().size(); i++){
            Log.d("polyline", "getPolylineOpt " + i + " " + this.polylineOpt.getPoints().get(i).longitude + " " + this.polylineOpt.getPoints().get(i).latitude);
        }
        return this.polylineOpt;
    }

    protected void setCurrentPosition(double lat, double lng){
        currentPosition = new LatLng(lat, lng);
        addGeoPointToPolyline();
    }


    protected void addGeoPointToPolyline(){
        if(previousPosition == null) {
            polylineOpt.add(currentPosition);

            Log.d("polyline", "added geoPoint. prev == null");
            previousPosition = currentPosition;
        }else{

            if((distance += distance(previousPosition, currentPosition)) > 1){
                polylineOpt.add(currentPosition);
                distanceTotal += distance;

                Log.d("polyline", "added geoPoint. dist > 1");
                previousPosition = currentPosition;
            }
        }
    }

    public static double distance(LatLng prev, LatLng cur) {
        double lat1 = prev.latitude;
        double lng1 = prev.longitude;
        double lat2 = cur.latitude;
        double lng2 = cur.longitude;
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        Log.d("polyline", "inside of distance. dist: " + Double.toString(dist));

        return dist;
    }


}
