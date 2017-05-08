package tridoo.bouncyarkanoid2;


enum TypBonusa {
    DUZY_CEL(1), MALY_CEL(0), MALA_PRZESZKODA(1), DUZA_PRZESZKODA(0), MALE_PRZESZKODY(1), DUZE_PRZESZKODY(0), DUZA_PALETKA(1), MALA_PALETKA(0),
    WOLNA_PILKA(1), SZYBKA_PILKA(0), WOLNY_CEL(1), SZYBKI_CEL(0),
    ZNIKAJACY_CEL(0), ZNIKAJACA_PRZESZKODA(1), ZNIKAJACE_PRZESZKODY(1),
    PODWOJNE_PKT(1), BRAK_PKT(0),
    SUPER_PILKA(1), GRAWITACJA(0);

    private int czyDodatni;

    private TypBonusa(int flaga) {
        czyDodatni = flaga;
    }

    public boolean isCzyDodatni() {
        return czyDodatni == 1;
    }

}

public class Bonus {

    public static TypBonusa getRandom() {
        return TypBonusa.values()[(int) (Math.random() * TypBonusa.values().length)];
    }
}

