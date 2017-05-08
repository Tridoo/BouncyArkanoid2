package tridoo.bouncyarkanoid2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class GraActivity extends Activity  {

    private GraThread graThread;
    private GraWidok graWidok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_gra);

        Typeface fMedium = Typeface.createFromAsset(getAssets(), "Athiti-Medium.ttf");
        ((TextView)findViewById(R.id.wynik)).setTypeface(fMedium);
        ((TextView)findViewById(R.id.status)).setTypeface(fMedium);
        ((TextView)findViewById(R.id.bonus)).setTypeface(fMedium);


        AdView mAdView = (AdView) findViewById(R.id.adViewGra);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        graWidok = (GraWidok)findViewById(R.id.gamearea);
        graWidok.setStatusView((TextView)findViewById(R.id.status));
        graWidok.setWynikView((TextView)findViewById(R.id.wynik));
        graWidok.setBonusView((TextView)findViewById(R.id.bonus));
        graWidok.setBonusZlapanyIkona((ImageView) findViewById(R.id.bonusZlapany));
        graWidok.setBonusProgres((ImageView)findViewById(R.id.bonus_progres));
        graWidok.setLayoutAds((RelativeLayout) findViewById(R.id.gameAds));

        startGame();

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber)
            {
                if(state==TelephonyManager.CALL_STATE_RINGING)
                {
                    graThread.setState(StanGry.PAUSE);
                }
            }
        };
        telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void startGame() {
        graThread = new Gra(graWidok);
        graWidok.setThread(graThread);
        graWidok.getLayoutAds().setVisibility(View.INVISIBLE);
        graThread.setState(StanGry.STARTING);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(graThread.getMode() == StanGry.RUNNING) {
            graThread.setState(StanGry.PAUSE);
        }
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        finish(); //zamyka aktywnosc
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        graWidok.czyszczenie();
        graThread = null;
        graWidok = null;
    }

    @Override
    public void onBackPressed() {
        if (graThread.stan != StanGry.RUNNING) {
            GraActivity.super.onBackPressed();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("PAUSED")
                .setMessage("do you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        GraActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}
