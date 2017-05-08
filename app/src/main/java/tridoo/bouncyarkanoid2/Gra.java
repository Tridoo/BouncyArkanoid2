package tridoo.bouncyarkanoid2;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.random;

public class Gra extends GraThread {
    Bitmap pilkaBMP, paletkaBMP, celBMP, celAktywnyBMP, aktywatorDodatniBMP,aktywatorUjemnyBMP, superPilkaBMP, bonusSpadajacyBMP,bonusSpadajacyUjemnyBMP;
    int pilkaRozmiar, paletkaRozmiar, celRozmiar,  przeszkodaRozmiarOrg, aktywatorRozmiar, bonusSpadajacyRozmiar;
    float pilkaX, pilkaY, pilkaVX, pilkaVY;
    Bitmap przeszkodaBMP[] =new Bitmap[3];
    Bitmap przeszkodaAktywnaBMP[] =new Bitmap[3];
    int przeszkodaRozmiar[]= new int[3];
    float przeszkodaX[] = new float[3];
    float przeszkodaY[] = new float[3];
    float przeszkodaVX[] = new float[3];
    float celX, celY, celVX, oldCelVX;
    float paletkaX, paletkaY, paletkaVX;
    float aktywatorX, aktywatorY;
    float superPilkaX, superPilkaY, superPilkaVX, superPilkaVY;
    float bonusSpadajacyX, bonusSpadajacyY, bonusSpadajacyVY;
    float bonusSpadajacyUjemnyX, bonusSpadajacyUjemnyY, bonusSpadajacyUjemnyVY;
    int paletkaKierunek;

    float dotykX;
    int maxV;
    int predkoscPoczatkowa;
    float pilkaOldV; //kwadratpredkosci


    float tlumienie=0.8f;
    float skalaBonusow=1.25f;
    int PKT_SPADAJACY_BONUS=10;
    float timer;

    long czasAktywnosciBonusa=7;
    long czasMiedzyBonusami=10;
    long czasDostepnosciBonusa=7;
    long czasStart;
    //long czasStartGry;

    Map<TypBonusa, Long> listaBonusow;
    TypBonusa bonusOczekujacy;
    int nrZmienionejPrzeszkody;
    float mnoznikPredkosciPilki;
    float mnoznikPredkosciCelu;
    boolean widocznoscCelu;
    boolean widocznoscPrzeszkody[]=new boolean[3];
    boolean widocznoscAktywatorDodatni;
    boolean widocznoscAktywatorUjemny;
    boolean podwojnePKT;
    boolean brakPKT;
    boolean grawitacja;
    boolean superPilkaAktywna;
    boolean animowanyCel;
    boolean bonusSpadajacyWidoczny;
    boolean bonusSpadajacyUjemnyWidoczny;
    boolean animowanaPrzeszkoda[] = new boolean[3];

    Paint alphaCelINPaint;
    Paint alphaCelOUTPaint;
    Paint alphaPrzeszkodaINPaint[];
    Paint alphaPrzeszkodaOUTPaint[];
    Paint alpha20;


    public Gra(GraWidok graWidok) {
        super(graWidok);

        paletkaBMP = BitmapFactory.decodeResource(graWidok.getContext().getResources(), R.drawable.yellow_ball);
        pilkaBMP = BitmapFactory.decodeResource(graWidok.getContext().getResources(), R.drawable.gray_ball);
        celBMP = BitmapFactory.decodeResource(graWidok.getContext().getResources(), R.drawable.green_ball);
        celAktywnyBMP=BitmapFactory.decodeResource(graWidok.getContext().getResources(), R.drawable.green_ball2);
        for (int i = 0; i < 3; i++) {
            przeszkodaBMP[i] = BitmapFactory.decodeResource(graWidok.getContext().getResources(), R.drawable.red_ball);
            przeszkodaAktywnaBMP[i] = BitmapFactory.decodeResource(graWidok.getContext().getResources(), R.drawable.red_ball2);
            przeszkodaRozmiar[i]=(int)(przeszkodaBMP[i].getHeight()*0.5);
            widocznoscPrzeszkody[i]=true;
            animowanaPrzeszkoda[i]=false;
        }
        przeszkodaRozmiarOrg=przeszkodaRozmiar[0];
        aktywatorDodatniBMP = BitmapFactory.decodeResource(graWidok.getContext().getResources(), R.drawable.kulka_pomarancz);
        aktywatorUjemnyBMP = BitmapFactory.decodeResource(graWidok.getContext().getResources(), R.drawable.kulka_fiolet);
        superPilkaBMP = BitmapFactory.decodeResource(graWidok.getContext().getResources(), R.drawable.super_ball);
        bonusSpadajacyBMP=BitmapFactory.decodeResource(graWidok.getContext().getResources(), R.drawable.star_ball);
        bonusSpadajacyUjemnyBMP=BitmapFactory.decodeResource(graWidok.getContext().getResources(), R.drawable.bomb_ball);

        pilkaRozmiar = (int)(pilkaBMP.getHeight()*0.5);
        paletkaRozmiar=(int)(paletkaBMP.getHeight()*0.5);
        celRozmiar=(int)(celBMP.getHeight()*0.5);
        aktywatorRozmiar=(int)(aktywatorDodatniBMP.getHeight()*0.5);
        bonusSpadajacyRozmiar=(int) (bonusSpadajacyBMP.getHeight()*0.5f);

        listaBonusow=new HashMap<>();
    }


