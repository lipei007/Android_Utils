package jack.com.jkutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

public class JKUtils {

    public static String md5OfString(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    public static String getApplicationName(Context context) {

        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    public static SharedPreferences sharedPreferences(Context context, String key) {

        SharedPreferences pref = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Context c= context.createDeviceProtectedStorageContext();
            pref = c.getSharedPreferences(key, 0);
        } else {
            pref = context.getSharedPreferences(key, 0);
        }
        return pref;
    }

    public static String getDeviceId(Context context) {
        return getAndroidId(context);
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean isDebugMode(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getSystemLanguageCode(Context context) {
        Locale curLocale = context.getResources().getConfiguration().locale;
        String languageCode = curLocale.getLanguage();
        return languageCode;
    }

    public static String[] list2Array(List list) {
        if (list != null) {
            return (String[])list.toArray(new String[list.size()]);
        }
        return null;
    }
}
