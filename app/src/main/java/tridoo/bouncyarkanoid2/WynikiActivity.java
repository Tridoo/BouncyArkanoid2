package tridoo.bouncyarkanoid2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class WynikiActivity extends ActionBarActivity {
    int pRozmiarRozpychacza;
    int pRozmiarTextu;
    WynikiDAO dao;
    GraphView graph;
    static final int LIMIT_WYKRESU=7;
    Typeface font;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        font = Typeface.createFromAsset(getAssets(),"Athiti-Medium.ttf");
        setContentView(R.layout.activity_wyniki);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        AdView mAdView = (AdView) findViewById(R.id.adViewWyniki);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        graph = (GraphView) findViewById(R.id.graph);
        GridLayout gridLayout = (GridLayout) findViewById(R.id.grid);
        dao =  new WynikiDAO(getApplicationContext());

        int pSzerokosc= this.getWindowManager().getDefaultDisplay().getWidth();
        pRozmiarRozpychacza=pSzerokosc/20;
        pRozmiarTextu=pSzerokosc/5;

        dodajNaglowek(gridLayout);

        ArrayList<Wynik> listaWynikow=dao.podajWyniki();

        int i=0;
        for (Wynik pWynik:podajWynikiWGPunktow(listaWynikow)){
            i++;
            dodajWynik(gridLayout, i, pWynik.getPunkty(), pWynik.getPredkosc());
        }

        if (!listaWynikow.isEmpty()) rysujWykres(podajWynikiWGCzasu(listaWynikow));
    }

    private void dodajNaglowek(GridLayout aGrid){
        LinearLayout linearLayout=dodajLinie(5);
        dodajRozpychacz(linearLayout,pRozmiarRozpychacza);
        dodajPoleTextowe(linearLayout,pRozmiarTextu,"Position");
        dodajRozpychacz(linearLayout,pRozmiarRozpychacza);
        dodajPoleTextowe(linearLayout,pRozmiarTextu,"Points");
        dodajRozpychacz(linearLayout,pRozmiarRozpychacza);
        dodajPoleTextowe(linearLayout,pRozmiarTextu,"Speed");
        dodajRozpychacz(linearLayout,pRozmiarRozpychacza);
        aGrid.addView(linearLayout);

    }

    private void dodajWynik(GridLayout aGrid, int aPozycja, int aPunkty, int aPredkosc){
        String pPozycja = Integer.toString(aPozycja)+".";
        String pPunkty = Integer.toString(aPunkty);
        String pPredkosc = Integer.toString(aPredkosc);
        LinearLayout linearLayout=dodajLinie(5);
        dodajRozpychacz(linearLayout,pRozmiarRozpychacza);
        dodajPoleTextowe(linearLayout,pRozmiarTextu,pPozycja);
        dodajRozpychacz(linearLayout,pRozmiarRozpychacza);
        dodajPoleTextowe(linearLayout,pRozmiarTextu,pPunkty);
        dodajRozpychacz(linearLayout,pRozmiarRozpychacza);
        dodajPoleTextowe(linearLayout,pRozmiarTextu,pPredkosc);
        dodajRozpychacz(linearLayout,pRozmiarRozpychacza);
        aGrid.addView(linearLayout);
    }

    private LinearLayout dodajLinie(int aPadding){
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundResource(R.drawable.border);
        linearLayout.setDividerPadding(aPadding);
        linearLayout.setGravity(1);

        return linearLayout;
    }

    private void dodajRozpychacz(LinearLayout aLay, int aRozmiar){
        Space rozpychacz = new Space(this);
        rozpychacz.setMinimumWidth(aRozmiar);
        aLay.addView(rozpychacz);
    }

    private void dodajPoleTextowe(LinearLayout aLay, int aRozmiar, String aText){
        TextView newTV = new TextView(this);
        newTV.setText(aText);
        newTV.setWidth(aRozmiar);
        newTV.setGravity(1);
        newTV.setTextColor(Color.WHITE);
        newTV.setTypeface(font);
        newTV.setTextSize(18);
        aLay.addView(newTV);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wyniki, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.trash) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Do you want to delete all?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            skasujWyniki();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void skasujWyniki() {
        dao.skasujWyniki();
        CharSequence text = "DONE";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
        finish();
    }

    private void rysujWykres(ArrayList<Wynik> aListaWynikow){
        int maxPkt=0;

        DataPoint[] tablicaPunktow=new DataPoint[aListaWynikow.size()];
        DataPoint[] tablicaPredkosci=new DataPoint[aListaWynikow.size()];

        int i=0;
        for(Wynik pWynik:aListaWynikow){
            tablicaPunktow[i]= new DataPoint(i+1,pWynik.getPunkty());
            tablicaPredkosci[i]= new DataPoint(i+1, pWynik.getPredkosc()) ;
            if (maxPkt<pWynik.getPunkty()) maxPkt = pWynik.getPunkty();
            i++;
        }


        LineGraphSeries<DataPoint> seriaPunktow = new LineGraphSeries<DataPoint>(tablicaPunktow);
        BarGraphSeries<DataPoint> seriaPredkosci = new BarGraphSeries<DataPoint>(tablicaPredkosci);

        seriaPunktow.setColor(Color.rgb(241,196,15));
        seriaPunktow.setDrawDataPoints(true);
        seriaPunktow.setDataPointsRadius(10);

        seriaPredkosci.setColor(Color.rgb(140,168,187));
        seriaPredkosci.setSpacing(30);

        graph.addSeries(seriaPredkosci);

// set second scale
        graph.getSecondScale().addSeries(seriaPunktow);
// the y bounds are always manual for second scale
        graph.getSecondScale().setMinY(0);
        graph.getSecondScale().setMaxY(maxPkt*1.2);


        double xInterval=1.0;
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(seriaPredkosci.getLowestValueX() - (xInterval/2.0));
        graph.getViewport().setMaxX(seriaPredkosci.getHighestValueX() + (xInterval/2.0));


        graph.getGridLabelRenderer().setTextSize(0);
        graph.getGridLabelRenderer().setGridColor(Color.GRAY);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"",""});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getGridLabelRenderer().reloadStyles();
    }

    private ArrayList<Wynik> podajWynikiWGPunktow(ArrayList<Wynik> aWyniki) {
        Collections.sort(aWyniki, new Comparator<Wynik>() {
            @Override
            public int compare(Wynik wynik1, Wynik wynik2) {
                int porownanie = wynik2.getPunkty() - wynik1.getPunkty();
                if (porownanie == 0) porownanie = wynik2.getPredkosc() - wynik1.getPredkosc();
                return porownanie;
            }
        });
        return aWyniki;
    }

    private ArrayList<Wynik> podajWynikiWGCzasu(ArrayList<Wynik> aWyniki) {
        Collections.sort(aWyniki, new Comparator<Wynik>() {
            @Override
            public int compare(Wynik wynik1, Wynik wynik2) {
                return (int) (wynik2.getCzas() - wynik1.getCzas());
            }
        });

        List<Wynik> listaWynikow = FluentIterable.from(aWyniki).limit(LIMIT_WYKRESU).toList().reverse();
        return new ArrayList<>(listaWynikow);
    }
}