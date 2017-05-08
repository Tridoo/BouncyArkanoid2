package tridoo.bouncyarkanoid2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

public class GraWidok extends SurfaceView implements SurfaceHolder.Callback {
    private volatile GraThread thread;
    private TextView wynikView;
    private TextView statusView;
    private TextView bonusView;
    private ImageView bonusZlapanyIkona;
    private ImageView bonusProgres;
    private RelativeLayout layoutAds;
    final AlphaAnimation animation1;
    final AlphaAnimation animation2;
    Animation animationBonusBMP;
    HashMap listaBMPBonusow;

    //uchwyt komunikacji GraThread i GraActivity
    private Handler uchwyt;

    public GraWidok(final Context context, AttributeSet attrs) {
        super(context, attrs);

        listaBMPBonusow=podajBonusyBMP();

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(400);

        animation2 = new AlphaAnimation(1.0f, 0.0f);
        animation2.setDuration(500);
        animation2.setStartOffset(200);

        animationBonusBMP = AnimationUtils.loadAnimation(context, R.anim.blink);
        animationBonusBMP.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {            }
            @Override
            public void onAnimationEnd(Animation animation) {
                bonusZlapanyIkona.setImageBitmap(null);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {            }
        });


        //uchwyt wiadomosci z GraThread
        uchwyt = new Handler() {
            @Override
            public void handleMessage(Message m) {
                if (m.getData().getBoolean("wynik")) {
                    ustawWynik(m.getData().getString("wartosc"));
                } else if (m.getData().getBoolean("bonus")) {
                    ustawBonusPredkosci(m.getData().getString("wartosc"));
                } else if (m.getData().getBoolean("bonusSpadajacy")) {
                    ustawBonusSpadajacy(m.getData().getString("wartosc"));
                } else if (m.getData().getBoolean("bonusZlapany")) {
                    ustawIkoneBonusa(m.getData().getString("wartosc"));
                    pokazBonusProgres();
                } else if (m.getData().getBoolean("komunikat")) {
                    ustawKomunikat(m.getData().getString("wartosc"));
                } else if (m.getData().getBoolean("widocznoscReklamy")) {
                    ustawWidocznoscReklamy(m.getData().getString("wartosc"));
                } else if (m.getData().getBoolean("schowajBonusProgres")) {
                    schowajBonusProgres();
                }
            }
        };
    }


    public void czyszczenie() {
        thread.setRunning(false);
        thread.czyszczenie();
        removeCallbacks(thread);
        thread = null;
        setOnTouchListener(null);
        getHolder().removeCallback(this);
    }


    //seters geters
    public GraThread getThread() {
        return thread;
    }

    public void setThread(GraThread aThread) {
        this.thread = aThread;

        setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return thread != null && thread.onTouch(event);
            }
        });
        setClickable(true);
        setFocusable(true);
    }

    public TextView getWynikView() {
        return wynikView;
    }

    public void setWynikView(TextView wynikView) {
        this.wynikView = wynikView;
    }

    public TextView getStatusView() {
        return statusView;
    }

    public void setStatusView(TextView statusView) {
        this.statusView = statusView;
    }

    public TextView getBonusView() {
        return bonusView;
    }

    public void setBonusView(TextView bonusView) {
        this.bonusView = bonusView;
    }

    public ImageView getBonusZlapanyIkona() {
        return bonusZlapanyIkona;
    }

    public void setBonusZlapanyIkona(ImageView bonusZlapanyIV) {
        this.bonusZlapanyIkona = bonusZlapanyIV;
    }

    public ImageView getBonusProgres() {
        return bonusProgres;
    }

    public void setBonusProgres(ImageView bonusProgres) {
        this.bonusProgres = bonusProgres;
    }

    public RelativeLayout getLayoutAds() {
        return layoutAds;
    }

    public void setLayoutAds(RelativeLayout layoutAds) {
        this.layoutAds = layoutAds;
    }

    public Handler getUchwyt() {
        return uchwyt;
    }

    public void setUchwyt(Handler uchwyt) {
        this.uchwyt = uchwyt;
    }

