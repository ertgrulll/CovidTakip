package fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.covidtracker.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import data.DataStruct;

public class LocalChartFragment extends Fragment {
    private int position;
    private LinearLayout container;
    private ArrayList<DataStruct> data = new ArrayList<>();

    public LocalChartFragment() {
        //required
    }

    public LocalChartFragment(int position, ArrayList<DataStruct> data) {
        this.data = data;
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart_local, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String chartDescription;
        container = view.findViewById(R.id.chart_container);
        TextView tv = new TextView(getContext());

        tv.setTextColor(Color.rgb(85, 86, 87));
        tv.setPadding(0, 20, 0, 0);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(Typeface.create("sans-serif-condensed-medium", Typeface.NORMAL));
        tv.setLetterSpacing(.1f);
        tv.setTextSize(15);

        container.addView(tv, 0);

        switch (position) {
            case 0: {
                chartDescription = "Test";
                setChart(new LineChart(getContext()), "toplam_test", LineDataSet.Mode.LINEAR, true, Color.BLUE);
                break;
            }
            case 1: {
                chartDescription = "Vaka";
                setChart(new BarChart(getContext()), "toplam_vaka", Color.argb(150, 252, 168, 0));
                break;
            }
            case 2: {
                chartDescription = "Vefat";
                setChart(new LineChart(getContext()), "toplam_vefat", LineDataSet.Mode.HORIZONTAL_BEZIER, true, Color.rgb(189, 9, 15));
                break;
            }
            case 3: {
                chartDescription = "Ağır Hasta";
                setChart(new LineChart(getContext()), "agir_hasta_sayisi", LineDataSet.Mode.CUBIC_BEZIER, true, Color.rgb(77, 63, 63));
                break;
            }
            case 4: {
                chartDescription = "İyileşen Hasta";
                setChart(new BarChart(getContext()), "toplam_iyilesen", Color.argb(150, 37, 219, 37));
                break;
            }
            case 5: {
                chartDescription = "Yatak Doluluk Oranı";
                setChart(new LineChart(getContext()), "yatak_doluluk_orani", LineDataSet.Mode.LINEAR, true, Color.rgb(133, 137, 140));
                break;
            }
            default: {
                chartDescription = "Veri ile ilgili bir hata oluştu.\nDaha sonra tekrar deneyin.";
                break;
            }
        }
        tv.setText(chartDescription);
    }

    private void setChart(BarChart chart, String header, int color) {
        if (this.data.size() == 0) {
            return;
        }

        setChartStyle(chart);

        List<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xAxisValues = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM", java.util.Locale.getDefault());
        int index = 0;
        for (int i = 0; i < this.data.size(); i++) {
            HashMap<String, String> dataMap = this.data.get(i).getData();
            entries.add(new BarEntry(index, Integer.parseInt(Objects.requireNonNull(dataMap.get(header)))));
            xAxisValues.add(formatter.format(data.get(i).getDate()));
            index++;
        }
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));

        BarDataSet barDataSet = new BarDataSet(entries, "");

        if (color != 0) {
            barDataSet.setColor(color);
        }
        else {
            barDataSet.setColor(Color.rgb(222, 18, 72));
        }

        BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(12);

        chart.setData(barData);
        setChartStyle(chart);
        chart.invalidate();

        container.addView(chart, 1);
        container.invalidate();
    }

    @SuppressWarnings("SameParameterValue")
    private <V> void setChart(LineChart chart, String header, V mode, boolean filled, int... color) {
        if (this.data.size() == 0) {
            return;
        }
        List<Entry> entries = new ArrayList<>();
        ArrayList<String> xAxisValues = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM", java.util.Locale.getDefault());

        int index = 0; //verinin yerleşim sırası için kullanıldı
        for (int i = 0; i < this.data.size(); i++) {
            HashMap<String, String> dataMap = this.data.get(i).getData();
            String xAxisLabel = formatter.format(this.data.get(i).getDate()).trim();
            xAxisValues.add(xAxisLabel);

            //typecast String -> float or int
            if (Objects.requireNonNull(dataMap.get(header)).isEmpty()) {
                entries.add(new Entry(index, 0));
            }
            else {
                try {
                    int num = Integer.parseInt(Objects.requireNonNull(dataMap.get(header)));
                    entries.add(new Entry(index, num));
                }
                catch (NumberFormatException e) {
                    float num = Float.parseFloat(Objects.requireNonNull(dataMap.get(header)));
                    entries.add(new Entry(index, num));
                }
            }
            index++;
        }
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));

        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setMode((LineDataSet.Mode) mode);
        lineDataSet.setDrawFilled(filled);
        lineDataSet.setCircleRadius(6.0f);

        if (!filled) {
            lineDataSet.setLineWidth(3.0f);
            lineDataSet.setColor(Color.LTGRAY, 255);
            lineDataSet.setCircleColor(Color.rgb(131, 180, 125));
        }

        if (color.length != 0) {
            int red = Color.red(color[0]);
            int green = Color.green(color[0]);
            int blue = Color.blue(color[0]);
            int circleColor = Color.argb(50, red, green, blue);

            lineDataSet.setCircleHoleColor(circleColor);
            lineDataSet.setFillColor(color[0]);
            lineDataSet.setCircleColor(circleColor);

        }
        else {
            lineDataSet.setFillColor(Color.GREEN);
        }
        lineDataSet.setFillAlpha(50);
        lineDataSet.setValueTextSize(12);

        LineData lineData = new LineData(lineDataSet);
        chart.setData(lineData);
        setChartStyle(chart);
        chart.invalidate();

        container.addView(chart, 1);
    }

    @SuppressWarnings("rawtypes")
    private <T extends BarLineChartBase> void setChartStyle(T t) {
        LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                .4f
        );
        t.setLayoutParams(chartParams);
        t.animateXY(1000, 1000, Easing.EaseInOutQuad);
        t.getDescription().setEnabled(false);
        t.getLegend().setEnabled(false);
        t.setDoubleTapToZoomEnabled(false);
        t.setNoDataText("Veri sağlanamıyor. Bu, seçilen tarih aralığında ilgili verinin olmamasından\n" +
                " veya bir sunucu hatasından kaynaklanıyor olabilir.\nLütfen  daha sonra tekrar deneyin.");

        XAxis xAxis = t.getXAxis();
        YAxis yAxis = t.getAxisLeft();

        /*xAxis.setCenterAxisLabels(true);*/
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setGranularity(1f);

        yAxis.setLabelCount(3, true);
        yAxis.setDrawLabels(true);
        if (t instanceof BarChart) {
            yAxis.setGridColor(Color.TRANSPARENT);
            xAxis.setGridColor(Color.TRANSPARENT);
        }

        t.getAxisRight().setEnabled(false);
    }
}
