package services.and.receivers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import data.GlobalDataStruct;
import interfaces.GlobalDataListener;

public class GlobalDataFetchService implements Runnable {
    private final ArrayList<GlobalDataStruct> countriesData = new ArrayList<>();
    private final HashMap<String, String> worldData = new HashMap<>();
    private GlobalDataListener globalDataListener;

    public GlobalDataFetchService() {}

    @Override
    public void run() {
        if (countriesData.isEmpty()) {
            try {
                String countriesDataUrl = "https://disease.sh/v3/covid-19/countries?sort=cases";
                parseData(getGlobalData(countriesDataUrl));
                String worldDataUrl = "https://disease.sh/v3/covid-19/all";
                addToHash(getGlobalData(worldDataUrl));
                notifyListener();
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }



    @SuppressWarnings("unchecked")
    private <T> T getGlobalData(String uriString) throws IOException, JSONException {
        URL url = new URL(uriString);
        BufferedReader reader;
        URLConnection request = url.openConnection();

        request.connect();
        InputStream stream = request.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder buffer = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        T res;
        try {
            res = (T) new JSONArray(buffer.toString());
        }
        catch (Exception e) {
            res = (T) new JSONObject(buffer.toString());
        }
        reader.close();
        stream.close();
        return res;
    }

    private void parseData(JSONArray res) throws JSONException {
        JSONObject countryData;
        for (int i = 0; i < res.length(); i++) {
            if (!(res.get(i) instanceof JSONObject)) {
                continue;
            }
            countryData = (JSONObject) res.get(i);
            String countryName = countryData.getString("country");

            HashMap<String, String> dataHash = new HashMap<>();

            for (Iterator<String> it = countryData.keys(); it.hasNext(); ) {
                String key = it.next();

                dataHash.put(key, countryData.getString(key));
            }

            countriesData.add(new GlobalDataStruct(countryName, dataHash));
        }
    }

    private void addToHash(JSONObject data) throws JSONException {
        for (Iterator<String> it = data.keys(); it.hasNext(); ) {
            String key = it.next();
            worldData.put(key, data.getString(key));
        }
    }

    private void notifyListener() {
        Log.v("GlobalDataFetch", "ok");
        globalDataListener.onDataGet(countriesData, worldData);
    }

    public void setGlobalDataListener(GlobalDataListener globalDataListener) {
        this.globalDataListener = globalDataListener;
    }
}