package com.ncsavault.views;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;

import applicationId.R;

import java.util.ArrayList;

/**
 * PermissionActivity is used to check for runtime permissions
 */
public abstract class PermissionActivity extends FragmentActivity {

    /**
     * This will check all must permission at the same time
     */
    public static final int PERMISSION_REQUEST_MUST = 101;

    public String[] writeExternalStorage = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private int permissionRequestCode;
    private final Object extras = null;

    AlertDialog alertDialog;

    private boolean havePermission(String permission) {
        return Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissionRequestCode) {

            if (grantResults.length >= 1) {
                boolean anyDenied = false;
                for (int grantResult : grantResults) {
                    // Check if the only required permission has been granted
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        anyDenied = true;


                    }
                }

                if (anyDenied) {
                    onPermissionResult(requestCode, false, extras);
                    String tag = "PermissionActivity";
                    Log.d(tag, "extra" + extras);
                } else {
                    onPermissionResult(requestCode, true, extras);
                }
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /**
     * This method will check all necessary permission to get
     *
     * @return permission
     */
    public boolean haveAllMustPermissions(String[] permissions) {
        this.permissionRequestCode = PermissionActivity.PERMISSION_REQUEST_MUST;
        ArrayList<String> permissionsNotAvail = new ArrayList<>();

        //collecting permission which are not given by user
        for (String permission : permissions) {
            if (!havePermission(permission)) {
                permissionsNotAvail.add(permission);
            }
        }

        if (permissionsNotAvail.size() > 0) {
            //adding in string array
            String[] neededPermissions = new String[permissionsNotAvail.size()];
            for (int i = 0; i < permissionsNotAvail.size(); i++) {
                neededPermissions[i] = permissionsNotAvail.get(i);
                Log.d("PermissionActivity",">>permission needed of =" + neededPermissions[i]);
            }

            ActivityCompat.requestPermissions(this, neededPermissions, PermissionActivity.PERMISSION_REQUEST_MUST);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Method is used to show confirmation dialog for runtime permissions
     * @param message set the message string
     */
    public void showPermissionsConfirmationDialog(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Permission Denied");
        alertDialogBuilder
                .setMessage(message);


        alertDialogBuilder.setPositiveButton("Go to Settings",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        goToSettings();

                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // showPermissionsConfirmationDialog(GlobalConstants.UGA_VAULT_PERMISSION);
                        dialog.dismiss();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(this,R.color.green));
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(ContextCompat.getColor(this,R.color.green));
    }

    public abstract void onPermissionResult(int requestCode, boolean isGranted, Object extras);

    /**
     * Method is used to go to settings screen
     */
    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, 500);
    }

}
