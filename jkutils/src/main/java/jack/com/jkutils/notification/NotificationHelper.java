package jack.com.jkutils.notification;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {

    public static class NotificationContent {

        public static final String ExtraKey = "extra";

        private String title;
        private String body;
        private int id;
        private String extra;

        private int smallIcon;
        private int largeIcon;

        /**
         * smallIcon like Driver R.drawable.small_icon_clear
         * largeIcon like Driver R.drawable.large_notification_icon_clear
         * */
        public NotificationContent(int id, String title, String body, int smallIcon, int largeIcon, String extra) {
            this.id = id;
            this.title = title;
            this.body = body;
            this.extra = extra;
            this.smallIcon = smallIcon;
            this.largeIcon = largeIcon;
        }
    }

    public static void showNotification(Application application, NotificationContent content, String channelId, String channelName, Class activityCls) {
        if (content == null) {
            return;
        }

        if (TextUtils.isEmpty(content.title) || TextUtils.isEmpty(content.body)) {
            return;
        }

        Intent intent = new Intent(application.getApplicationContext(), activityCls);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP); // getIntent可能是null
        if (content.extra != null) {
            intent.putExtra(NotificationContent.ExtraKey,content.extra); // 程序在后台的情况下，点击通知将程序唤醒到前台时，并不能取得extra
        }

        /**
         *
         * requestCode: 需要保证不同，否则id不通的intent取到的extra也是同一个
         * */
        int requestCode = content.id;
        PendingIntent contentIntent = PendingIntent.getActivity(application.getApplicationContext(), requestCode, intent,FLAG_UPDATE_CURRENT);

        //1.获取系统通知的管理者
        NotificationManager nm = (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);

        Notification noti = null;
        long[] vibrates = { 0, 1000, 1000, 1000 };

//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri soundUri = Settings.System.DEFAULT_NOTIFICATION_URI;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            /**
             * Oreo不用Priority了，用importance
             * IMPORTANCE_NONE 关闭通知
             * IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
             * IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
             * IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
             * IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
             */
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            // 震动
            channel.enableVibration(true);
            channel.setVibrationPattern(vibrates);

            channel.enableLights(true);

            channel.setSound(soundUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);

            nm.createNotificationChannel(channel);

            noti = new NotificationCompat.Builder(application, channelId)
                    .setContentTitle(content.title)
                    .setContentText(content.body)
                    .setSmallIcon(content.smallIcon)
                    .setLargeIcon(BitmapFactory.decodeResource(application.getResources(),content.largeIcon))
                    .setContentIntent(contentIntent)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setSound(soundUri)
                    .setLights(0xff00bbff,500,200)
                    .build();
        } else {

            noti = new Notification.Builder(application)
                    .setContentTitle(content.title)
                    .setContentText(content.body)
                    .setSmallIcon(content.smallIcon)
                    .setLargeIcon(BitmapFactory.decodeResource(application.getResources(),content.largeIcon))
                    .setContentIntent(contentIntent)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setSound(soundUri)
                    .setLights(0xff00bbff,500,20)
                    .build();

        }

        /**
         * vibrate属性是一个长整型的数组，用于设置手机静止和振动的时长，以毫秒为单位。
         * 参数中下标为0的值表示手机静止的时长，下标为1的值表示手机振动的时长， 下标为2的值又表示手机静止的时长，以此类推。
         */
        noti.vibrate = vibrates;

        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        nm.notify(content.id, noti);
    }
}
