package com.creativeconflux.srdp.mycard.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.creativeconflux.srdp.mycard.R;
import com.creativeconflux.srdp.mycard.listeners.OnPermissionListener;

import java.util.ArrayList;
import java.util.List;

import static com.creativeconflux.srdp.mycard.utils.AppConstants.REQUEST_PERMISSION_SETTINGS;
import static com.creativeconflux.srdp.mycard.utils.SharedPref.getEditor;
import static com.creativeconflux.srdp.mycard.utils.SharedPref.getSharedPref;

/**
 * Created by srdpatel on 3/4/2018.
 */

public class PermissionUtils {

    private boolean sentToSettings;
    private Activity activity;
    private Fragment fragment;
    private String permissionReason;
    private String deniedMsg;
    private OnPermissionListener onPermissionListener;

    public PermissionUtils (Activity activity, Fragment fragment,
                            OnPermissionListener onPermissionListener){
        this.activity = activity;
        this.fragment = fragment;
        this.onPermissionListener = onPermissionListener;
    }

    public boolean isAllPermissionsGranted(Context mContext, String[] permissions, String permissionReason) {
        this.permissionReason = permissionReason;
        //Check if permission has granted or not
        if (!hasPermission(mContext, permissions)) {
            //Check if permission has denied with or without "don't ask again" option
            //If "don't ask again" was not checked, show reason for permission
            if (shouldShowRational(permissions, mContext)) {
                showPermissionReason(mContext, permissions);
                //If "don't ask again" was checked, show dialog to redirect to permission settings
            } else if (getSharedPref(mContext).getBoolean(PrefKeys.ALREADY_ASKED_PERMISSION, false)) {
                redirectToSettings(mContext);
                //Otherwise, it is very first time the permission dialog is going to be opened
            } else {
                askForPermission(permissions, mContext);
            }
            //Store that we have already asked for permission despite of what user has granted and what not
            getEditor(mContext).putBoolean(PrefKeys.ALREADY_ASKED_PERMISSION, true);
            getEditor(mContext).apply();
            return false;
        } else {
            return true;
        }
    }

    private boolean hasPermission(Context mContext, String[] permissions) {
        for (int i = 0; i < permissions.length - 1; i++) {
            if (ActivityCompat.checkSelfPermission(mContext, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldShowRational(String[] permissions, Context mContext) {
        List<String> nonGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length - 1; i++) {
            if (ActivityCompat.checkSelfPermission(mContext, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                nonGranted.add(permissions[i]);
            }
        }
        String[] permissionsToCheck = (String[]) nonGranted.toArray(new String[0]);
        if (fragment != null){
            for (int i = 0; i < permissionsToCheck.length - 1; i++) {
                if (!fragment.shouldShowRequestPermissionRationale(permissionsToCheck[i])) {
                    return false;
                }
            }
        } else if (activity != null) {
            for (int i = 0; i < permissionsToCheck.length - 1; i++) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionsToCheck[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showPermissionReason(final Context mContext, final String[] permissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.title_dialog_permission));
        builder.setMessage(permissionReason);
        builder.setPositiveButton(mContext.getResources().getString(R.string.label_grant), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                askForPermission(permissions, mContext);
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void redirectToSettings(final Context mContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.title_dialog_permission));
        builder.setMessage(mContext.getResources().getString(R.string.des_permission));
        builder.setPositiveButton(mContext.getResources().getString(R.string.label_grant), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                sentToSettings = true;
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(mContext.getResources().getString(R.string.scheme_package), mContext.getPackageName(), null);
                intent.setData(uri);
                // TODO: 3/4/2018 by sagar  Callback?
                if (fragment != null){
                    fragment.startActivityForResult(intent, REQUEST_PERMISSION_SETTINGS);
                } else if (activity != null){
                    activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTINGS);
                }
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void askForPermission(String[] permissions, Context mContext) {
        List<String> nonGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length - 1; i++) {
            if (ActivityCompat.checkSelfPermission(mContext, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                nonGranted.add(permissions[i]);
            }
        }
        String[] pendingPermissions = (String[]) nonGranted.toArray(new String[0]);
        if (fragment != null){
            fragment.requestPermissions(pendingPermissions, AppConstants.REQUEST_PERMISSION_CALLBACK);
        } else if (activity != null){
            ActivityCompat.requestPermissions(activity, pendingPermissions, AppConstants.REQUEST_PERMISSION_CALLBACK);
        }
    }
}
