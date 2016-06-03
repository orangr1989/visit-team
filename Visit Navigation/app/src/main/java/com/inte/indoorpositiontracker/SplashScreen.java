package com.inte.indoorpositiontracker;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashScreen extends Activity {
    private static boolean splashLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!splashLoaded) {
            setContentView(R.layout.activity_splash);

            TextView txtView = (TextView)findViewById(R.id.txt_title);

            Typeface roboto = Typeface.createFromAsset(getAssets(),
                    "font/Roboto-Thin.ttf"); //use this.getAssets if you are calling from an Activity
            //txtView.setText("Visit");
            txtView.setTypeface(roboto);

            int secondsDelayed = 10;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //startActivity(new Intent(SplashScreen.this, MapViewActivity.class));
                    finish();
                }
            }, secondsDelayed * 500);

            splashLoaded = true;
        }
        else {
            /*Intent goToMainActivity = new Intent(SplashScreen.this, MapViewActivity.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToMainActivity);*/
            finish();
        }
    }
}