package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

//TODO: Declare variables over here ################################################################

    protected GPS gps = null;
    protected Button gpsStartButton = null;
    protected Button gpsStopButton = null;
    protected MainActivity that = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//TODO: Init variables over here ###################################################################

        gps = GPS.initGPSService(this);
        CallbackLib.initCallbackLib(this);
        gpsStartButton = (Button) findViewById(R.id.startButton);
        gpsStopButton = (Button) findViewById(R.id.stopButton);


//TODO: Set buttons/views ect. over here ###########################################################

        gpsStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gps.requestLocationUpdates();

            }
        });

        gpsStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gps.removeLocationUpdates();
                startActivity(new Intent(MainActivity.this, MapsActivity.class));


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
