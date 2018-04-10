package vn.com.mattana.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import vn.com.mattana.dms.R;

/**
 * Created by HAI on 2/24/2018.
 */

public class Commons {

    public Snackbar makeSnackbar(final View view, final String message) {
        return Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
    }

    public Toast makeToast(final Context context, final String message) {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);

    }

    public void showToastDisconnect(final Context context) {
        Toast.makeText(context, "Mất kết nối internet", Toast.LENGTH_LONG).show();
    }

    public void showAlertCancel(Activity activity, String tile, String content, DialogInterface.OnClickListener listener) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setIcon(R.mipmap.ic_logo);
        dialog.setTitle(tile)
                .setMessage(content)
                .setPositiveButton("Đồng ý", listener).setNegativeButton("Thôi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    public void showAlertCancelHandle(Activity activity, String tile, String content, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener cancel) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setIcon(R.mipmap.ic_logo);
        dialog.setTitle(tile)
                .setMessage(content)
                .setPositiveButton("Đồng ý", listener).setNegativeButton("Thôi", cancel);

        dialog.setCancelable(false);
        dialog.show();
    }

    public void showAlertInfo(Activity activity,String tile, String content, DialogInterface.OnClickListener listener) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setIcon(R.mipmap.ic_logo);
        dialog.setTitle(tile)
                .setMessage(content)
                .setPositiveButton("Đồng ý", listener);
        dialog.setCancelable(false);
        dialog.show();
    }
    public void startActivity(Activity activity, Class activityTo) {
        Intent intent = new Intent(activity, activityTo);


        activity.startActivity(intent);
    }

    public Intent createIntent(Activity activity, Class activityTo) {
        Intent intent = new Intent(activity, activityTo);
        return intent;
    }


    public void writeFile(String json, String path, Activity activity) {
        try {

            if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
                File file = new File(activity.getExternalFilesDir("MATTANA"), path);

                if (!file.exists()) {
                    file.mkdirs();
                }

                FileWriter writer = new FileWriter(file.getAbsoluteFile() + path);
                writer.write(json);
                writer.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void deleteFile(String path, Activity activity) {
        try {

            if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
                File file = new File(activity.getExternalFilesDir("MATTANA"), path);

                if (file != null){
                    file.delete();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public BufferedReader readBufferedReader(String path, Activity activity) {
        try {
            if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {

                File file = new File(activity.getExternalFilesDir("MATTANA"), path);

                BufferedReader br = new BufferedReader(
                        new FileReader(file.getAbsoluteFile() +  path));

                return  br;
            }

        } catch (IOException e) {
            return null;
        }

        return null;
    }



}
