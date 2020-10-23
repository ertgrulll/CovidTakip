package services.and.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SystemReceivers extends BroadcastReceiver {
    private static final String TAG = SystemReceivers.class.getSimpleName();

    public SystemReceivers() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        if (action.equals("stop_scanner")) {
            Log.v("SystemReceiver", "Stopping scan and transmit");
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
