package tridoo.bouncyarkanoid2;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Switch;

public abstract class GraThread extends Thread {

    protected int stan;
    private SurfaceHolder surfaceHolder;
    private Handler uchwyt;
    private Context kontekst;
    public GraWidok graWidok;

    protected int canvasWidth = 1;
    protected int canvasHeight = 1;

    protected long ostatniCzas = 0;
    protected long czasStartGry;
    private long teraz;
    private float roznicaCzasu;

    protected int wynik = 0;
    protected long maxPredkosc = 0;
    protected int bonus = 0;

    private boolean mRun = false;

    public GraThread(GraWidok aGraWidok) {
        graWidok = aGraWidok;

        surfaceHolder = graWidok.getHolder();
        uchwyt = graWidok.getUchwyt();
        kontekst = graWidok.getContext();
    }

    public abstract void ustawPozycjePoczatkowe();

    public abstract void setupBeginning();

    protected abstract void updateGame(float secondsElapsed);

    protected abstract int podajPrzeliczonaPredkosc();

    public void czyszczenie() {
        this.kontekst = null;
        this.graWidok = null;
        this.uchwyt = null;
        this.surfaceHolder = null;
    }

    public void doStart() {
        setupBeginning();
        ostatniCzas = System.currentTimeMillis() + 10;
        setState(StanGry.RUNNING);
        czasStartGry=System.currentTimeMillis();
    }

    @Override
    public void run() {
        Canvas canvasRun;
        while (mRun) {
            canvasRun = null;
            try {
                canvasRun = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    switch (stan){
                        case StanGry.RUNNING:
                            updatePhysics();
                            break;
                        case StanGry.READY:
                            break;
                        case StanGry.STARTING:
                            ustawPozycjePoczatkowe();
                            setState(StanGry.READY);
                            break;
                    }
                    doDraw(canvasRun);
                }
            }
            finally {
                if (canvasRun != null) {
                    if(surfaceHolder != null)
                        surfaceHolder.unlockCanvasAndPost(canvasRun);
                }
            }
        }
    }

    public void setSurfaceSize(int szerokosc, int wysokosc) {
        synchronized (surfaceHolder) {
            canvasWidth = szerokosc;
            canvasHeight = wysokosc;
        }
    }

    protected void doDraw(Canvas canvas) {
        if(canvas == null) return;
        canvas.drawColor(Color.rgb(16,98,153));
    }

    private void updatePhysics() {
        teraz = System.currentTimeMillis();
        updateGame((teraz - ostatniCzas) *0.001f);
        ostatniCzas = teraz;
    }

    public boolean onTouch(MotionEvent e) {
        if(e.getAction() == MotionEvent.ACTION_DOWN) {
            switch (stan){
                case StanGry.LOSE:
                    setWidocznoscReklamy(false);
                    setState(StanGry.STARTING);
                    return true;
                case StanGry.READY:
                    doStart();
                    return true;
                case StanGry.PAUSE:
                    unpause();
                    return true;
            }

            synchronized (surfaceHolder) {
                this.actionOnTouch(e.getRawX(), e.getRawY());
            }

        }
        if(e.getAction() == MotionEvent.ACTION_MOVE){
            synchronized (surfaceHolder) {
                this.actionOnTouch(e.getRawX(), e.getRawY());
            }
        }
        return false;
    }

    protected void actionOnTouch(float x, float y) {    }


    public void pause() {
        synchronized (surfaceHolder) {
            if (stan == StanGry.RUNNING) setState(StanGry.PAUSE);
        }
    }

    public void unpause() {
        synchronized (surfaceHolder) {
            ostatniCzas = System.currentTimeMillis();
        }
        setState(StanGry.READY);
    }

    public void setState(int aStan) {
        synchronized (surfaceHolder) {
            stan = aStan;
            String komunikat="";
            Resources res = kontekst.getResources();
            switch (stan){
                case StanGry.RUNNING:
                    komunikat="";
                    break;
                case StanGry.READY:
                    komunikat = res.getText(R.string.mode_ready).toString();
                    break;
                case StanGry.PAUSE:
                    komunikat = res.getText(R.string.mode_pause).toString();
                    break;
                case StanGry.LOSE:
                    komunikat = res.getText(R.string.mode_lose) + "\n" + "Points: " + getWynik() + "\n" + "Top Speed: " +podajPrzeliczonaPredkosc();
                    break;

            }
                wyslijWiadomoscDoWidoku("komunikat",komunikat);
        }
    }

    public void setSurfaceHolder(SurfaceHolder h) {
        surfaceHolder = h;
    }

    public boolean isRunning() {
        return mRun;
    }

    public void setRunning(boolean running) {
        mRun = running;
    }

    public int getMode() {
        return stan;
    }

    public void setMode(int mMode) {
        stan = mMode;
    }


    public long getWynik() {
        return wynik;
    }

    public long getTopV() {
        return maxPredkosc;
    }

    public void setTopV(long aV) {
        maxPredkosc = aV;
    }

    public void updateWynik(int score) {
        this.setWynik(wynik + score);
    }

    public void zapiszWynik(int aPunkty, int aSpeed) {
        WynikiDAO dao = new WynikiDAO(kontekst);
        dao.zapiszWynik(aPunkty, aSpeed);
    }


    public void setWynik(int aWynik) {
        wynik = aWynik;
        wyslijWiadomoscDoWidoku("wynik",String.valueOf(aWynik));
    }

    public void setBonus(int aBonus) {
        if (aBonus>0) wyslijWiadomoscDoWidoku("bonus",String.valueOf(aBonus));
    }

    public void setBonusSpadajacy(int aBonus){
        wyslijWiadomoscDoWidoku("bonusSpadajacy",String.valueOf(aBonus));
    }

    public void setBonusZlapany(TypBonusa bonus){
        wyslijWiadomoscDoWidoku("bonusZlapany",String.valueOf(bonus));
    }

    public void setWidocznoscReklamy(boolean aWidocznosc) {
        wyslijWiadomoscDoWidoku("widocznoscReklamy", String.valueOf((aWidocznosc) ? 1 : 0));
    }

    public void ukrykBonusProgres(){
        wyslijWiadomoscDoWidoku("schowajBonusProgres", "");
    }

    public void wyslijWiadomoscDoWidoku(String klucz, String wartosc){
        synchronized (surfaceHolder) {
            Message msg = uchwyt.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean(klucz,true);
            b.putString("wartosc", wartosc);
            msg.setData(b);
            uchwyt.sendMessage(msg);
        }
    }
}
