package com.mysoft.debug_tools.utils;

import android.os.AsyncTask;

import timber.log.Timber;

/**
 * Created by Zourw on 2018/9/14.
 */
public class SimpleTask<Params, Result> extends AsyncTask<Params, Void, Result> {
    private Callback<Params, Result> callback;

    public SimpleTask() {
    }

    public SimpleTask(Callback<Params, Result> callback) {
        this.callback = callback;
    }

    public void setCallback(Callback<Params, Result> callback) {
        this.callback = callback;
    }

    private Callback<Params, Result> getCallback() {
        return callback;
    }

    @SafeVarargs
    @Override
    protected final Result doInBackground(Params... params) {
        if (getCallback() != null) {
            try {
                return getCallback().doInBackground(params);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else {
            Timber.w("doInBackground: getCallback() == null");
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        if (getCallback() != null) {
            getCallback().onPreExecute();
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (getCallback() != null) {
            try {
                getCallback().onPostExecute(result);
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                callback = null;
            }
        } else {
            Timber.w("onPostExecute");
        }
    }

    public interface Callback<T, K> {
        void onPreExecute();

        K doInBackground(T[] params);

        void onPostExecute(K result);
    }
}
