package com.mysoft.debug_tools.utils.permission;

/**
 * Created by Zourw on 2018/9/11.
 */
public class PermissionWatcher {
    public interface OnGranted {
        void granted(int requestCode);
    }

    public interface OnRationale {
        void rationale(int requestCode, final String[] permissions);
    }

    public interface OnDenied {
        void denied(int requestCode, final String[] permissions);
    }

    public interface OnCancel {
        void cancel(int requestCode);
    }
}