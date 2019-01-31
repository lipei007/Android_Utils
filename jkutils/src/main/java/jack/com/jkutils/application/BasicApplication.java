package jack.com.jkutils.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import android.content.ServiceConnection;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;



public class BasicApplication extends Application {

    // region Activity Life
    private boolean isBackground = false;

    public boolean isBackground() {
        return isBackground;
    }

    private class LifeCallback implements ActivityLifecycleCallbacks {

        private int activityStartCount = 0;
        private Activity mCurrentActivity = null;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityStartCount++;
            mCurrentActivity = activity;
        }

        @Override
        public void onActivityResumed(Activity activity) {

            if(isBackground==true)
            {
                isBackground = false;
                applicationDidEnterForeground();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

            activityStartCount--;
            if(activityStartCount == 0)
            {
                isBackground = true;
                applicationDidEnterBackground();
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
    // endregion

    // region Life Circle

    private static BasicApplication instance;
    private LifeCallback mLifeCallback;
    private ServiceConnection mServiceConnection = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mLifeCallback = new LifeCallback();
        registerActivityLifecycleCallbacks(mLifeCallback);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterActivityLifecycleCallbacks(mLifeCallback);
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
    }

    // endregion

    // region Getter
    public static BasicApplication sharedApplication() {
        return instance;
    }

    public Activity getCurrentActivity() {
        if (mLifeCallback != null) {

            return mLifeCallback.mCurrentActivity;
        }
        return null;
    }

    // endregion

    // region Callback

    public void applicationDidEnterBackground() {

    }

    public void applicationDidEnterForeground() {

    }

    // endregion

    // region Preference

    public SharedPreferences sharedPreferences(String key) {

        SharedPreferences pref=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            Context c= this.createDeviceProtectedStorageContext();
            pref = c.getSharedPreferences(key, 0);
        }
        else
        {
            pref = getSharedPreferences(key, 0);
        }
        return pref;
    }

    // endregion

}
