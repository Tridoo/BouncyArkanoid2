package tridoo.bouncyarkanoid2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class WynikiDAO {
    Context context;
    String jStringName="bouncyarkanoid2";

    public WynikiDAO(Context aContext){
        context=aContext;
    }

    public void zapiszWynik(int aPunkty, int aSpeed) {
        long czas= Calendar.getInstance().getTimeInMillis();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray jsonArray = new JSONArray();

        ArrayList<Wynik> pWyniki = podajWyniki();
        pWyniki.add(new Wynik(aPunkty, aSpeed, czas));

        for (Wynik wynik : pWyniki) {
            JSONObject jWynik = new JSONObject();
            try {
                jWynik.put("pkt", wynik.punkty);
                jWynik.put("speed", wynik.predkosc);
                jWynik.put("czas",wynik.czas);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jWynik);
        }
        editor.putString(jStringName, jsonArray.toString());
        editor.apply();
    }

    public ArrayList<Wynik> podajWyniki() {
        ArrayList<Wynik> tablicaWynikow = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String jString = prefs.getString(jStringName, null);

        if (jString == null) return tablicaWynikow;

        try {
            JSONArray jTablica = new JSONArray(jString);
            int ileWierszy = jTablica.length();
            for (int i = 0; i < ileWierszy; i++) {
                JSONObject wynik = jTablica.getJSONObject(i);
                int punkty = (Integer) wynik.get("pkt");
                int predkosc = (Integer) wynik.get("speed");
                long czas = (Long) wynik.get("czas");
                tablicaWynikow.add(new Wynik(punkty, predkosc, czas));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tablicaWynikow;
    }

    public void skasujWyniki(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();
    }
}
