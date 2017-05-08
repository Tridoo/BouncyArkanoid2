package tridoo.bouncyarkanoid2;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Typeface fontMedium= Typeface.createFromAsset(getAssets(),"Athiti-Medium.ttf");

        TextView tvGuide=(TextView)findViewById(R.id.tv_guide);
        tvGuide.setTypeface(fontMedium);
        tvGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), InstrukcjeActivity.class);
                startActivity(intent);
            }
        });

        TextView tvScores=(TextView)findViewById(R.id.tv_scores);
        tvScores.setTypeface(fontMedium);
        tvScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WynikiActivity.class);
                startActivity(intent);
            }
        });

        TextView tvPlay=(TextView)findViewById(R.id.tv_play);
        tvScores.setTypeface(fontMedium);
        tvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GraActivity.class);
                startActivity(intent);
            }
        });
    }
}
