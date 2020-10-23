package interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import data.GlobalDataStruct;

@FunctionalInterface
public interface GlobalDataListener {
    void onDataGet(ArrayList<GlobalDataStruct> globalData, HashMap<String, String> worldData);
}
