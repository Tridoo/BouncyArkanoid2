package tridoo.bouncyarkanoid2;

public class Wynik {
    long czas;
    int punkty;
    int predkosc;

    public Wynik(int aPunkty, int aPredkosc, long aCzas){
        punkty=aPunkty;
        predkosc=aPredkosc;
        czas=aCzas;
    }

    public int getPunkty() {
        return punkty;
    }

    public void setPunkty(int punkty) {
        this.punkty = punkty;
    }

    public int getPredkosc() {
        return predkosc;
    }

    public void setPredkosc(int predkosc) {
        this.predkosc = predkosc;
    }

    public long getCzas() {
        return czas;
    }

    public void setCzas(long czas) {
        this.czas = czas;
    }
}