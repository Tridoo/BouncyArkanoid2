package tridoo.bouncyarkanoid2;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.w3c.dom.Text;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Typeface fontMedium= Typeface.createFromAsset(getAssets(),"Athiti-Medium.ttf");
        Typeface fontLight= Typeface.createFromAsset(getAssets(),"Athiti-Light.ttf");
        ((TextView)findViewById(R.id.tv_app_name)).setTypeface(fontLight);
        ((TextView)findViewById(R.id.tv_loading)).setTypeface(fontMedium);
        ((TextView)findViewById(R.id.tv_wait)).setTypeface(fontMedium);

        ImageView imageView=(ImageView) findViewById(R.id.img_progress);
        imageView.setBackgroundResource(R.drawable.progress);
        ((AnimationDrawable) imageView.getBackground()).start();

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    try {
                        sleep(SPLASH_TIME_OUT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(SplashActivity.this, MenuActivity.class);
                    startActivity(intent);
                } finally {                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}