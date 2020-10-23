package activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import services.and.receivers.BTService;
import services.and.receivers.Constants;
import services.and.receivers.SessionChecker;
import services.and.receivers.SnackCreator;

public class Loader extends AppCompatActivity {
    int counter = 0;
    FirebaseAuth firebaseAuth;
    private BTService btService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        btService = new BTService(this.getActivityResultRegistry(), this);
        getLifecycle().addObserver(btService);
    }

    @Override
    protected void onStart() {
        super.onStart();
        btService.requestEnableBtIfRequire();
        checkStatus();
    }

    private void checkStatus() {
        final FutureTask<String> sessionCheckerTask = new FutureTask<>(new SessionChecker(this));
        Thread t = new Thread(sessionCheckerTask);
        t.start();
        try {
            String res = sessionCheckerTask.get();
            switch (res) {
                case "connection err":
                    showSnack();
                    break;
                case "not logged":
                    startActivity(new Intent(Loader.this, Register.class));
                    break;
                case "ok":
                    startActivity(new Intent(Loader.this, MainActivity.class));
                    break;
            }
        }
        catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        t.interrupt();
    }

    //İnternet bağlantısı uyarısı, tekrar deneme(2 defa) ve uygulamayı kapat
    private void showSnack() {
        if (counter == 2) {
            SnackCreator.showActionSnack(this, Constants.CONNECTION_ERR_MSG_LAST, "Kapat"
                    , view -> android.os.Process.killProcess(android.os.Process.myPid()));
        }
        else {
            SnackCreator.showActionSnack(this, Constants.CONNECTION_ERR_MSG, "Tekrar dene", view -> checkStatus());
        }
        this.counter++;
    }
}