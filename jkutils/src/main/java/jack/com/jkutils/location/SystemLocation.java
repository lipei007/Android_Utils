package jack.com.jkutils.location;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

public class SystemLocation {

    private Context mCtx;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private SystemLocationChangeCallback mCallback;

    public boolean adjustLocation = false;

    public interface SystemLocationChangeCallback {

        void onLocationChanged(Location location);
    }

    private static volatile SystemLocation systemLocationInstance;

    private SystemLocation() {

    }

    public static SystemLocation sharedLocation() {

        if (systemLocationInstance == null) {
            synchronized (SystemLocation.class) {
                if (systemLocationInstance == null) {
                    systemLocationInstance = new SystemLocation();
                }
            }
        }

        return systemLocationInstance;
    }


    public boolean requestLocation(Context context, SystemLocationChangeCallback callback) {

        if (context == null) {
            return false;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return false;
        }

        mCtx = context;
        mCallback = callback;

        //获取一个地址管理者，获取的方法比较特殊，不是直接new出来的
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        mLocationManager = locationManager;

        //使用GPS获取上一次的地址，这样获取到的信息需要多次，才能够显示出来，所以后面有动态的判断
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        handleMyLocation(location);

        //判断是否用户打开了GPS开关，这个和获取权限没关系
        boolean gpsEnable = GPSisopen(locationManager);

        startListenLocationUpdate();

        if (!gpsEnable) {
            openLocationSetting();
        }

        return true;
    }

    public void stopRequestLocation() {
        stopListenLocationUpdate();
    }

    private void startListenLocationUpdate() {

        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mLocationListener = new LocationListener() {

            @Override
            /*当地理位置发生改变的时候调用*/
            public void onLocationChanged(Location location) {

                if (location != null) {

                    handleMyLocation(location);
                }

            }

            /* 当状态发生改变的时候调用*/
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("GPS_SERVICES", "状态信息发生改变");

            }

            /*当定位者启用的时候调用*/
            @Override
            public void onProviderEnabled(String s) {
                Log.d("TAG", "onProviderEnabled: ");

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("TAG", "onProviderDisabled: ");
            }
        };

        //获取时时更新，第一个是Provider,第二个参数是更新时间1000ms，第三个参数是更新半径，第四个是监听器
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 8, mLocationListener);
    }

    private void stopListenLocationUpdate() {
        if (mLocationListener != null && mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
        mLocationListener = null;
        mLocationManager = null;
        mCtx = null;
    }

    private void openLocationSetting() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("请打开GPS连接");
        dialog.setMessage("为了获取定位服务，请先打开GPS");
        dialog.setPositiveButton("设置", new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //界面跳转
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mCtx.startActivity(intent);
            }
        });
        dialog.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        //调用显示方法！
        dialog.show();
    }



    //判断是否用户打开GPS开关，并作指导性操作！
    private boolean GPSisopen(LocationManager locationManager) {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            return false;
        }
        return true;
    }

    private void handleMyLocation(Location location) {

        if (location == null)
            return;

        if (adjustLocation) {

            double lat = location.getLatitude();
            double lon = location.getLongitude();

            double p[] = GeoUtils.wgs84togcj02(lon,lat);
            lon = p[0];
            lat = p[1];

            location.setLatitude(lat);
            location.setLongitude(lon);
        }

        if (mCallback != null) {
            mCallback.onLocationChanged(location);
        }
    }

}
