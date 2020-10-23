package interfaces;

import java.util.ArrayList;

import data.DataStruct;

@FunctionalInterface
public interface DataManagerListener {
    void dataReady(/*DataStruct todayData, ArrayList<DataStruct> archiveDataTR, String compareResult*/boolean status);
}