    @Override
    public void ustawPozycjePoczatkowe() {
        pilkaX = canvasWidth * 0.5f;
        pilkaY = canvasHeight * 0.35f;

        paletkaY = canvasHeight - paletkaBMP.getHeight() * 1.1f;
        paletkaX = canvasWidth * 0.5f;

        celX = canvasWidth * 0.5f;
        celY = celBMP.getHeight() * 0.65f + canvasHeight * 0.01f;

        for (int i = 0; i < 3; i++) przeszkodaVX[i] = 0;
        przeszkodaX[0] = canvasWidth * 0.3f;
        przeszkodaX[1] = canvasWidth * 0.7f;
        przeszkodaX[2] = canvasWidth * 0.5f;

        przeszkodaY[0] = canvasHeight * 0.35f;
        przeszkodaY[1] = canvasHeight * 0.35f;
        przeszkodaY[2] = canvasHeight * 0.2f;

        pilkaVX = 0;
        pilkaVY = 0;

        aktywatorX=-canvasHeight;

        celVX = 0;
        paletkaVX = canvasWidth * 1.8f;
        paletkaKierunek = 0;
        dotykX = canvasWidth * 0.5f;
        setTopV(0);
        setWynik(0);

        zerujBonusy();

        alphaCelOUTPaint=new Paint();
        alphaCelOUTPaint.setAlpha(255);
        alphaCelINPaint=new Paint();
        alphaCelINPaint.setAlpha(0);

        alphaPrzeszkodaINPaint = new Paint[3];
        alphaPrzeszkodaOUTPaint = new Paint[3];
        for (int i=0;i<3;i++){
            alphaPrzeszkodaINPaint[i]=new Paint();
            alphaPrzeszkodaOUTPaint[i]= new Paint();
            alphaPrzeszkodaINPaint[i].setAlpha(0);
            alphaPrzeszkodaOUTPaint[i].setAlpha(255);
        }

        alpha20 =new Paint();
        alpha20.setAlpha(25);

    }

