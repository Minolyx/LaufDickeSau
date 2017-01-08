package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static MainActivity mainActivity = null;
    private static Polyline polyline = null;
    private PolylineOptions polylineOpt = new PolylineOptions();
    private ThreadOverhaul thr = null;
    private LatLng lastDrawnPosition = null;
    private boolean refresh = true;
    private boolean noRefresh = false;
    private float zoomLevel = 16.0f;

    public static void initMapsActivity(MainActivity mainActivity) { MapsActivity.mainActivity = mainActivity; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        polyline = Polyline.initPolyline(mainActivity);


        if (polyline.getCurrentPosition() == null){
            Marker hsnrMarker = getDefaultMarker();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hsnrMarker.getPosition(), zoomLevel));
            hsnrMarker.showInfoWindow();
        }else{
            thr = new ThreadOverhaul("map", 5000, "show", new Object() {

                public void show() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(lastDrawnPosition != polyline.getPreviousPosition()) {
                                drawPolyline(refresh);
                                lastDrawnPosition = polyline.getPreviousPosition();
                            }
                        }
                    });

                }

            }, true, 1000000);

            if (mainActivity.gpsButton.getText().equals("Start")){
                drawPolyline(noRefresh);
            }else{
                thr.start();
            }




            //TODO: TEST
            //drawPolyline();
/*
        try{
            drawPolyline();
        }catch (Exception e){
            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
*/
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(thr != null){thr.kill();}


    }

    protected void drawPolyline(boolean running){
        polylineOpt = polyline.getPolylineOpt();
        mMap.clear();

        Marker startMarker = getStartMarker();
        Marker finishMarker = getFinishMarker();

        mMap.addPolyline(polylineOpt);


        if(running){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polyline.getCurrentPosition(), zoomLevel), 2000, null);
        }else {
            mMap.moveCamera(getCamBounds(startMarker, finishMarker));
            finishMarker.showInfoWindow();
        }


    }

    protected Marker getStartMarker(){
        Marker start = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(polylineOpt.getPoints().get(0).latitude, polylineOpt.getPoints().get(0).longitude))
                .title("Start")
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.piggy_start))
                // gif leider nicht möglich ohne methode zu schreiben die ständig marker entfernt und
                // neuen marker setzt mit dem nächsten bild. und so sieht das nicht sehr gut aus.
                // kannst es ja mal einkommentieren dann siehste es
                .icon(BitmapDescriptorFactory.defaultMarker(250))
        );
        return start;
    }
    protected Marker getFinishMarker(){
        Marker finish = mMap.addMarker(new MarkerOptions()
                .position(polyline.getCurrentPosition())
                .title("Finish")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.piggy_run_small))
                //.icon(BitmapDescriptorFactory.defaultMarker(320))
                .snippet(String.format("Distance: %.2fm", polyline.getDistanceTotal()))
        );
        return finish;
    }

    protected Marker getDefaultMarker(){

        Marker hsnr = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(51.31686, 6.57144))
                .title("HSNR")
                .snippet("No GPS available."));

        return hsnr;
    }

    protected CameraUpdate getCamBounds(Marker finish, Marker start){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(start.getPosition());
        builder.include(finish.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        return cu;
    }
}
