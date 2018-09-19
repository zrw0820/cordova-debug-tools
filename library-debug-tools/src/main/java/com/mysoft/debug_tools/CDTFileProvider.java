package com.mysoft.debug_tools;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.View;

import com.mysoft.debug_tools.crash.CrashKep;
import com.mysoft.debug_tools.ui.view.EntranceView;
import com.mysoft.debug_tools.utils.Utils;

import java.util.Set;

import timber.log.Timber;

/**
 * Created by Zourw on 2018/9/15.
 */
public class CDTFileProvider extends FileProvider {
    @Override
    public boolean onCreate() {
        if (getContext() instanceof Application) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected void log(int priority, String tag, @NonNull String message, Throwable t) {
                    super.log(priority, "Debug-Tools", "TAG: " + tag + ", Message: " + message, t);
                }
            });
            CrashKep.bind(getContext());

            final EntranceView entranceView = new EntranceView(getContext());
            ((Application) getContext()).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    if (isLauncher(activity.getIntent())) {
                        if (Utils.checkPermission(activity)) {
                            entranceView.open();
                        }
                    }
                }

                @Override
                public void onActivityStarted(Activity activity) {
                }

                @Override
                public void onActivityResumed(Activity activity) {
                    if (isLauncher(activity.getIntent())) {
                        entranceView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    if (isLauncher(activity.getIntent())) {
                        entranceView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onActivityStopped(Activity activity) {
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (isLauncher(activity.getIntent())) {
                        entranceView.close();
                    }
                }
            });

            SensorManager manager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            if (manager != null) {
                Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                manager.registerListener(new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        if (event.sensor.getType() == 1) {
                            if (Utils.checkIfShake(event.values[0], event.values[1], event.values[2])) {
                                entranceView.open();
                            }
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    }
                }, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
        return super.onCreate();
    }

    private boolean isLauncher(Intent intent) {
        Set<String> categories = intent.getCategories();
        return categories != null && !categories.isEmpty() && categories.contains(Intent.CATEGORY_LAUNCHER);
    }
}