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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import data.DataStruct;
import interfaces.LocalDataListener;

public class LocalDataFetchService implements Runnable {
    private final ArrayList<DataStruct> localData = new ArrayList<>();
    private LocalDataListener localDataListener;

    @Override
    public void run() {
        if(localData.isEmpty()) {
            try {
                getArchiveData();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getArchiveData() throws IOException, JSONException {
        BufferedReader reader;
        URL url = new URL("https://covid19.saglik.gov.tr/covid19api?getir=liste");
        URLConnection request = url.openConnection();

        request.connect();

        InputStream stream = request.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder buffer = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }

        JSONArray jsonArray = new JSONArray(buffer.toString());
        reader.close();
        stream.close();
        parseData(jsonArray);
    }

    private void parseData(JSONArray res) throws JSONException {
        JSONObject dayData;
        for(int i=0; i<res.length(); i++) {
            dayData =(JSONObject) res.get(i);

            Date date = null;
            HashMap<String, String> dataHash = new HashMap<>();

            String dateVal = dayData.getString("tarih");
            try {
                date = new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).parse(dateVal);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

            for (Iterator<String> it = dayData.keys(); it.hasNext(); ) {
                String key = it.next();

                dataHash.put(key, dayData.getString(key));
            }

            localData.add(new DataStruct(date, dataHash));
        }
        Log.v("LocalDataFetchService", "ok");
        localDataListener.onDataGet(localData);
    }

    public void setLocalDataListener(LocalDataListener localDataListener) {
        this.localDataListener = localDataListener;
    }
}
