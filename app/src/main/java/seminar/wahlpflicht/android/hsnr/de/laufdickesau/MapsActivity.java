package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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


    public static void initMapsActivity(MainActivity mainActivity) {
        MapsActivity.mainActivity = mainActivity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        polyline = Polyline.initPolyline();


        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        if (polyline.getCurrentPosition() == null){
            Marker hsnrMarker = getDefaultMarker();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hsnrMarker.getPosition(), zoomLevel));
            hsnrMarker.showInfoWindow();
        }else{
            thr = new ThreadOverhaul("mapUpdater", 2000, "show", new Object() {

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

            }, ThreadOverhaul.REMEMBER, 1000000);

            if (mainActivity.gpsButton.getText().equals("Start")){
                drawPolyline(noRefresh);
            }else{
                thr.start();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ThreadOverhaul("mapUpdater", 2000, "show", new Object() {

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

        }, ThreadOverhaul.REMEMBER, 1000000).start();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void drawPolyline(boolean running){
        polylineOpt = polyline.getPolylineOpt();
        mMap.clear();

        Marker startMarker = getStartMarker();

        if(polylineOpt.getPoints().size() == 1){
            mMap.addPolyline(polylineOpt);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(polylineOpt.getPoints().get(0).latitude, polylineOpt.getPoints().get(0).longitude), zoomLevel), 2000, null);
        }else{

            Marker finishMarker = getFinishMarker();
            mMap.addPolyline(polylineOpt);


            if(running){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polyline.getCurrentPosition(), zoomLevel), 2000, null);
            }else {
                mMap.moveCamera(getCamBounds(startMarker, finishMarker));
                finishMarker.showInfoWindow();
            }
        }
    }

    protected Marker getStartMarker(){
        Marker start = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(polylineOpt.getPoints().get(0).latitude, polylineOpt.getPoints().get(0).longitude))
                .title("Start")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.piggy_start_small))
        );
        return start;
    }
    protected Marker getFinishMarker(){
        Marker finish = mMap.addMarker(new MarkerOptions()
                .position(polyline.getCurrentPosition())
                .title("Current Position")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.piggy_run_small))
                .snippet("Time: " + mainActivity.timerString + "\n" + String.format("Distance: %.2fm", polyline.getDistanceTotal()))
        );
        return finish;
    }

    protected Marker getDefaultMarker(){

        Marker hsnr = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.piggy_start_small))
                .position(new LatLng(51.31686, 6.57144))
                .title("ups...")
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
