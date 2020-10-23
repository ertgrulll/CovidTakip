package data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import interfaces.DataManagerListener;
import interfaces.GraphDataListener;
import services.and.receivers.DataCompareService;
import services.and.receivers.GlobalDataFetchService;
import services.and.receivers.LocalDataFetchService;

public class DataManager {
    public static ArrayList<DataStruct> SDataTR = new ArrayList<>();
    private final GlobalDataFetchService globalDataFetchService = new GlobalDataFetchService();
    private final LocalDataFetchService localDataFetchService = new LocalDataFetchService();
    private final Thread tGlobalDataFetchService = new Thread(globalDataFetchService);
    private final Thread tTrDataFetchService = new Thread(localDataFetchService);
    private final DataCompareService dataCompareService = new DataCompareService();
    private ArrayList<GlobalDataStruct> countriesData;
    private HashMap<String, String> worldData = new HashMap<>();
    private DataManagerListener dataManagerListener;
    private GraphDataListener graphDataListener;
    private boolean gotLocalData = false;
    private boolean gotGlobalData = false;


    public DataManager() {
    }

    public DataManager(boolean fetchData) {
        fetchLocalData();
        fetchGlobalData();
    }

    private void fetchLocalData() {
        tTrDataFetchService.setPriority(Thread.MAX_PRIORITY);
        localDataFetchService.setLocalDataListener((data) -> {
            SDataTR = data;
            tTrDataFetchService.interrupt();
            gotLocalData = true;
            notifyListener();
        });
        tTrDataFetchService.start();
    }

    private void fetchGlobalData() {
        globalDataFetchService.setGlobalDataListener((countriesData, worldData) -> {
            this.countriesData = countriesData;
            this.worldData = worldData;
            tGlobalDataFetchService.interrupt();
            gotGlobalData = true;
            notifyListener();
        });
        tGlobalDataFetchService.start();
    }

    public DataStruct getTodayData() {
        return SDataTR.get(0);
    }

    public String getCompareRes() {
        return dataCompareService.compare2days(SDataTR.get(0), SDataTR.get(1));
    }

    /**
     * (default data)veri sırasını tersine çevirip weekData'ya ekler, grafikte görüntülenmesi
     * 7 gün önceden bugüne şeklinde
     */
    private ArrayList<DataStruct> getDefaultGraphData() {
        ArrayList<DataStruct> intervalData = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            intervalData.add(dataCompareService.removeDot(SDataTR.get(i)));
        }
        return intervalData;
    }

    /**
     * @param dateAndInterval başlangıç. tarihi ve başlangıç - bitiş arasındaki gün sayısı,
     * @return başlangıç-bitiş aralığındaki veri
     *
     * @Overload grafikler için veri
     */
    public ArrayList<DataStruct> getGraphData(Object... dateAndInterval) {
        Date startDate;
        int interval;
        ArrayList<DataStruct> intervalData = new ArrayList<>();

        if (dateAndInterval == null || dateAndInterval.length == 0) {
            return getDefaultGraphData();
        }
        else {
            try {
                startDate = (Date) dateAndInterval[0];
                interval = (int) dateAndInterval[1];
            }
            catch (Exception e) {
                startDate = (Date) dateAndInterval[1];
                interval = (int) dateAndInterval[0];
            }

            for (int i = 0; i < SDataTR.size() - 1; i++) {
                DataStruct value = SDataTR.get(i);

                if (value.getDate().toString().equals(startDate.toString())) {
                    for (int j = i; j >= i - interval; j--) {
                        intervalData.add(dataCompareService.removeDot(SDataTR.get(j)));
                    }
                    break;
                }
            }
        }
        graphDataListener.notifyDataChange(intervalData);
        return intervalData;
    }

    /**
     * Api servislerinden veri alma işlemlerinin durumunu kontrol edip tamamlandığında veri bekleyen
     * sınıflara/metotlara bildirir
     */
    private void notifyListener() {
        if (gotLocalData && gotGlobalData) {
            getGraphData();
            dataManagerListener.dataReady(true);
        }
    }

    public HashMap<String, String> getWorldData() {
        return worldData;
    }

    public ArrayList<GlobalDataStruct> getCountriesData() {
        return this.countriesData;
    }

    public void setDataManagerListener(DataManagerListener dataManagerListener) {
        this.dataManagerListener = dataManagerListener;
    }

    public void setGraphDataListener(GraphDataListener graphDataListener) {
        this.graphDataListener = graphDataListener;
    }
}