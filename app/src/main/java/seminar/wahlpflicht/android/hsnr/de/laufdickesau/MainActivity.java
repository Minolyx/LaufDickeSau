package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
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
    protected TextView textViewTimer = null;
    protected MainActivity that = this;
    protected Polyline polyline = null;
    protected ThreadOverhaul thr = null;
    protected boolean started = false;
    protected boolean isMapOpen = false;
    protected String timerString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//TODO: Init variables over here ###################################################################

            gps = GPS.initGPSService(this);
            CallbackLib.initCallbackLib(this);
            MapsActivity.initMapsActivity(this);
            polyline = Polyline.initPolyline(this);
            gpsButton = (Button) findViewById(R.id.startButton);
            textView = (TextView) findViewById(R.id.textView);
            textViewTimer = (TextView) findViewById(R.id.timerTextView);
            textViewTimer.setVisibility(View.INVISIBLE);
            textView.setText("\n\n\n\nHold Start/Stop to show map\n");
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

//TODO: Set buttons/views ect. over here ###########################################################

            gpsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        if (gpsButton.getText().equals("Start")) {

                            gps.requestLocationUpdates();

                            if (!started) {
                                that.thr = new ThreadOverhaul("Countdown_10", 1000, "startTimer", new Object() {

                                    private int counter = 3;

                                    public void startTimer() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                that.textView.setTextSize(56);
                                                that.textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                that.textView.setText("\n" + counter);
                                                counter--;

                                                if (counter < 0) {

                                                    that.textView.setTextSize(24);

                                                    textView.setText("\nLat: "
                                                            + gps.getLatitude()
                                                            + "\nLong: "
                                                            + gps.getLongitude()
                                                            + "\nAlt: "
                                                            + gps.getAltitude()
                                                            + "\nAcc: "
                                                            + gps.getAccuracy()
                                                            + "\nDistance: "
                                                            + String.format("%.2fm", polyline.getDistanceTotal()));

                                                    that.thr.getThreadByName("timeElapsed").start();
                                                    textViewTimer.setVisibility(View.VISIBLE);
                                                    that.started = true;
                                                }
                                            }
                                        });

                                    }

                                }, !ThreadOverhaul.REMEMBER, 4);
                                that.thr.start();
                            }

                            new ThreadOverhaul("timeElapsed", 1000, "startTimer", new Object() {

                                private long seconds = 0;
                                private long minutes = 0;
                                private long hours = 0;

                                public void startTimer() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (seconds == 59) {
                                                seconds = 0;
                                                minutes++;
                                                if (minutes == 59) {
                                                    minutes = 0;
                                                    hours++;
                                                }
                                            }

                                            textView.setText("\nLat: "
                                                    + gps.getLatitude()
                                                    + "\nLong: "
                                                    + gps.getLongitude()
                                                    + "\nAlt: "
                                                    + gps.getAltitude()
                                                    + "\nAcc: "
                                                    + gps.getAccuracy()
                                                    + "\nDistance: "
                                                    + String.format("%.2fm", polyline.getDistanceTotal()));
                                            timerString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                                            textViewTimer.setText(timerString);

                                            seconds++;
                                        }
                                    });

                                }

                            }, ThreadOverhaul.REMEMBER, 1000000000);

                            thr.getThreadByName("timeElapsed").wake();
                            gpsButton.setText("Stop");

                        } else {
                            gps.removeLocationUpdates();
                            gpsButton.setText("Start");
                            thr.getThreadByName("timeElapsed").hibernate();
                        }
                    } catch (Exception e) {
                    }

                }
            });

            gpsButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    try {
                        startActivity(new Intent(MainActivity.this, MapsActivity.class));
                        isMapOpen = true;
                    } catch (Exception e) {
                    }
                    return true;

                }
            });
    }

//TODO: Declare State-Callbacks over here ##########################################################

    @Override
    protected void onPause() {
        super.onPause();
        if(!isMapOpen) System.exit(0);
        else isMapOpen = false;
    }
}
