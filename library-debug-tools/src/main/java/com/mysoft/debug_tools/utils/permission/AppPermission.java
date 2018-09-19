package com.mysoft.debug_tools.utils.permission;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zourw on 2018/9/11.
 */
public class AppPermission {
    private Activity activity;
    private PermissionWatcher.OnGranted onGranted;
    private PermissionWatcher.OnRationale onRationale;
    private PermissionWatcher.OnDenied onDenied;
    private PermissionWatcher.OnCancel onCancel;

    private AppPermission() {
    }

    public static class Builder {
        private Activity activity;
        private PermissionWatcher.OnGranted onGranted;
        private PermissionWatcher.OnRationale onRationale;
        private PermissionWatcher.OnDenied onDenied;
        private PermissionWatcher.OnCancel onCancel;

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setOnGranted(PermissionWatcher.OnGranted onGranted) {
            this.onGranted = onGranted;
            return this;
        }

        public Builder setOnRationale(PermissionWatcher.OnRationale onRationale) {
            this.onRationale = onRationale;
            return this;
        }

        public Builder setOnDenied(PermissionWatcher.OnDenied onDenied) {
            this.onDenied = onDenied;
            return this;
        }

        public Builder setOnCancel(PermissionWatcher.OnCancel onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        public AppPermission build() {
            AppPermission permission = new AppPermission();
            permission.activity = activity;
            permission.onGranted = onGranted;
            permission.onRationale = onRationale;
            permission.onDenied = onDenied;
            permission.onCancel = onCancel;
            return permission;
        }
    }

    public void requestPermission(int requestCode, @NonNull String... permissions) {
        List<String> noPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                noPermissions.add(permission);
            }
        }

        if (noPermissions.isEmpty()) {
            if (onGranted != null) {
                onGranted.granted(requestCode);
            }
        } else {
            ActivityCompat.requestPermissions(activity, noPermissions.toArray(new String[noPermissions.size()]), requestCode);
        }
    }

    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        final List<String> deniedPermissions = new ArrayList<>();// 拒绝的
        final List<String> rationalePermissions = new ArrayList<>();// 可再次申请的

        for (int i = 0, length = permissions.length; i < length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                String permission = permissions[i];
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    rationalePermissions.add(permission);
                } else {
                    deniedPermissions.add(permission);
                }
            }
        }

        if (!rationalePermissions.isEmpty()) {// 此处可提醒用户只有开启权限才能使用该功能
            final String[] array = rationalePermissions.toArray(new String[rationalePermissions.size()]);
            if (onRationale == null) {
                showHintDialog(requestCode, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission(requestCode, array);
                    }
                });
            } else {
                onRationale.rationale(requestCode, array);
            }
            return;
        }

        if (deniedPermissions.isEmpty()) {// 用户同意该权限
            if (onGranted != null) {
                onGranted.granted(requestCode);
            }
        } else {// 用户拒绝该权限
            if (onDenied == null) {
                showHintDialog(requestCode, "去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        activity.startActivity(intent);
                    }
                });
            } else {
                onDenied.denied(requestCode, deniedPermissions.toArray(new String[deniedPermissions.size()]));
            }
        }
    }

    private void showHintDialog(final int requestCode, CharSequence positiveText, DialogInterface.OnClickListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setMessage("您需要给予权限才能继续该操作")
                .setPositiveButton(positiveText, listener)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onCancel != null) {
                            onCancel.cancel(requestCode);
                        }
                    }
                })
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}