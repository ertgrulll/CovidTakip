package services.and.receivers;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BTService implements DefaultLifecycleObserver {
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final ActivityResultRegistry registry;
    private final Activity activity;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    public BTService(@NonNull ActivityResultRegistry registry, @NonNull Activity activity) {
        this.registry = registry;
        this.activity = activity;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        activityResultLauncher = registry.register(String.valueOf(Constants.BT_ACTIVITY_REQUEST_CODE),
                new ActivityResultContracts.StartActivityForResult()
                , this::handleRequestRes);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
    }

 /*   private boolean checkHardware() {
        String msg = "";
        if (!(BluetoothAdapter.getDefaultAdapter() == null)) {
            requestEnableBtIfRequire();
            if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
                beaconManager.bind(this);
                if (BeaconTransmitter.checkTransmissionSupported(getApplicationContext()) == 0) {
                    startTransmitter();
                }
            }
        }
        Log.v("Ble support", String.valueOf(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)));
        String transmitSupport = (BeaconTransmitter.checkTransmissionSupported(getApplicationContext())) == 0 ? "supporting" : "not supporting";
        Log.v("Transmit ", transmitSupport);
        return true;
    }*/

    public void requestEnableBtIfRequire() {
        if (bluetoothAdapter == null) {
            Log.e("BTService", "can't find bt hardware");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            FutureTask<Boolean> enableBtWithoutRequest = new FutureTask<>(bluetoothAdapter::enable);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(enableBtWithoutRequest);
            try {
                boolean isBtEnabled = enableBtWithoutRequest.get(4000, TimeUnit.MICROSECONDS);
                if (!isBtEnabled) {
                    Intent btRequest = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    activityResultLauncher.launch(btRequest);
                    executor.shutdown();
                    return;
                }
                Log.v("BTService", "Bluetooth enabled without request needed");
                executor.shutdown();
            }
            catch (ExecutionException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }


    private void handleRequestRes(ActivityResult result) {
        if (result.getResultCode() != Constants.BT_ACTIVITY_REQUEST_CODE) return;

        if (result.getResultCode() == 0) {
            SnackCreator.showActionSnack(activity, Constants.BT_RES_CANCELED_MSG, "Bluetooth'u aç", t -> requestEnableBtIfRequire());
        }
    }
}
