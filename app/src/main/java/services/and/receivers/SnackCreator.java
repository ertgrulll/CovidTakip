package services.and.receivers;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.covidtracker.R;
import com.google.android.material.snackbar.Snackbar;

import interfaces.Task;

public class SnackCreator {

    public static void showSnack(Activity activity, String msg, View... view) {
        View snackView;
        if (view.length != 0) {
            snackView = view[0];
        }
        else snackView = activity.findViewById(android.R.id.content);
        snackView.bringToFront();
        snackView.setTranslationZ(10);
        snackView.setElevation(10);
        Snackbar snackbar = Snackbar.make(snackView, msg, Snackbar.LENGTH_LONG);
        snackbar.setDuration(4000);

        Snackbar.SnackbarLayout snackOuterLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackOuterLayout.setBackgroundColor(activity.getResources().getColor(R.color.navIconSelected));
        snackOuterLayout.bringToFront();
        TextView tvSnack = snackOuterLayout.findViewById(com.google.android.material.R.id.snackbar_text);
        tvSnack.bringToFront();
        tvSnack.setGravity(Gravity.CENTER);
        tvSnack.setMaxLines(10);
        snackbar.show();
    }

    @SafeVarargs
    public static void showActionSnack(Activity activity, String msg, String btnTxt, Task<Snackbar>... action) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);

        Snackbar.SnackbarLayout snackOuterLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackOuterLayout.setBackgroundColor(activity.getResources().getColor(R.color.navIconSelected));
        TextView tvSnack = snackOuterLayout.findViewById(com.google.android.material.R.id.snackbar_text);
        tvSnack.setGravity(Gravity.CENTER);
        tvSnack.setMaxLines(10);
        snackbar.setDuration(6000);
        snackbar.setActionTextColor(Color.WHITE);

        if (action != null) {
            snackbar.setAction(btnTxt, view -> action[0].exec(snackbar));
        }
        else {
            snackbar.setAction(btnTxt, view -> snackbar.dismiss());
        }

        snackbar.show();
    }
}
