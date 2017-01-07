package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static MainActivity mainActivity = null;
    private static Polyline polyline = null;
    private PolylineOptions polylineOpt = new PolylineOptions();
    private ThreadOverhaul thr = null;

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

        thr = new ThreadOverhaul("map", 5000, "show", new Object() {

            public void show() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawPolyline();
                    }
                });

            }

        }, true, 1000000);

        thr.start();


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

    @Override
    protected void onPause() {
        super.onPause();
        thr.kill();

    }

    protected void drawPolyline(){
        polylineOpt = polyline.getPolylineOpt();
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
            .position(new LatLng(polylineOpt.getPoints().get(0).latitude, polylineOpt.getPoints().get(0).longitude))
            .title("Start")
            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.piggy_start))
                // gif leider nicht möglich ohne methode zu schreiben die ständig marker entfernt und
                // neuen marker setzt mit dem nächsten bild. und so sieht das nicht sehr gut aus.
                // kannst es ja mal einkommentieren dann siehste es
           .icon(BitmapDescriptorFactory.defaultMarker(250))
        );


        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(polyline.getCurrentPosition().latitude, polyline.getCurrentPosition().longitude))
                .title("Finish")
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.piggy_run))
                .icon(BitmapDescriptorFactory.defaultMarker(320))
        );

        mMap.addPolyline(polylineOpt);

        Log.d("MapsActivity", "currentPost: " + polyline.getCurrentPosition());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(polyline.getCurrentPosition()));
    }
}
