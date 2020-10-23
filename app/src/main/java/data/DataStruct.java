package data;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;

public class DataStruct {
    private Date date;
    private HashMap<String, String> data;

    public DataStruct(Date date, HashMap<String, String> data) {
        this.data = data;
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return "Date: " + date.toString() + " - val: " + data.toString();
    }

    public Date getDate() {
        return date;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }
}
