package data;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class GlobalDataStruct {
    private final String country;
    private final HashMap<String, String> globalData;

    public GlobalDataStruct(String country, HashMap<String, String> globalData) {
        this.country = country;
        this.globalData = globalData;
    }

    @NonNull
    @Override
    public String toString() {
        return "Country: " + country + " - val: " + globalData.toString();
    }

    public String getCountry() {
        return country;
    }

    public HashMap<String, String> getGlobalData() {
        return globalData;
    }
}
