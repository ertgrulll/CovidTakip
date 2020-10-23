package interfaces;

import java.util.ArrayList;

import data.DataStruct;

public interface GraphDataListener {
    void notifyDataChange(ArrayList<DataStruct> graphData);
}
