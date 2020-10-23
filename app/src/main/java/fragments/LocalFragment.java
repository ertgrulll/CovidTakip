package fragments;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.covidtracker.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import data.DataManager;
import data.DataStruct;
import me.relex.circleindicator.CircleIndicator3;
import services.and.receivers.Constants;
import services.and.receivers.SnackCreator;

public class LocalFragment extends Fragment {
    private final DataManager dataManager = new DataManager();
    private DataStruct data;
    private String compareRes;
    private TextView tvTest, tvHasta, tvVefat, tvIyilesen;
    private FloatingActionButton btnDetails;
    private MaterialButton btnSelectDate;
    private ViewPagerAdapter viewPagerAdapter;

    public LocalFragment() {
    }

    public LocalFragment(DataStruct data, String compareRes) {
        this.data = data;
        this.compareRes = compareRes;
    }

    private static Calendar getClearedUtc() {
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.clear();
        return utc;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local, container, false);

        tvTest = view.findViewById(R.id.tvTest);
        tvHasta = view.findViewById(R.id.tvHasta);
        tvIyilesen = view.findViewById(R.id.tvIyilesen);
        tvVefat = view.findViewById(R.id.tvVefat);
        btnDetails = view.findViewById(R.id.btnDetails);
        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        ViewPager2 viewPager2 = view.findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(this, dataManager);

        CircleIndicator3 indicator = view.findViewById(R.id.indicator);

        //Grafikleri içeren viewpager, adapter ve indicator
        viewPager2.setAdapter(viewPagerAdapter);
        viewPager2.setPageTransformer(new ViewPagerAdapter.ZoomOutPageTransformer());
        indicator.setViewPager(viewPager2);

        setBtnEffect();

        btnSelectDate.setOnClickListener(view1 -> {
            btnSelectDate.setEnabled(false);
            createDatePicker();
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //data(DataStruct): güncel veri, parent'dan parametre alır
        if (data == null) {
            SnackCreator.showSnack(getActivity(), Constants.UNKNOWN_ERROR_MSG);
        }
        else locateData();
        //compareRes(String) bugün - önceki gün verisi karşılaştırma sonucu. Snackbar olarak gösterilir.
        btnDetails.setOnClickListener(view1 -> SnackCreator.showActionSnack(requireActivity(), compareRes, "Kapat", Snackbar::dismiss));
    }

    /**
     * Tarih aralığı için date picker oluşturur. Covid istatistik verisi beşlangıç tarihinden
     * bugüne kadar, grafik okunabilirliği için en fazla 15 gün seçilebilir.
     */
    private void createDatePicker() {
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        MaterialDatePicker.Builder<Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker();
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        Calendar calendar = getClearedUtc();
        calendar.set(2020, 2, 11);
        long statsStartDay = calendar.getTimeInMillis();
        long today = MaterialDatePicker.todayInUtcMilliseconds();

        constraintsBuilder.setStart(statsStartDay);
        constraintsBuilder.setEnd(today);
        constraintsBuilder.setOpenAt(today);
        constraintsBuilder.setValidator(DateValidatorPointBackward.before(today - 24 * 60 * 60 * 1000));

        builder.setCalendarConstraints(constraintsBuilder.build());
        builder.setTitleText(R.string.tarih_araligi_sec);
        builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar);

        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

        pickerHandler(picker);

        picker.show(getParentFragmentManager(), picker.toString());
    }

    /**
     * @param picker pickerObj
     *               tarih aralığı seçilip butona tıklandığında seçilen gün sayısını kontrol eder, listener tetikler,
     *               ilgili arayüz elemanlarını yeniler.
     */
    private void pickerHandler(MaterialDatePicker<Pair<Long, Long>> picker) {
        picker.addOnPositiveButtonClickListener(selection -> {
            int second = (int) (selection.second / (24 * 60 * 60 * 1000));
            int first = (int) (selection.first / (24 * 60 * 60 * 1000));
            int interval = second - first;

            if (interval > 14) {
                SnackCreator.showSnack(getActivity(), "En fazla 15 gün seçebilirsiniz!");
                new android.os.Handler().postDelayed(
                        this::createDatePicker,
                        1500);
            }
            else {
                Date startDate = new Date();
                startDate.setTime(selection.first - 3 * 60 * 60 * 1000);
                dataManager.getGraphData(startDate, interval);
                //TODO: #bug sayfa yenilendiğinde grafik bazen küçük kalıyor, hatayı düzelt
                viewPagerAdapter.notifyDataSetChanged();
                /*viewPager2.invalidate();*/
                btnSelectDate.setEnabled(true);
                /*viewPager2.post(viewPager2::invalidate);*/
            }
        });

        picker.addOnNegativeButtonClickListener(view -> btnSelectDate.setEnabled(true));
        picker.addOnCancelListener(view -> btnSelectDate.setEnabled(true));
    }

    /**
     * "?" buton animasyonu
     */
    private void setBtnEffect() {
        YoYo.with(Techniques.Pulse)
                .duration(500)
                .repeat(1)
                .delay(1500)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        YoYo.with(Techniques.Pulse)
                                .duration(500)
                                .delay(5000)
                                .interpolate(new AccelerateDecelerateInterpolator())
                                .withListener(this)
                                .playOn(btnDetails);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                })
                .playOn(btnDetails);
    }


    /**
     * Kartlar için veri yerleşimi
     */
    private void locateData() {
        for (String key : data.getData().keySet()
        ) {
            String val = data.getData().get(key);
            switch (key) {
                case "gunluk_test": {
                    tvTest.setText(val);
                    break;
                }
                case "gunluk_vaka": {
                    tvHasta.setText(val);
                    break;
                }
                case "gunluk_vefat": {
                    tvVefat.setText(val);
                    break;
                }
                case "gunluk_iyilesen": {
                    tvIyilesen.setText(val);
                    break;
                }
            }
        }
    }
}