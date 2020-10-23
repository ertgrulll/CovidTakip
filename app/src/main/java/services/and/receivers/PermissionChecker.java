package services.and.receivers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import static android.content.ContentValues.TAG;
import static services.and.receivers.Constants.PERMISSION_REQUEST_BACKGROUND_LOCATION;
import static services.and.receivers.Constants.PERMISSION_REQUEST_FINE_LOCATION;

public class PermissionChecker implements ActivityCompat.OnRequestPermissionsResultCallback {
    Activity activity;

    public PermissionChecker(Activity activity) {
        this.activity = activity;
    }

    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (activity.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(Constants.LOCATION_PERMISSION_TITLE);
                        builder.setMessage(Constants.LOCATION_PERMISSION_MSG);
                        builder.setPositiveButton("İzin Ver", null);
                        builder.setOnDismissListener(dialog -> activity.requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                PERMISSION_REQUEST_BACKGROUND_LOCATION));
                        builder.show();
                    }
                    else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(Constants.PERMISSIONS_LIMITED_FUNC);
                        builder.setMessage(Constants.LOCATION_PERMISSION_REJECT_MSG);
                        builder.setPositiveButton("Kapat", null);
                        builder.setOnDismissListener(dialog -> {
                        });
                        builder.show();
                    }
                }
            }
            else {
                if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            PERMISSION_REQUEST_FINE_LOCATION);
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(Constants.PERMISSIONS_LIMITED_FUNC);
                    builder.setMessage(Constants.LOCATION_PERMISSION_REJECT_MSG);
                    builder.setPositiveButton("Kapat", null);
                    builder.setOnDismissListener(dialog -> {
                    });
                    builder.show();
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(Constants.LOCATION_PERMISSION_TITLE);
                builder.setMessage(Constants.LOCATION_PERMISSION_MSG);
                builder.setPositiveButton("İzin Ver", null);
                builder.setOnDismissListener(dialog -> activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constants.PERMISSION_REQUEST_COARSE_LOCATION));
                builder.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "fine location permission granted");
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(Constants.PERMISSIONS_LIMITED_FUNC);
                    builder.setMessage(Constants.LOCATION_PERMISSION_REJECT_MSG);
                    builder.setPositiveButton("Kapat", null);
                    builder.setOnDismissListener(dialog -> {
                    });
                    builder.show();
                }
                return;
            }
            case Constants.PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(Constants.PERMISSIONS_LIMITED_FUNC);
                    builder.setMessage(Constants.LOCATION_PERMISSION_REJECT_MSG);
                    builder.setPositiveButton("Kapat", null);
                    builder.setOnDismissListener(dialog -> {
                    });
                    builder.show();
                }
                return;
            }
            case PERMISSION_REQUEST_BACKGROUND_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "background location permission granted");
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(Constants.PERMISSIONS_LIMITED_FUNC);
                    builder.setMessage(Constants.LOCATION_PERMISSION_REJECT_MSG);
                    builder.setPositiveButton("Kapat", null);
                    builder.setOnDismissListener(dialog -> {
                    });
                    builder.show();
                }
            }
        }
    }
}
