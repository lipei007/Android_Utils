package jack.com.jkutils.application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;


public class CrashHandler implements Thread.UncaughtExceptionHandler{

    public interface InfoPreserver {
        public void saveUserInformation();
        public void handleCrashInfo(String deviceInfo, String exception);
    }
    // 当前应用上下文
    private Context ctx;

    // 系统默认UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static volatile CrashHandler sharedInstance;
    public InfoPreserver preserver;

    private CrashHandler(Context context) {

    }

    public static synchronized CrashHandler getSharedInstance(Context context) throws NullPointerException {

        if (context == null) {
            NullPointerException nullPointerException = new NullPointerException();
            throw nullPointerException;
        }

        if (sharedInstance == null) {
            sharedInstance = new CrashHandler(context);
        }
        return sharedInstance;
    }

    private void init(Context ctx) {
        this.ctx = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 如果不能处理异常，并且系统处理器不为空，则交给系统处理
        if (!handleException(e) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t,e);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable e) {

        if (e == null) {
            return false;
        }

        if (preserver != null) {
            preserver.saveUserInformation();
            preserver.handleCrashInfo(getDeviceInfo(),getThrowableStackString(e));
        }


        return true;
    }

    private String getDeviceInfo() {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getApplicationContext().getPackageName(), PackageManager.GET_ACTIVITIES);
            String appVer = "App Version: " + pi.versionName + "_" + pi.versionCode + "\n";
            String osVer = "OS Version: " + Build.VERSION.RELEASE + "_" + Build.VERSION.SDK_INT + "\n";
            String manufacture = "Manufacturer: " + Build.MANUFACTURER + "\n";
            String model = "Model: " + Build.MODEL;
            return appVer + osVer + manufacture + model;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    private String getThrowableStackString(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        LineNumberReader reader = new LineNumberReader(new StringReader(sw.toString()));
        StringBuffer lines = new StringBuffer();
        try {
            String line = reader.readLine();
            while (line != null) {
                lines.append(line + "\n");
                line = reader.readLine();
            }
        } catch (IOException ex) {
            lines.append(ex.toString() + "\n");
        }

        return lines.toString();
    }


}
