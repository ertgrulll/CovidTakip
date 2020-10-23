package activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.covidtracker.R;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;

import data.DataManager;
import data.DataStruct;
import data.GlobalDataStruct;
import fragments.GlobalFragment;
import fragments.HomeFragment;
import fragments.LocalFragment;
import services.and.receivers.PermissionChecker;

//TODO: Loading indicator'e belirli bir süre sonra iptal butonu ekle
public class MainActivity extends AppCompatActivity {
    private boolean dataReady = false;
    private boolean isNavigatedLocal = false;
    private boolean isNavigatedGlobal = false;

    private RelativeLayout indicatorContainer;

    private DataStruct todayData;
    private String compareRes;
    private ArrayList<GlobalDataStruct> globalData = new ArrayList<>();
    private HashMap<String, String> worldData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionChecker p = new PermissionChecker(this);
        p.checkPermissions();

        indicatorContainer = findViewById(R.id.indicator_container);
        final BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        final ProgressBar loadingIndicator = findViewById(R.id.loading_indicator);

        final DataManager dataManager = new DataManager(true);

        //Arka planda günlük verileri al
        dataManager.setDataManagerListener((status) -> {
            this.todayData = dataManager.getTodayData();
            this.compareRes = dataManager.getCompareRes();
            this.globalData = dataManager.getCountriesData();
            this.worldData = dataManager.getWorldData();
            setDataReady();
        });

        //Indicator ayarla
        loadingIndicator.setIndeterminateDrawable(new WanderingCubes());

        loadFragment(new HomeFragment());
        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                loadFragment(new HomeFragment());
            }
            else if (itemId == R.id.navigation_global) {//Veri yüklenemediyse indicator göster, veriyi bekle
                if (!dataReady) {
                    indicatorContainer.setVisibility(View.VISIBLE);
                    indicatorContainer.bringToFront();

                    isNavigatedGlobal = true;
                }
                else {
                    loadFragment(new GlobalFragment(globalData, worldData));
                }
            }
            else if (itemId == R.id.navigation_local) {//Veri yüklenemediyse indicator göster, veriyi bekle
                if (!dataReady) {
                    indicatorContainer.setVisibility(View.VISIBLE);
                    indicatorContainer.bringToFront();

                    isNavigatedLocal = true;
                }
                else {
                    loadFragment(new LocalFragment(todayData, compareRes));
                }
            }
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    private void setDataReady() {
        dataReady = true;
        if (isNavigatedLocal) {
            loadFragment(new LocalFragment(todayData, compareRes));
            runOnUiThread(() -> indicatorContainer.setVisibility(View.GONE));
        }
        if (isNavigatedGlobal) {
            loadFragment(new GlobalFragment(globalData, worldData));
            runOnUiThread(() -> indicatorContainer.setVisibility(View.GONE));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.setMainActivity(this);
    }

    //Geri butonunun varsayılan davranışı iptal et
    @Override
    public void onBackPressed() {
    }
}