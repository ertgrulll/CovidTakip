package services.and.receivers;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SessionChecker implements Callable<String> {
    private final FirebaseAuth mAuth;
    Activity activity;

    public SessionChecker(Activity activity) {
        mAuth = FirebaseAuth.getInstance();
        this.activity = activity;
    }

    @Override
    public String call() {
        if (checkConnection()) {
            if (checkLoginStatus()) {
                return "ok";
            }
            else return "not logged";
        }
        else {
            return "connection err";
        }
    }

    //İnternet bağlantısı kontrolü
    private boolean checkConnection() {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    return InetAddress.getByName("google.com");
                }
                catch (UnknownHostException e) {
                    return null;
                }
            });
            inetAddress = future.get(8000, TimeUnit.MILLISECONDS);
            future.cancel(true);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return inetAddress != null && !inetAddress.toString().equals("");
    }

    private boolean checkLoginStatus() {
        /*SharedPreferences.Editor editor = activity.getApplicationContext().getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE).edit();*/

        FirebaseUser currentUser = mAuth.getCurrentUser();

        return currentUser != null;
    }
}
