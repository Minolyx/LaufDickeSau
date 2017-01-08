package seminar.wahlpflicht.android.hsnr.de.laufdickesau;

import android.os.Handler;
import android.widget.TextView;
import android.view.View;

/**
 * Created by Mino on 08.01.2017.
 */

public class Timer {
    TextView timerTextView;
    long startTime = 0;
    long stopTime = 0;
    private static Timer timer = null;
    private MainActivity mainActivity = null;
    private boolean isPaused = false;


    private Timer(MainActivity that) {
        this.mainActivity = that;
    }

    static protected Timer initTimer(MainActivity that){
        if (timer == null) return new Timer(that);
        return timer;
    }


    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    public void startTime(){
        if (isPaused){
            timerRunnable.run();
            isPaused = false;
        }else{
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
        }

    }

    public void stopTime(){
        stopTime = System.currentTimeMillis();
        timerHandler.removeCallbacks(timerRunnable);
        isPaused = true;
    }




}
