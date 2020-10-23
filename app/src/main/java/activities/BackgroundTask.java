package activities;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.TaskStackBuilder;
import androidx.multidex.MultiDex;

import com.example.covidtracker.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.SSLContext;

import fragments.HomeFragment;
import interfaces.Task;
import services.and.receivers.Constants;
import services.and.receivers.SystemReceivers;

@SuppressWarnings("ConstantConditions")
public class BackgroundTask extends Application implements BootstrapNotifier, RangeNotifier, Application.ActivityLifecycleCallbacks {
    public static Task<String> UI;
    private final Region region = new Region("covidtakip", null, null, null);
    private final BeaconParser beaconParser = new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-9,i:10-21,i:22-23,p:24-24,d:25-25");
    private final ArrayList<String> foundUsers = new ArrayList<>();
    private final ArrayList<String> foundPeople = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private BeaconManager beaconManager;
    private RegionBootstrap regionBootstrap;
    private BeaconTransmitter beaconTransmitter;
    private MainActivity mainActivity = null;
    private String tmpId = "0";
    private String tmpCondition = "0";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        }
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        setForegroundScanner();

        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(beaconParser);
        BeaconManager.setDebug(false);
        beaconManager.applySettings();

        HomeFragment.notifySwitchChange = bool -> {
            if (beaconTransmitter != null) beaconTransmitter.stopAdvertising();

            DocumentReference dr = db.collection("users").document(auth.getCurrentUser().getPhoneNumber());
            dr.get().addOnSuccessListener(documentSnapshot -> {
                boolean i = documentSnapshot.getBoolean("indication");
                boolean c = documentSnapshot.getBoolean("case");
                String phone = documentSnapshot.getString("phone");
                new android.os.Handler().postDelayed(
                        () -> {
                            if (i) startTransmitter(phone, Constants.SUSPICIOUS);
                            else if (c) startTransmitter(phone, Constants.CASE);
                            else startTransmitter(phone, Constants.NORMAL);
                        },
                        5000);
            });
        };

        if (auth.getCurrentUser() != null) {
            DocumentReference dr = db.collection("users").document(auth.getCurrentUser().getPhoneNumber());
            dr.get().addOnSuccessListener(documentSnapshot -> {
                boolean i = documentSnapshot.getBoolean("indication");
                boolean c = documentSnapshot.getBoolean("case");
                String phone = documentSnapshot.getString("phone");
                if (i) startTransmitter(phone, Constants.SUSPICIOUS);
                else if (c) startTransmitter(phone, Constants.CASE);
                else startTransmitter(phone, Constants.NORMAL);
                regionBootstrap = new RegionBootstrap(this, region);
            });
        }
    }

    private void setForegroundScanner() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.heart);
        builder.setContentTitle(Constants.FOREGROUND_NOTIFICATION_TITLE);

        Intent notificationTapIntent = new Intent(this, NotificationActivity.class);
        PendingIntent notificationTapPending = PendingIntent.getActivity(
                this, 0, notificationTapIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent btnTapIntent = new Intent(this, SystemReceivers.class);
        btnTapIntent.putExtra("action", "stop_scanner");
        PendingIntent btnTapPending = PendingIntent.getBroadcast(this, 3, btnTapIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(notificationTapPending);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID,
                    Constants.NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(Constants.NOTIFICATION_CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }
        builder.addAction(R.drawable.no, "Taramayı durdur", btnTapPending);
        beaconManager.enableForegroundServiceScanning(builder.build(), 502);

        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);
        BackgroundPowerSaver backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    private void startTransmitter(String id2, String id3) {
        int isTransmitSupporting = BeaconTransmitter.checkTransmissionSupported(this);
        if (isTransmitSupporting != BeaconTransmitter.SUPPORTED) {
            return;
        }
        beaconTransmitter = new BeaconTransmitter(this, beaconParser);

        Beacon beacon = new Beacon.Builder()
                .setId1(("0x" + convertStringToHex("cvdtkp")))
                //phone of user
                .setId2(("0x" + convertStringToHex(id2)))
                //condition
                .setId3(id3)
                .setManufacturer(0x0118)
                .setDataFields(Arrays.asList(new Long[]{0l}))
                .setTxPower(-59)
                .build();

        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {

            @Override
            public void onStartFailure(int errorCode) {
            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.v("Advertisement started", settingsInEffect + "");
            }
        });
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        if (collection.size() > 0) {
            for (Beacon b : collection) {
                String id1 = convertHexToString(b.getId1().toHexString());
                String id2 = convertHexToString(b.getId2().toHexString());
                String id3 = b.getId3().toString();
                if (id2.equals(tmpId) && id3.equals(tmpCondition)) continue;
                if (!foundPeople.contains(id2)) {
                    foundPeople.add(id2);
                    UI.exec(id3);
                    increaseFoundPersonCount();
                }

                tmpId = id2;
                tmpCondition = id3;
                if (id1.equals(Constants.APP_UUID_STR)) {
                    if (foundUsers.contains(id2)) continue;
                    updateFoundUsers(id2, id3);
                    if (id3.equals(Constants.CASE) || id3.equals(Constants.SUSPICIOUS)) {
                        sendNotification();
                        foundUsers.add(id2);

                    }
                }
            }
        }
    }

    private void updateFoundUsers(String userId, String condition) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(userId, condition);

        DocumentReference dr = db.collection("users").document(auth.getCurrentUser().getPhoneNumber());
        dr.update("contacts", map).addOnSuccessListener(aVoid -> {
        });
    }

    private void increaseFoundPersonCount() {
        DocumentReference dr = db.collection("users").document(auth.getCurrentUser().getPhoneNumber());
        dr.update("contactedPeopleCount", FieldValue.increment(1));
    }

    @Override
    public void didEnterRegion(Region region) {
        beaconManager.addRangeNotifier(this);
        try {
            beaconManager.startRangingBeaconsInRegion(this.region);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didExitRegion(Region region) {
        beaconManager.removeAllRangeNotifiers();
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
    }

    private void sendNotification() {
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("238",
                    "cvdtkp", NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(this, channel.getId());
        }
        else {
            builder = new Notification.Builder(this);
            builder.setPriority(Notification.PRIORITY_MAX);
        }
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{0, 300, 0, 300, 0, 300, 0, 300, 0, 300, 0, 300}, 1);
        Uri uri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.warn_alert);
        Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
        ringtone.play();
        builder.setLights(Color.rgb(255, 0, 0), 3000, 3000);

        new android.os.Handler().postDelayed(
                vibrator::cancel,
                1700);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder.setSmallIcon(R.drawable.warnred);
        builder.setContentTitle(Constants.NOTIFICATION_RISK_TITLE);
        builder.setContentText(Constants.NOTIFICATION_RISK_CONTENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        notificationManager.notify(503, builder.build());
    }

    private int getUserCondition() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);
        String caseVal = pref.getString("case_switch", null);
        String indicationVal = pref.getString("indication_switch", null);
        AtomicReference<Boolean> c = null;
        AtomicReference<Boolean> i = null;
        CountDownLatch done = new CountDownLatch(1);

        if (caseVal == null || indicationVal == null) {
            DocumentReference dr = db.collection("users").document(auth.getCurrentUser().getPhoneNumber());
            dr.get().addOnSuccessListener(documentSnapshot -> {
                try {
                    i.set(documentSnapshot.getBoolean("indication"));
                    c.set(documentSnapshot.getBoolean("case"));
                }
                catch (NullPointerException e) {
                    i.set(false);
                    c.set(false);
                }
                done.countDown();
            });
            try {
                done.await();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            i.set(Boolean.parseBoolean(indicationVal));
            c.set(Boolean.parseBoolean(caseVal));
        }

        if (i == null && c == null) return 0;
        if (i.get()) return Integer.parseInt(Constants.SUSPICIOUS);
        if (c.get()) return Integer.parseInt(Constants.CASE);

        return 0;
    }

    public String convertStringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuilder hex = new StringBuilder();

        for (char aChar : chars) {
            hex.append(Integer.toHexString(aChar));
        }

        return hex.toString();
    }

    public String convertHexToString(String hexVal) {
        String hex = hexVal;
        if (hex.contains("x")) {
            hex = hexVal.substring(2);
        }

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }

        return sb.toString();
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * Activity yaşam döngüsü kontrolleri
     */

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (activity.getClass().getSimpleName().equals("MainActivity")) {
            if (auth.getCurrentUser() != null) {
                regionBootstrap = new RegionBootstrap(this, region);
                /*startTransmitter();*/
            }
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
