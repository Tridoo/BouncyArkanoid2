package tridoo.bouncyarkanoid2;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;


public class InstrukcjeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrukcje);
        ustawCzcionke();
        AdView mAdView = (AdView) findViewById(R.id.adViewInstrukcje);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void ustawCzcionke(){
        Typeface fMedium = Typeface.createFromAsset(getAssets(), "Athiti-Medium.ttf");
        Typeface fLight = Typeface.createFromAsset(getAssets(), "Athiti-Light.ttf");
        Typeface fSemiB = Typeface.createFromAsset(getAssets(), "Athiti-SemiBold.ttf");

        ((TextView)findViewById(R.id.tv_how)).setTypeface(fMedium);
        ((TextView)findViewById(R.id.tv_score)).setTypeface(fSemiB);
        ((TextView)findViewById(R.id.tv_0)).setTypeface(fLight);
        ((TextView)findViewById(R.id.tv_target)).setTypeface(fLight);
        ((TextView)findViewById(R.id.tv_mobile)).setTypeface(fLight);
        ((TextView)findViewById(R.id.tv_hit)).setTypeface(fLight);
        ((TextView)findViewById(R.id.tv_dont)).setTypeface(fLight);
        ((TextView)findViewById(R.id.tv_ball)).setTypeface(fLight);
        ((TextView)findViewById(R.id.tv_paddle)).setTypeface(fLight);
    }
}
