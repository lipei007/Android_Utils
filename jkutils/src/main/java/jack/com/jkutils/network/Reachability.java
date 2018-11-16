package jack.com.jkutils.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.net.NetworkInfo.State.UNKNOWN;

public class Reachability {

    private static volatile Reachability instance;
    private Context mCtx;
    private NetworkStateChangeCallback mCallback;
    private NetworkStatus mStatus;

    private Reachability() {

    }

    public static Reachability sharedReachability(Context context) {
        if (instance == null) {
            synchronized (Reachability.class) {
                if (instance == null) {
                    instance = new Reachability();
                    instance.mCtx = context;
                }
            }
        }
        return instance;
    }

    public void startMonitor(NetworkStateChangeCallback callback) {

        if (mCtx != null) {
            mCallback = callback;
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            mCtx.registerReceiver(networkStateReceiver, filter);
        }
    }

    public void stopMonitor() {
        if (mCtx != null) {
            mCallback = null;
            mCtx.unregisterReceiver(networkStateReceiver);
        }
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
                if (mCallback != null) {

                    boolean connected = networkInfo != null;
                    NetworkInfo.State netState = UNKNOWN;
                    if (connected) {
                        netState = networkInfo.getState();
                        connected = connected && (netState == NetworkInfo.State.CONNECTED);
                    }

                    if (!connected) {

                        mStatus = NetworkStatus.NETWORK_STATUS_NOT_CONNECTED;

                    } else {

//                        int networkType = networkInfo.getType();
//                        boolean isWifi = networkType == ConnectivityManager.TYPE_WIFI || networkType == ConnectivityManager.TYPE_ETHERNET;

                        final android.net.NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        final android.net.NetworkInfo mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                        if (wifi.isAvailable()) {

                            mStatus = NetworkStatus.NETWORK_STATUS_WIFI;
                        } else if (mobile.isAvailable()) {

                            mStatus = NetworkStatus.NETWORK_STATUS_WWAN;
                        } else {

                            mStatus = NetworkStatus.NETWORK_STATUS_UNKNOWN;
                        }

                    }

                    mCallback.networkStateChanged(mStatus,networkInfo);
                }

            }
        }
    };

    public interface NetworkStateChangeCallback {
        void networkStateChanged(NetworkStatus status, NetworkInfo networkInfo);
    }

    public enum NetworkStatus {
        NETWORK_STATUS_NOT_CONNECTED,
        NETWORK_STATUS_WIFI,
        NETWORK_STATUS_WWAN,
        NETWORK_STATUS_UNKNOWN
    }
}