/*
     * funkcje ekranu
	 */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (thread != null) {
            if (!hasWindowFocus)
                thread.pause();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (thread != null) {
            thread.setRunning(true);

            if (thread.getState() == Thread.State.NEW) {
                thread.start();
            } else {
                if (thread.getState() == Thread.State.TERMINATED) {
                    thread = new Gra(this);
                    thread.setRunning(true);
                    thread.start();
                }
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (thread != null) {
            thread.setSurfaceSize(width, height);
        }
    }

    public void surfaceDestroyed(SurfaceHolder arg0) {
        boolean retry = true;
        if (thread != null) {
            thread.setRunning(false);
        }

        while (retry) {
            try {
                if (thread != null) {
                    thread.join();
                }
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }


    private void ustawWynik(String wynik){
        wynikView.setText(wynik);
    }

    private void ustawBonusPredkosci(String bonus){
        bonus="Speed bonus: "+bonus;
        bonusView.setText(bonus);
        bonusView.startAnimation(animation1);
        bonusView.startAnimation(animation2);
    }

    private void ustawBonusSpadajacy(String bonus){
        bonus = "Extra bonus: "+bonus;
        bonusView.setText(bonus);
        bonusView.startAnimation(animation1);
        bonusView.startAnimation(animation2);
    }


    private void ustawIkoneBonusa(String bonus) {
        bonusZlapanyIkona.setImageBitmap((Bitmap)listaBMPBonusow.get(TypBonusa.valueOf(bonus)));
        bonusZlapanyIkona.startAnimation(animationBonusBMP);
    }

    private void ustawKomunikat(String komunikat){
        statusView.setText(komunikat);
    }

    private void ustawWidocznoscReklamy(String widocznosc){
        layoutAds.setVisibility(widocznosc.equals("1") ? VISIBLE : INVISIBLE);
    }

    private HashMap<TypBonusa, Bitmap> podajBonusyBMP(){
        Context context=getContext();
        HashMap<TypBonusa, Bitmap> pMapaBonusow=new HashMap<>();
        pMapaBonusow.put(TypBonusa.BRAK_PKT, BitmapFactory.decodeResource(context.getResources(), R.drawable.x0));
        pMapaBonusow.put(TypBonusa.PODWOJNE_PKT,BitmapFactory.decodeResource(context.getResources(), R.drawable.x2));
        pMapaBonusow.put(TypBonusa.WOLNY_CEL,BitmapFactory.decodeResource(context.getResources(), R.drawable.wolny_cel));
        pMapaBonusow.put(TypBonusa.WOLNA_PILKA,BitmapFactory.decodeResource(context.getResources(), R.drawable.wolna_pilka));
        pMapaBonusow.put(TypBonusa.SUPER_PILKA,BitmapFactory.decodeResource(context.getResources(), R.drawable.super_pilka_z_promieniami));
        pMapaBonusow.put(TypBonusa.GRAWITACJA,BitmapFactory.decodeResource(context.getResources(), R.drawable.strzalka_w_dol));
        pMapaBonusow.put(TypBonusa.DUZY_CEL,BitmapFactory.decodeResource(context.getResources(), R.drawable.duzy_cel));
        pMapaBonusow.put(TypBonusa.DUZA_PALETKA,BitmapFactory.decodeResource(context.getResources(), R.drawable.duza_paletka));
        pMapaBonusow.put(TypBonusa.DUZA_PRZESZKODA,BitmapFactory.decodeResource(context.getResources(), R.drawable.duza_przeszkoda));
        pMapaBonusow.put(TypBonusa.MALY_CEL,BitmapFactory.decodeResource(context.getResources(), R.drawable.maly_cel));
        pMapaBonusow.put(TypBonusa.SZYBKI_CEL,BitmapFactory.decodeResource(context.getResources(), R.drawable.szybki_cel));
        pMapaBonusow.put(TypBonusa.SZYBKA_PILKA,BitmapFactory.decodeResource(context.getResources(), R.drawable.szybka_pilka));
        pMapaBonusow.put(TypBonusa.MALA_PRZESZKODA,BitmapFactory.decodeResource(context.getResources(), R.drawable.mala_przeszkoda));
        pMapaBonusow.put(TypBonusa.MALA_PALETKA,BitmapFactory.decodeResource(context.getResources(), R.drawable.mala_paletka));
        pMapaBonusow.put(TypBonusa.ZNIKAJACY_CEL,BitmapFactory.decodeResource(context.getResources(), R.drawable.znikajacy_cel));
        pMapaBonusow.put(TypBonusa.ZNIKAJACA_PRZESZKODA,BitmapFactory.decodeResource(context.getResources(), R.drawable.znikajaca_przeszkoda));
        pMapaBonusow.put(TypBonusa.ZNIKAJACE_PRZESZKODY,BitmapFactory.decodeResource(context.getResources(), R.drawable.znikajace_przeszkody));
        pMapaBonusow.put(TypBonusa.DUZE_PRZESZKODY,BitmapFactory.decodeResource(context.getResources(), R.drawable.duze_przeszkody));
        pMapaBonusow.put(TypBonusa.MALE_PRZESZKODY,BitmapFactory.decodeResource(context.getResources(), R.drawable.male_przeszkody));
        return pMapaBonusow;
    }

    private void pokazBonusProgres(){
        bonusProgres.setBackgroundResource(R.drawable.bonus_timer);
        AnimationDrawable anim =   ((AnimationDrawable) bonusProgres.getBackground());
        anim.start();
    }
    private void schowajBonusProgres(){
        bonusProgres.setBackgroundResource(0);
    }

}
