package interfaces;

import java.util.ArrayList;

import data.DataStruct;

@FunctionalInterface
public interface LocalDataListener {
    void onDataGet(ArrayList<DataStruct> data);
}
