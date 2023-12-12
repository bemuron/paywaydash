package com.payway.dashboard.app;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.payway.dashboard.data.AppExecutors;
import com.payway.dashboard.data.database.DashboardDatabase;
import com.payway.dashboard.helpers.SessionManager;

/**
 * Created by BE on 3/17/2018.
 *
 */

public class AppController extends Application implements LifecycleObserver {

    public static final String TAG = AppController.class
            .getSimpleName();
    private final AppExecutors executors = AppExecutors.getInstance();

    public boolean myApplicationStatus;
    public static boolean isAppInBg;
    private SessionManager pref;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        mInstance = this;

//        final QonversionConfig qonversionConfig = new QonversionConfig.Builder(
//                this,
//                "ObSCZXzMPntLst1p4eabPv0S33i4Iqfb",
//                QLaunchMode.SubscriptionManagement
//        ).build();
//        Qonversion.initialize(qonversionConfig);

    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public SessionManager getPrefManager() {
        if (pref == null) {
            pref = new SessionManager(this);
        }

        return pref;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void connectListener() {
        Log.d(TAG, "resumed observing lifecycle.");
        mInstance.myApplicationStatus = true;
        isAppInBg = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void disconnectListener() {
        Log.d(TAG, "paused observing lifecycle.");
        mInstance.myApplicationStatus = false;
        isAppInBg = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        Log.d(TAG, "Returning to foreground…");
        isAppInBg = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        Log.d(TAG, "Moving to background…");
        isAppInBg = true;
    }

    /**
     * methods for accessing singletons.
     */
    public DashboardDatabase getDatabase() {
        return DashboardDatabase.getInstance(this);
    }

    public static boolean isNetworkAvailable(Context context) {
        if(context == null)  return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        return true;
                    }
                }
            }

            else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_status", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_status", "" + e.getMessage());
                }
            }
        }
        Log.i("update_status","Network is available : FALSE ");
        return false;
    }
}
