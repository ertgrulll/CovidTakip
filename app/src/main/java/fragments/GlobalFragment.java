package fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.covidtracker.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import data.GlobalDataStruct;
import services.and.receivers.SnackCreator;

public class GlobalFragment extends Fragment {
    private ArrayList<GlobalDataStruct> globalData = new ArrayList<>();
    private HashMap<String, String> worldData = new HashMap<>();
    private final ArrayList<PieEntry> pieEntries = new ArrayList<>();
    private Snackbar dataSnack;

    public GlobalFragment() {
        // Required empty public constructor
    }

    public GlobalFragment(ArrayList<GlobalDataStruct> SDataGlobal, HashMap<String, String> worldData) {
        this.globalData = SDataGlobal;
        this.worldData = worldData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_global, container, false);
        TextView affectedCountries = view.findViewById(R.id.affectedCountries);
        TextView totalCase = view.findViewById(R.id.totalCases);
        TextView totalDeath = view.findViewById(R.id.totalDeaths);
        TextView todayDeath = view.findViewById(R.id.todayDeaths);

        affectedCountries.setText("Etkilenen ülke sayısı: " + worldData.get("affectedCountries"));
        totalCase.setText("Toplam vaka sayısı: " + worldData.get("cases"));
        totalDeath.setText("Toplam vefat sayısı: " + worldData.get("deaths"));
        todayDeath.setText("Günlük vefat sayısı: " + worldData.get("todayDeaths"));

        PieChart pieChart = view.findViewById(R.id.pieChart);

        final String[] keys = {"deaths", "cases", "todayCases", "todayDeaths"};
        final String[] anahtarlar = {"vefat", "vaka", "günlük vaka", "günlük vefat"};

        final int keyIndex = (int) (Math.random() * 100) % 4;
        final String selectedKey = keys[keyIndex];
        final List<Integer> randomColors = generateRandomColors(114, 187, 232);

        selectData(selectedKey);

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(randomColors);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(10f);

        //entry label settings, küçük değerlerde üst üste gelebildiğinden legend kullanılıyor
        /*pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1Length(0.7f);
        pieDataSet.setValueLinePart2Length(0.7f);
        pieDataSet.setValueLinePart1OffsetPercentage(100f);
        pieDataSet.setIconsOffset(new MPPointF(0, 40));*/

        PieData data = new PieData(pieDataSet);
        data.setValueTextSize(15);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.setExtraOffsets(3f, 3f, 3f, 0);
        pieChart.setCenterText("Rastgele 5 ülkenin '" + anahtarlar[keyIndex] + "' sayıları");
        pieChart.setCenterTextSize(14);
        pieChart.setHoleRadius(37);
        pieChart.setTransparentCircleRadius(70);
        pieChart.setTransparentCircleAlpha(40);
        pieChart.animateXY(1500, 1000, Easing.EaseInOutQuart);
        pieChart.setDrawEntryLabels(false);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setTextSize(12);
        legend.setXOffset(5);
        legend.setYOffset(5);
        legend.setFormSize(14);

        pieChart.getDescription().setEnabled(false);

        //entry label settings, küçük değerlerde üst üste gelebildiğinden legend kullanılıyor
        /*pieChart.setEntryLabelTextSize(13);
        pieChart.setEntryLabelColor(Color.BLACK);*/
        pieChart.invalidate();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                HashMap<?, ?> dataMap = (HashMap<?, ?>) e.getData();
                if (dataSnack != null && dataSnack.isShown()) {
                    dataSnack.dismiss();
                }
                showSnack(dataMap);
            }

            @Override
            public void onNothingSelected() { }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * @param key "deaths, cases, todayCases, todayDeaths" arasından rastgele belirlenip gönderiliyor
     */
    private void selectData(String key) {
        Random random = new Random();
        List<Integer> selectedIndexes = new ArrayList<>();


        //Değer kontrolü, PieChart'da en fazla 2 değer 0 olarak gösterilecek
        int uselessValCounter = 0;
        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(globalData.size() - 1);
            GlobalDataStruct data = globalData.get(index);
            selectedIndexes.add(index);
            HashMap<String, String> dataMap = data.getGlobalData();
            int val = Integer.parseInt(Objects.requireNonNull(dataMap.get(key)));

            if (val == 0) {
                uselessValCounter++;
            }
        }

        if (uselessValCounter > 2) {
            selectData(key);
            return;
        }
        for (int i = 0; i < 5; i++) {
            GlobalDataStruct data = globalData.get(selectedIndexes.get(i));
            HashMap<String, String> dataMap = data.getGlobalData();
            PieEntry entry = new PieEntry(Integer.parseInt(Objects.requireNonNull(dataMap.get(key))), data.getCountry(), dataMap);
            pieEntries.add(i, entry);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showSnack(HashMap<?, ?> data) {
        String continent = "", countryName = "";
        final ArrayList<String> kitalar = new ArrayList<>(Arrays.asList("Asya", "Avrupa", "Afrika", "Güney Amerika", "Kuzey Amerika", "Avusturalya"));
        final ArrayList<String> continents = new ArrayList<>(Arrays.asList("Asia", "Europe", "Africa", "South America", "North America", "Australia" +
                "/Oceania"));

        for (int i = 0; i < 6; i++) {
            if (continents.get(i).equals(data.get("continent"))) {
                continent = kitalar.get(i);
                break;
            }
        }

        Locale inLocale = Locale.forLanguageTag("en_US");
        Locale outLocale = Locale.forLanguageTag("tr_TR");
        for (Locale locale : Locale.getAvailableLocales()) {
            if (locale.getDisplayCountry(inLocale).equals(data.get("country"))) {
                countryName = locale.getDisplayCountry(outLocale);
                break;
            }
        }

        if (countryName.isEmpty()) {
            countryName = (String) data.get("country");
        }

        String res = countryName + ", " + continent + " kıtasında bir ülke. \nNüfus: " + data.get("population") + "\nToplam vaka sayısı: " +
                data.get("cases") + "\nToplam vefat: " + data.get("deaths") + "\nToplam iyileşen: " + data.get("recovered");
        SnackCreator.showActionSnack(requireActivity(), res, "Kapat", t -> {
            t.dismiss();
            dataSnack = t;
        });
    }

    private List<Integer> generateRandomColors(int red, int green, int blue) {
        List<Integer> colors = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            int r = (random.nextInt(256) + red) / 2;
            int g = (random.nextInt(256) + green) / 2;
            int b = (random.nextInt(256) + blue) / 2;

            colors.add(Color.rgb(r, g, b));
        }
        return colors;
    }
}