    private void zerujBonusy() {
        if (listaBonusow.size() > 0) {
            Iterator it = listaBonusow.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                    switch ((TypBonusa) pair.getKey()){
                        case DUZY_CEL:
                        case MALY_CEL:
                        case MALA_PRZESZKODA:
                        case DUZA_PRZESZKODA:
                        case MALE_PRZESZKODY:
                        case DUZE_PRZESZKODY:
                        case DUZA_PALETKA:
                        case MALA_PALETKA:
                            zdejmijBonus((TypBonusa) pair.getKey());
                            break;
                }
                it.remove();
            }
        }
        bonusOczekujacy=null;
        mnoznikPredkosciPilki=1;
        mnoznikPredkosciCelu=1f;
        widocznoscCelu=true;
        widocznoscAktywatorDodatni=false;
        widocznoscAktywatorUjemny=false;
        podwojnePKT=false;
        brakPKT=false;
        grawitacja=false;
        superPilkaAktywna=false;
        for (int i = 0; i < 3; i++) {
            widocznoscPrzeszkody[i] = true;
            animowanaPrzeszkoda[i]=false;
        }
        animowanyCel=false;
        ukrykBonusProgres();
    }

    @Override
    public void setupBeginning() {
        //losowanie predkosci poczatkowej pilki
        maxV = (int) (canvasHeight * 0.5f);
        int pMinV = (int) (maxV * 0.6f);
        predkoscPoczatkowa=maxV;

        Random r = new Random();
        int x = r.nextInt(maxV) - pMinV;
        int y = (int) Math.sqrt(maxV * maxV - x * x);
        pilkaVX = x;
        pilkaVY = y;
        timer=0;
        bonusSpadajacyVY=maxV;
        bonusSpadajacyUjemnyVY=maxV;

        bonusSpadajacyWidoczny=false;
        bonusSpadajacyUjemnyWidoczny=false;
    }

    @Override
    protected void doDraw(Canvas canvas) {
        if (canvas == null) return;
        super.doDraw(canvas);

        canvas.drawBitmap(pilkaBMP, pilkaX - pilkaRozmiar, pilkaY - pilkaRozmiar, null);
        canvas.drawBitmap(paletkaBMP, paletkaX - paletkaRozmiar, paletkaY - paletkaRozmiar, null);

        if (widocznoscCelu) {
            if (!animowanyCel) canvas.drawBitmap(celBMP, celX - celRozmiar, celY - celRozmiar, null);
            else {
                canvas.drawBitmap(celBMP, celX - celRozmiar, celY - celRozmiar, alphaCelOUTPaint);
                canvas.drawBitmap(celAktywnyBMP, celX - celRozmiar, celY - celRozmiar, alphaCelINPaint);
            }
        }
        else {
            canvas.drawBitmap(celBMP, celX - celRozmiar, celY - celRozmiar, alpha20);
        }

        for (int i = 0; i < 3; i++) {
            if (widocznoscPrzeszkody[i]) {
                if (!animowanaPrzeszkoda[i])
                    canvas.drawBitmap(przeszkodaBMP[i], przeszkodaX[i] - przeszkodaRozmiar[i], przeszkodaY[i] - przeszkodaRozmiar[i], null);
                else{
                    canvas.drawBitmap(przeszkodaBMP[i], przeszkodaX[i] - przeszkodaRozmiar[i], przeszkodaY[i] - przeszkodaRozmiar[i], alphaPrzeszkodaOUTPaint[i]);
                    canvas.drawBitmap(przeszkodaAktywnaBMP[i], przeszkodaX[i] - przeszkodaRozmiar[i], przeszkodaY[i] - przeszkodaRozmiar[i], alphaPrzeszkodaINPaint[i]);
                }
            }
            else{
                canvas.drawBitmap(przeszkodaBMP[i], przeszkodaX[i] - przeszkodaRozmiar[i], przeszkodaY[i] - przeszkodaRozmiar[i], alpha20);
            }
        }

        if (widocznoscAktywatorDodatni) canvas.drawBitmap(aktywatorDodatniBMP, aktywatorX - aktywatorRozmiar, aktywatorY - aktywatorRozmiar, null);
        if (widocznoscAktywatorUjemny) canvas.drawBitmap(aktywatorUjemnyBMP, aktywatorX - aktywatorRozmiar, aktywatorY - aktywatorRozmiar,null);
        if (superPilkaAktywna)  canvas.drawBitmap(superPilkaBMP, superPilkaX - pilkaRozmiar, superPilkaY - pilkaRozmiar,null);
        if (bonusSpadajacyWidoczny) canvas.drawBitmap(bonusSpadajacyBMP, bonusSpadajacyX - bonusSpadajacyRozmiar, bonusSpadajacyY - bonusSpadajacyRozmiar, null);
        if (bonusSpadajacyUjemnyWidoczny) canvas.drawBitmap(bonusSpadajacyUjemnyBMP, bonusSpadajacyUjemnyX - bonusSpadajacyRozmiar, bonusSpadajacyUjemnyY - bonusSpadajacyRozmiar, null);
    }


    @Override
    protected void updateGame(float secondsElapsed) {
        boolean pCzyBylaKolizja=false;
        timer+=secondsElapsed;
        //sprawdzic aktywne bonusy ewen wylaczyc
        long pCzasAktualny = System.currentTimeMillis();
        if (listaBonusow.size() > 0) {
            Iterator it = listaBonusow.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if ((long) pair.getValue() < pCzasAktualny) {
                    zdejmijBonus((TypBonusa) pair.getKey());
                    it.remove();
                    //Log.e("bonus","zdjecie dzialania bonusa");
                }
            }
        }

        //nowa pozycja pilki
        if (grawitacja) pilkaVY = pilkaVY + secondsElapsed * canvasHeight*0.5f;
        pilkaX = pilkaX + secondsElapsed * pilkaVX*mnoznikPredkosciPilki;
        pilkaY = pilkaY + secondsElapsed * pilkaVY*mnoznikPredkosciPilki;


        //czy prrzegrana
        if(pilkaY+pilkaRozmiar > paletkaY + paletkaRozmiar) {
            setState(StanGry.LOSE);
            setWidocznoscReklamy(true);
            animowanyCel=false;
            if (getWynik()>0) zapiszWynik((int)getWynik(), podajPrzeliczonaPredkosc());
            return;
        }

        //nowa pozycje celu i przeszkod
        if (widocznoscCelu) celX = celX + secondsElapsed * celVX * mnoznikPredkosciCelu;
        for (int i=0;i<3;i++){
            if (widocznoscPrzeszkody[i]) przeszkodaX[i]=przeszkodaX[i]+secondsElapsed*przeszkodaVX[i];
        }

        //kolizje ze scianami i przeszkody miedzy soba
        if ((celX < celRozmiar && celVX < 0) || (celX > canvasWidth - celRozmiar && celVX > 0)) {
            celVX = -celVX * tlumienie;
        }
        if ((pilkaX < pilkaRozmiar && pilkaVX < 0) || (pilkaX > canvasWidth - pilkaRozmiar && pilkaVX > 0)) {
            pilkaVX = -pilkaVX;
            pCzyBylaKolizja=true;
        }
        if (pilkaY < pilkaRozmiar && pilkaVY < 0) {
            pilkaVY = -pilkaVY;
        }
        for (int i=0;i<3;i++){
            if (!widocznoscPrzeszkody[i]) continue;
            if((przeszkodaX[i] < przeszkodaRozmiar[i] && przeszkodaVX[i] < 0) || (przeszkodaX[i] > canvasWidth - przeszkodaRozmiar[i] && przeszkodaVX[i] > 0) ) {
                przeszkodaVX[i] = -przeszkodaVX[i] * tlumienie;
            }
        }

        if (widocznoscPrzeszkody[0] && widocznoscPrzeszkody[1] && abs(przeszkodaX[0] - przeszkodaX[1]) < przeszkodaRozmiar[0] + przeszkodaRozmiar[1]) {
            float pTmp = przeszkodaVX[0];
            przeszkodaVX[0] = przeszkodaVX[1];
            przeszkodaVX[1] = pTmp;

            if (przeszkodaVX[0] * przeszkodaVX[1] > 0) {
                float delta = (przeszkodaRozmiar[0] + przeszkodaRozmiar[1] - abs(przeszkodaX[0] - przeszkodaX[1]));
                if (przeszkodaX[0] < przeszkodaX[1]) {
                    przeszkodaX[0] -= delta * 0.51;
                    przeszkodaX[1] += delta * 0.51;
                } else{
                    przeszkodaX[0] += delta * 0.51;
                    przeszkodaX[1] -= delta * 0.51;
                }
                //Log.e("bonus","nachodzenie");
            }
        }

        //ruch paletki
        if (paletkaX < dotykX) {
            paletkaKierunek = 1;
        } else if (paletkaX > dotykX) {
            paletkaKierunek = -1;
        }
        if (abs(paletkaX - dotykX) < secondsElapsed * paletkaVX) paletkaKierunek = 0;

        paletkaX = paletkaX + secondsElapsed * paletkaVX * paletkaKierunek;
        if (paletkaX < paletkaRozmiar) paletkaX = paletkaRozmiar;
        if (paletkaX > canvasWidth - paletkaRozmiar) paletkaX = canvasWidth - paletkaRozmiar;

        //kolizje z pilka
        for (int i =0 ; i<3;i++){
            if (widocznoscPrzeszkody[i] && sprawdzTrafieniePilka(przeszkodaX[i], przeszkodaY[i], przeszkodaRozmiar[i],true)){
                pCzyBylaKolizja=true;

                if (animowanaPrzeszkoda[0] && animowanaPrzeszkoda[1] && animowanaPrzeszkoda[2]) {
                    bonusSpadajacyUjemnyWidoczny = true;
                    bonusSpadajacyUjemnyX = przeszkodaX[i];
                    bonusSpadajacyUjemnyY = przeszkodaY[i];
                    for (int i2 = 0; i2 < 3; i2++) animowanaPrzeszkoda[i2] = false;
                }

                if (!animowanaPrzeszkoda[i] && !bonusSpadajacyUjemnyWidoczny && czyAktywowac(Math.abs(pilkaOldV))) {
                    animowanaPrzeszkoda[i] = true;
                }

                float pAvg;
                pilkaVX = -pilkaVX;
                pAvg = (pilkaVX + przeszkodaVX[i])*0.5f;
                pilkaVX = pAvg-pilkaVX;
                przeszkodaVX[i] = pAvg-przeszkodaVX[i];
            }
        }

        if (widocznoscCelu && sprawdzTrafieniePilka(celX, celY, celRozmiar, true)) {
            pCzyBylaKolizja = true;
            float pAvg;
            oldCelVX = celVX;
            pilkaVX = -pilkaVX;
            pAvg = (pilkaVX + celVX) * 0.5f;
            pilkaVX = pAvg - pilkaVX;
            celVX = pAvg - celVX;

            if (!brakPKT) {
                int pkt = policzPunkty();
                if (podwojnePKT) pkt = pkt * pkt;
                setBonus(pkt);
                updateWynik(pkt + 1);
            }

            if (animowanyCel) {
                bonusSpadajacyWidoczny = true;
                bonusSpadajacyX = celX;
                bonusSpadajacyY = celY;
                animowanyCel=false;
                czasStart = System.currentTimeMillis();
            }

            if (!animowanyCel && !bonusSpadajacyWidoczny && czyAktywowac(Math.abs(pilkaOldV))) {
                    animowanyCel = true;
            }
        }

        if (sprawdzTrafieniePilka(paletkaX, paletkaY, paletkaRozmiar,true)){
            pCzyBylaKolizja=true;
            dodajPredkosc();
        }

        //superpilka
        if (superPilkaAktywna) {
            superPilkaX = superPilkaX + secondsElapsed * superPilkaVX;
            superPilkaY = superPilkaY + secondsElapsed * superPilkaVY;

            if ((superPilkaX < pilkaRozmiar && superPilkaX < 0) || (superPilkaX > canvasWidth - pilkaRozmiar && superPilkaVX > 0)) {
                superPilkaVX = -superPilkaVX;
            }
            if (superPilkaY + pilkaRozmiar > paletkaY + paletkaRozmiar) {
                superPilkaAktywna = false;
            }
            if (superPilkaY < pilkaRozmiar && superPilkaVY < 0) superPilkaVY = -superPilkaVY;

            if (sprawdzTrafieniePilka(superPilkaX, superPilkaY, pilkaRozmiar,true)) {
                pCzyBylaKolizja = true;
            }

            if (sprawdzTrafienieSuperPilka(celX,celY, celRozmiar)){
                int pkt=(int)(wynik*0.5)+10;
                setBonus(pkt);
                updateWynik(pkt + 1);
                celVX = - celVX;
            }
            sprawdzTrafienieSuperPilka(paletkaX, paletkaY, paletkaRozmiar);
        }

        //minimalna predkosc pilki jesli byla kolizja
        if (pCzyBylaKolizja) ustawMinV();

        //zarzadzanie aktywatorow bonusa
        if (timer>czasMiedzyBonusami){
            timer=timer-czasMiedzyBonusami;
            Point punkt=losujPozycjaAktywatora();
            aktywatorX=punkt.x;
            aktywatorY=punkt.y;
            bonusOczekujacy=losujBonus();
            if(bonusOczekujacy.isCzyDodatni()) widocznoscAktywatorDodatni=true;
            else widocznoscAktywatorUjemny=true;
            //Log.e("bonus","dodanie aktywatora");
        }

        if (bonusOczekujacy!=null){
            if (timer>czasDostepnosciBonusa){
//                Log.e("bonus","zdjecie aktywatora");
                if (bonusOczekujacy.isCzyDodatni()) widocznoscAktywatorDodatni=false;
                else widocznoscAktywatorUjemny=false;
                bonusOczekujacy=null;
            } else if (sprawdzTrafieniePilka(aktywatorX, aktywatorY, aktywatorRozmiar, false)) {
                dodajBonus(bonusOczekujacy);
                listaBonusow.put(bonusOczekujacy,System.currentTimeMillis()+czasAktywnosciBonusa*1000);
                if (bonusOczekujacy.isCzyDodatni()) widocznoscAktywatorDodatni=false;
                else widocznoscAktywatorUjemny=false;
                setBonusZlapany(bonusOczekujacy);
                bonusOczekujacy=null;
                //Log.e("bonus","trafienie aktywatora");
            }
        }

        //bonus spadajacy
        if (bonusSpadajacyWidoczny) {
            bonusSpadajacyY=bonusSpadajacyY+secondsElapsed*bonusSpadajacyVY;
            if (bonusSpadajacyY > canvasHeight) bonusSpadajacyWidoczny = false;

            float pOdleglosc = (paletkaX - bonusSpadajacyX) * (paletkaX - bonusSpadajacyX) + (paletkaY - bonusSpadajacyY) * (paletkaY - bonusSpadajacyY);
            float pSumaPromieni = (paletkaRozmiar + bonusSpadajacyRozmiar) * (paletkaRozmiar + bonusSpadajacyRozmiar);
            if (pOdleglosc < pSumaPromieni){
                setBonusSpadajacy(PKT_SPADAJACY_BONUS);
                updateWynik(PKT_SPADAJACY_BONUS);
                bonusSpadajacyWidoczny=false;
            }
        }

        if (bonusSpadajacyUjemnyWidoczny) {
            bonusSpadajacyUjemnyY=bonusSpadajacyUjemnyY+secondsElapsed*bonusSpadajacyUjemnyVY;
            if (bonusSpadajacyUjemnyY > canvasHeight) bonusSpadajacyUjemnyWidoczny = false;

            float pOdleglosc = (paletkaX - bonusSpadajacyUjemnyX) * (paletkaX - bonusSpadajacyUjemnyX) + (paletkaY - bonusSpadajacyUjemnyY) * (paletkaY - bonusSpadajacyUjemnyY);
            float pSumaPromieni = (paletkaRozmiar + bonusSpadajacyRozmiar) * (paletkaRozmiar + bonusSpadajacyRozmiar);
            if (pOdleglosc < pSumaPromieni){
                if (wynik>PKT_SPADAJACY_BONUS) {
                    setBonusSpadajacy(-PKT_SPADAJACY_BONUS);
                    updateWynik(-PKT_SPADAJACY_BONUS);
                }
                else {
                    setBonusSpadajacy(-wynik);
                    updateWynik(-wynik);
                }
                bonusSpadajacyUjemnyWidoczny=false;
            }
        }

        // aplhy do animacji
        if (animowanyCel) {
            int alpha = 127 + (int) (127 * Math.cos(((pCzasAktualny - czasStart) * 0.01)));
            alphaCelINPaint.setAlpha(255 - alpha);
            alphaCelOUTPaint.setAlpha(alpha);
        }

        for (int i=0; i<3;i++){
            if (animowanaPrzeszkoda[i]){
                int alpha = 127 + (int) (127 * Math.cos(((pCzasAktualny - czasStart) * 0.01)));
                alphaPrzeszkodaINPaint[i].setAlpha(255 - alpha);
                alphaPrzeszkodaOUTPaint[i].setAlpha(alpha);
            }
        }
    }

    public boolean sprawdzTrafieniePilka(float x, float y, float r, boolean aCzyOdbicie) {
        float pOdleglosc = (x - pilkaX) * (x - pilkaX) + (y - pilkaY) * (y - pilkaY);
        float pSumaPromieni = (pilkaRozmiar + r) * (pilkaRozmiar + r);

        if (pOdleglosc < pSumaPromieni) {
            if (aCzyOdbicie) {
                pilkaOldV = (pilkaVX * pilkaVX + pilkaVY * pilkaVY);
                pilkaVX = pilkaX - x;
                pilkaVY = pilkaY - y;
                float pNowaV = (pilkaVX * pilkaVX + pilkaVY * pilkaVY);
                float pWspolczynnik = (float) Math.sqrt(pilkaOldV / pNowaV);
                pilkaVX = pilkaVX * pWspolczynnik;
                pilkaVY = pilkaVY * pWspolczynnik;
            }
            return true;
        }
        return false;
    }

    public boolean sprawdzTrafienieSuperPilka(float x, float y, float r) {
        float pOdleglosc = (x - superPilkaX) * (x - superPilkaX) + (y - superPilkaY) * (y - superPilkaY);
        float pSumaPromieni = (pilkaRozmiar + r) * (pilkaRozmiar + r);

        if (pOdleglosc < pSumaPromieni) {
            float pOldV = (superPilkaVX * superPilkaVX + superPilkaVY * superPilkaVY);
            superPilkaVX = superPilkaX - x;
            superPilkaVY = superPilkaY - y;
            float pNowaV = (superPilkaVX * superPilkaVX + superPilkaVY * superPilkaVY);
            float pWspolczynnik = (float) Math.sqrt(pOldV / pNowaV);
            superPilkaVX = superPilkaVX * pWspolczynnik;
            superPilkaVY = superPilkaVY * pWspolczynnik;
            return true;
        }
        return false;
    }

    @Override
    protected int podajPrzeliczonaPredkosc() {
        return (int) ((getTopV() * 20) / pilkaBMP.getHeight());
    }

    private void ustawMinV() {
        float pSsekundyDzialania= (System.currentTimeMillis()-czasStartGry)*0.001f;
        float pWspolczynnikCzasu=1+pSsekundyDzialania * 0.002f;
        float pWspolczynnikWyniku=1 + (wynik * 0.002f);
        //Log.e("bonus",String.valueOf(pSsekundyDzialania));

        maxV = (int) (predkoscPoczatkowa * pWspolczynnikWyniku * pWspolczynnikCzasu);
        //Log.e("bonus",String.valueOf(maxV));
        if (pilkaVX * pilkaVX + pilkaVY * pilkaVY < maxV * maxV) {
            float pPredkosc = (float) Math.sqrt(pilkaVX * pilkaVX + pilkaVY * pilkaVY);
            float wspolczynnik = maxV / pPredkosc;
            pilkaVX = pilkaVX * wspolczynnik;
            pilkaVY = pilkaVY * wspolczynnik;
        }
    }

    @Override
    protected void actionOnTouch(float x, float y) {
        dotykX = x;
    }

    private void dodajBonus(TypBonusa bonus){
        //Log.e("bonus","dodaj bonus "+bonus);
        Random randomGenerator = new Random();
        int nr;
        int rozmiar;
        switch (bonus){
            case MALY_CEL:
                celRozmiar=(int)(celRozmiar/skalaBonusow);
                celBMP=BITMAP_RESIZER(celBMP,(celRozmiar*2),(celRozmiar*2));
                celAktywnyBMP=BITMAP_RESIZER(celAktywnyBMP,celRozmiar*2,celRozmiar*2);
                break;
            case DUZY_CEL:
                celRozmiar=(int)(celRozmiar*skalaBonusow);
                celBMP=BITMAP_RESIZER(celBMP,celRozmiar*2,celRozmiar*2);
                celAktywnyBMP=BITMAP_RESIZER(celAktywnyBMP,celRozmiar*2,celRozmiar*2);
                break;
            case MALA_PRZESZKODA:
                nr = randomGenerator.nextInt(3);
                nrZmienionejPrzeszkody=nr;
                przeszkodaRozmiar[nr]=(int)(przeszkodaRozmiar[nr]/skalaBonusow);
                przeszkodaBMP[nr]=BITMAP_RESIZER(przeszkodaBMP[nr],przeszkodaRozmiar[nr]*2,przeszkodaRozmiar[nr]*2);
                przeszkodaAktywnaBMP[nr]=BITMAP_RESIZER(przeszkodaAktywnaBMP[nr],przeszkodaRozmiar[nr]*2,przeszkodaRozmiar[nr]*2);
                break;
            case DUZA_PRZESZKODA:
                nr = randomGenerator.nextInt(3);
                nrZmienionejPrzeszkody=nr;
                przeszkodaRozmiar[nr]=(int)(przeszkodaRozmiar[nr]*skalaBonusow);
                przeszkodaBMP[nr]=BITMAP_RESIZER(przeszkodaBMP[nr],przeszkodaRozmiar[nr]*2,przeszkodaRozmiar[nr]*2);
                przeszkodaAktywnaBMP[nr]=BITMAP_RESIZER(przeszkodaAktywnaBMP[nr],przeszkodaRozmiar[nr]*2,przeszkodaRozmiar[nr]*2);
                break;
            case MALE_PRZESZKODY:
                rozmiar = (int) (przeszkodaRozmiarOrg / skalaBonusow);
                for (int i = 0; i < 3; i++) {
                    przeszkodaRozmiar[i] = rozmiar;
                    przeszkodaBMP[i] = BITMAP_RESIZER(przeszkodaBMP[i], przeszkodaRozmiar[i] * 2, przeszkodaRozmiar[i] * 2);
                    przeszkodaAktywnaBMP[i]=BITMAP_RESIZER(przeszkodaAktywnaBMP[i],przeszkodaRozmiar[i]*2,przeszkodaRozmiar[i]*2);
                }
                break;
            case DUZE_PRZESZKODY:
                rozmiar = (int) (przeszkodaRozmiarOrg * skalaBonusow);
                for (int i = 0; i < 3; i++) {
                    przeszkodaRozmiar[i] = rozmiar;
                    przeszkodaBMP[i] = BITMAP_RESIZER(przeszkodaBMP[i], przeszkodaRozmiar[i] * 2, przeszkodaRozmiar[i] * 2);
                    przeszkodaAktywnaBMP[i]=BITMAP_RESIZER(przeszkodaAktywnaBMP[i],przeszkodaRozmiar[i]*2,przeszkodaRozmiar[i]*2);
                }
                break;
            case MALA_PALETKA:
                paletkaRozmiar=(int)(paletkaRozmiar/skalaBonusow);
                paletkaBMP=BITMAP_RESIZER(paletkaBMP,(paletkaRozmiar*2),(paletkaRozmiar*2));
                break;
            case DUZA_PALETKA:
                paletkaRozmiar=(int)(paletkaRozmiar*skalaBonusow);
                paletkaBMP=BITMAP_RESIZER(paletkaBMP,(paletkaRozmiar*2),(paletkaRozmiar*2));
                break;
            case SZYBKA_PILKA:
                mnoznikPredkosciPilki=1.33f;
                break;
            case WOLNA_PILKA:
                mnoznikPredkosciPilki=0.75f;
                break;
            case SZYBKI_CEL:
                mnoznikPredkosciCelu=1.33f;
                break;
            case WOLNY_CEL:
                mnoznikPredkosciCelu=0.75f;
                break;
            case ZNIKAJACY_CEL:
                widocznoscCelu=false;
                break;
            case ZNIKAJACA_PRZESZKODA:
                nr = randomGenerator.nextInt(3);
                widocznoscPrzeszkody[nr]=false;
                break;
            case ZNIKAJACE_PRZESZKODY:
                for (int i=0; i<3; i++) {
                    widocznoscPrzeszkody[i] = false;
                }
                break;
            case BRAK_PKT:
                brakPKT=true;
                break;
            case PODWOJNE_PKT:
                podwojnePKT=true;
                break;
            case GRAWITACJA:
                grawitacja=true;
                break;
            case SUPER_PILKA:
                superPilkaAktywna=true;
                superPilkaX=pilkaX;
                superPilkaY=pilkaY;
                superPilkaVX=pilkaVX;
                superPilkaVY=-pilkaVY;
                break;
        }
    }

    private void zdejmijBonus(TypBonusa bonus){
//        Log.e("bonus","zdejmij bonus");
        switch (bonus){
            case MALY_CEL:
                celRozmiar=(int)(celRozmiar*skalaBonusow);
                celBMP=BITMAP_RESIZER(celBMP,celRozmiar*2,celRozmiar*2);
                celAktywnyBMP=BITMAP_RESIZER(celAktywnyBMP,celRozmiar*2,celRozmiar*2);
                break;
            case DUZY_CEL:
                celRozmiar=(int)(celRozmiar/skalaBonusow);
                celBMP=BITMAP_RESIZER(celBMP,(celRozmiar*2),(celRozmiar*2));
                celAktywnyBMP=BITMAP_RESIZER(celAktywnyBMP,celRozmiar*2,celRozmiar*2);
                break;
            case MALA_PRZESZKODA:
            case DUZA_PRZESZKODA:
                przeszkodaRozmiar[nrZmienionejPrzeszkody]=przeszkodaRozmiarOrg;
                przeszkodaBMP[nrZmienionejPrzeszkody]=BITMAP_RESIZER(przeszkodaBMP[nrZmienionejPrzeszkody],przeszkodaRozmiarOrg*2,przeszkodaRozmiarOrg*2);
                przeszkodaAktywnaBMP[nrZmienionejPrzeszkody]=BITMAP_RESIZER(przeszkodaAktywnaBMP[nrZmienionejPrzeszkody],przeszkodaRozmiar[nrZmienionejPrzeszkody]*2,przeszkodaRozmiar[nrZmienionejPrzeszkody]*2);
                break;
            case MALE_PRZESZKODY:
            case DUZE_PRZESZKODY:
                for (int i = 0; i < 3; i++) {
                    przeszkodaRozmiar[i] = przeszkodaRozmiarOrg;
                    przeszkodaBMP[i] = BITMAP_RESIZER(przeszkodaBMP[i], przeszkodaRozmiar[i] * 2, przeszkodaRozmiar[i] * 2);
                    przeszkodaAktywnaBMP[i]=BITMAP_RESIZER(przeszkodaAktywnaBMP[i],przeszkodaRozmiar[i]*2,przeszkodaRozmiar[i]*2);
                }
                break;
            case MALA_PALETKA:
                paletkaRozmiar=(int)(paletkaRozmiar*skalaBonusow);
                paletkaBMP=BITMAP_RESIZER(paletkaBMP,(paletkaRozmiar*2),(paletkaRozmiar*2));
                break;
            case DUZA_PALETKA:
                paletkaRozmiar=(int)(paletkaRozmiar/skalaBonusow);
                paletkaBMP=BITMAP_RESIZER(paletkaBMP,(paletkaRozmiar*2),(paletkaRozmiar*2));
                break;
            case SZYBKA_PILKA:
            case WOLNA_PILKA:
                mnoznikPredkosciPilki=1;
                break;
            case SZYBKI_CEL:
            case WOLNY_CEL:
                mnoznikPredkosciCelu=1;
                break;
            case ZNIKAJACY_CEL:
                widocznoscCelu=true;
                break;
            case ZNIKAJACA_PRZESZKODA:
            case ZNIKAJACE_PRZESZKODY:
                for (int i=0; i<3; i++) {
                    widocznoscPrzeszkody[i] = true;
                }
                break;
            case BRAK_PKT:
                brakPKT=false;
                break;
            case PODWOJNE_PKT:
                podwojnePKT=false;
                break;
            case GRAWITACJA:
                grawitacja=false;
                break;
            case SUPER_PILKA:
                superPilkaAktywna=false;
        }
        ukrykBonusProgres();
    }

    private int policzPunkty() {
        int pkt;
        float sumaPredkosci = Math.abs(oldCelVX);

        for (int i = 0; i < 3; i++) {
            sumaPredkosci += Math.abs(przeszkodaVX[i]);
        }
        if (pilkaOldV > (maxV * maxV)) {
            pkt = (int) ((Math.sqrt((pilkaOldV / (predkoscPoczatkowa * predkoscPoczatkowa) - 1)) * 10)); //bonus za predkosc pilki
            //Log.e("bonus", "" + pilkaOldV + " " + maxV + " " + pkt);
        } else pkt = 0;
        pkt = pkt + (int) ((sumaPredkosci * 5) / canvasWidth);
        return pkt;
    }

    private void dodajPredkosc(){
        float pMnoznik =(float) Math.sqrt(1+getWynik()*0.002);
        pilkaVX= (pilkaVX * pMnoznik);
        pilkaVY= (pilkaVY * pMnoznik);
        float pPredkoscKwadrat=(pilkaVX*pilkaVX+pilkaVY*pilkaVY);
        float pTopV=getTopV();
        if (pPredkoscKwadrat>pTopV*pTopV){
            setTopV((long)Math.sqrt(pPredkoscKwadrat));
        }
    }

    private TypBonusa losujBonus() {
        return Bonus.getRandom();
    }

    private Point losujPozycjaAktywatora() {
        float x = 0, y = 0;
        Random randomGenerator = new Random();
        int nrSciany = randomGenerator.nextInt(3);
        float pozycjaNaScianie = randomGenerator.nextInt(100);
        switch (nrSciany) {
            case 0://lewa
                //Log.e("bonus","losuj pozycje lewa");
                x = 0;
                y = pozycjaNaScianie * canvasWidth*0.01f;
                break;
            case 1://sufit
                //Log.e("bonus","losuj pozycje sufit");
                y = 0;
                x = pozycjaNaScianie * canvasWidth*0.01f;
                break;
            case 2://prawa
                //Log.e("bonus","losuj pozycje prawa");
                x = canvasWidth;
                y = pozycjaNaScianie * canvasWidth*0.01f;
                break;
        }
        return new Point((int) x, (int) y);
    }


    private boolean czyAktywowac(float aPredkosc){
        return (Math.random() < ((aPredkosc / (2 * predkoscPoczatkowa * predkoscPoczatkowa)) - 0.35f));
    }

    public Bitmap BITMAP_RESIZER(Bitmap bitmap,int newWidth,int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth * 0.5f;
        float middleY = newHeight * 0.5f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() *0.5f, middleY - bitmap.getHeight() *0.5f, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

}
