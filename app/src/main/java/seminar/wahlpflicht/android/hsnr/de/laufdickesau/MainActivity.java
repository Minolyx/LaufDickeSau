package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;


public class MainActivity extends AppCompatActivity {

//TODO: Declare variables over here ################################################################

    protected GPS gps = null;
    protected Button gpsButton = null;
    protected TextView textView = null;
    protected MainActivity that = this;
    protected Polyline polyline = null;
    protected Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//TODO: Init variables over here ###################################################################

        gps = GPS.initGPSService(this);
        CallbackLib.initCallbackLib(this);
        MapsActivity.initMapsActivity(this);
        polyline = Polyline.initPolyline(this);
        timer = Timer.initTimer(this);
        gpsButton = (Button) findViewById(R.id.startButton);
        textView = (TextView) findViewById(R.id.textView);
        timer.timerTextView = (TextView) findViewById(R.id.timerTextView);
        textView.setText(" \n\n\n\n\t\t              Hold Start/Stop for map display\n");

//TODO: Set buttons/views ect. over here ###########################################################

        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (gpsButton.getText().equals("Start")) {
                        gps.requestLocationUpdates();
                        timer.startTime();
                        gpsButton.setText("Stop");
                    } else {
                        gps.removeLocationUpdates();
                        timer.stopTime();
                        gpsButton.setText("Start");
                    }
                } catch (Exception e) {}

                textView.setText("\n\n         Lat: " + gps.getLatitude() + "\n\n         Long: " + gps.getLongitude() + "\n\n         Alt: " + gps.getAltitude() + "\n\n         Acc: " + gps.getAccuracy() + "\n\n         Distance: " + String.format("%.2fm", polyline.getDistanceTotal()));

            }
        });

        gpsButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                } catch (Exception e) {}
                return true;

            }
        });
    }

//TODO: Declare State-Callbacks over here ##########################################################

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
