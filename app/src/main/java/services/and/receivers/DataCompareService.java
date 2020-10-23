package services.and.receivers;

import java.util.Objects;

import data.DataStruct;

public class DataCompareService {

    public DataStruct removeDot(DataStruct data) {
        DataStruct res = new DataStruct(data.getDate(), data.getData());
        String replacedData;
        for (String key : data.getData().keySet()
        ) {
            if (Objects.requireNonNull(data.getData().get(key)).contains(".")) {
                if (key.contains("oran")) {
                    res.getData().put(key, data.getData().get(key));
                } else {
                    replacedData = Objects.requireNonNull(data.getData().get(key)).replace(".", "");
                    while(replacedData.contains(".")) {
                        replacedData = replacedData.replace(".", "");
                    }
                    res.getData().put(key, replacedData);
                }
            }
        }
        return res;
    }

    /**
     * @param todayData     günün verileri
     * @param yesterdayData dünün verileri
     *                      verileri karşılaştırır, string sonucu üretir
     * @return (string) karşılaştırma sonucu döndürür.
     */
    public String compare2days(DataStruct todayData, DataStruct yesterdayData) {
        String vaka, vefat, test, iyilesen;
        String res;

        DataStruct rTodayData = removeDot(todayData);
        DataStruct rArchiveData = removeDot(yesterdayData);

        vaka = calculate(rTodayData.getData().get("gunluk_vaka"), rArchiveData.getData().get("gunluk_vaka"));
        vefat = calculate(rTodayData.getData().get("gunluk_vefat"), rArchiveData.getData().get("gunluk_vefat"));
        test = calculate(rTodayData.getData().get("gunluk_test"), rArchiveData.getData().get("gunluk_test"));
        iyilesen = calculate(rTodayData.getData().get("gunluk_iyilesen"), rArchiveData.getData().get("gunluk_iyilesen"));

        res = "Düne göre değişimler:\nTest sayısı: " + test + "\nVaka sayısı: " +
                vaka + "\nVefat sayısı: " + vefat + "\nİyileşen hasta sayısı: " + iyilesen;

        return res;
    }

    /**
     * @param todayVal     (string), bugünün covid parametre değerleri
     * @param yesterdayVal (string), dünün covid parametre değerleri
     *                     String -> int dönüşümü, int -> string dönüşümü
     * @return (string), değerlerin farkı
     */
    private String calculate(String todayVal, String yesterdayVal) {
        int res = Integer.parseInt(todayVal) - Integer.parseInt(yesterdayVal);

        return String.valueOf(res);
    }
}
