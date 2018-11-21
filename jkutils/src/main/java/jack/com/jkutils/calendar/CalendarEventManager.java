package jack.com.jkutils.calendar;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.TimeZone;

/**
 * <uses-permission android:name="android.permission.READ_CALENDAR" />
 * <uses-permission android:name="android.permission.WRITE_CALENDAR" />
 * */
public class CalendarEventManager {


    /**
     *
     *  startTimeInMillis
     *
     * Calendar beginTime = Calendar.getInstance();
     * beginTime.set(2012, 0, 19, 7, 30);
     * beginTime.getTimeInMillis();
     *
     * */
    public static class CalendarEvent {

        public long eventID;
        public long startTimeInMillis;
        public long endTimeInMillis;
        public String title;
        public String description;
        public int alarmMinutes; // 提前多少分钟提醒

        public CalendarEvent() {
            eventID = -1;
            alarmMinutes = 30;
        }

        @Override
        public String toString() {

            return String.format("EventID: %d StartTime: %d EndTime: %d Title: %s Description: %s",eventID, startTimeInMillis, endTimeInMillis, title, description);
        }

        private Uri getUri() {
            if (eventID < 0) {
                return null;
            }
            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
            return uri;
        }

        public long getEventID() {
            return eventID;
        }

        public long getStartTimeInMillis() {
            return startTimeInMillis;
        }

        public long getEndTimeInMillis() {
            return endTimeInMillis;
        }

        public String getTitle() {
            if (title == null) {
                title = "";
            }
            return title;
        }

        public String getDescription() {
            if (description == null) {
                description = "";
            }
            return description;
        }

        public int getAlarmMinutes() {
            return alarmMinutes;
        }
    }

    private static volatile CalendarEventManager manager;

    private Context mCtx;

    private CalendarEventManager() {

    }

    private CalendarEventManager(Context context) {
        mCtx = context;
    }

    public static CalendarEventManager sharedManager(Context context) {
        if (manager == null) {
            synchronized (CalendarEventManager.class) {
                if (manager == null) {
                    manager = new CalendarEventManager(context);
                }
            }

        }
        return manager;
    }

    /**
     * 创建
     * */

    public void createNewCalendarEvent(CalendarEvent event) {

        if (event != null) {
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getStartTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEndTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, event.getTitle())
                    .putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription())
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

//                .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
//                .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");

            mCtx.startActivity(intent);
        }

    }

    public long createNewCalendarEventID(CalendarEvent event) {

        if (event != null) {

            String calID = "1";

            ContentResolver cr = mCtx.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, event.getStartTimeInMillis());
            values.put(CalendarContract.Events.DTEND, event.getEndTimeInMillis());
            values.put(CalendarContract.Events.TITLE, event.getTitle());
            values.put(CalendarContract.Events.DESCRIPTION, event.getDescription());
            values.put(CalendarContract.Events.CALENDAR_ID, calID);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());


            if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return -1;
            }

            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // get the event ID that is the last element in the Uri
            long eventID = Long.parseLong(uri.getLastPathSegment());

            event.eventID = eventID;

            // 添加提醒
            addAlarmForEvent(event.getAlarmMinutes(), eventID);

            return eventID;

        }
        return -1;
    }

    /**
     * 查询
     * */

    private static final String[] EVENT_PROJECTION = new String[]{
            "_id",                                  // 0
            CalendarContract.Events.DTSTART,        // 1
            CalendarContract.Events.DTEND,          // 2
            CalendarContract.Events.TITLE,          // 3
            CalendarContract.Events.DESCRIPTION     // 4
    };

    public CalendarEvent eventUriWithIdentifier(long id) {

        ContentResolver cr = mCtx.getContentResolver();

        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id);

        Cursor cur = cr.query(uri, EVENT_PROJECTION, null, null, null);

        CalendarEvent event = null;
        while (cur.moveToNext()) {

            long eventID = cur.getLong(0);
            long startTime = cur.getLong(1);
            long endTime = cur.getLong(2);
            String title = cur.getString(3);
            String description = cur.getString(4);

            event = new CalendarEvent();
            event.eventID = eventID;
            event.startTimeInMillis = startTime;
            event.endTimeInMillis = endTime;
            event.title = title;
            event.description = description;
        }
        return event;
    }

    public interface QueryCallback {
        void fetchEvents(ArrayList<CalendarEvent> events);
    }

    public void eventsWithStartTimeAndEndTime(long startTimeInMillis, long endTimeInMillis, QueryCallback callback) {

        ContentResolver cr = mCtx.getContentResolver();

        String selection = "((" + CalendarContract.Events.DTSTART + " >= ?) AND (" + CalendarContract.Events.DTEND + " <= ?))";

        String[] selectionArgs = new String[]{String.format("%d", startTimeInMillis), String.format("%d", endTimeInMillis)};

        Uri uri = CalendarContract.Events.CONTENT_URI;



        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Cursor cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        ArrayList<CalendarEvent> events = new ArrayList<>();
        while (cur.moveToNext()) {

            long eventID = cur.getLong(0);
            long startTime = cur.getLong(1);
            long endTime = cur.getLong(2);
            String title = cur.getString(3);
            String description = cur.getString(4);

            CalendarEvent event = new CalendarEvent();
            event.eventID = eventID;
            event.startTimeInMillis = startTime;
            event.endTimeInMillis = endTime;
            event.title = title;
            event.description = description;

            events.add(event);
        }

        if (callback != null) {
            callback.fetchEvents(events);
        }
    }

    /**
     * 编辑
     * */

    /**
     * 调用系统界面编辑
     * */
    public void editCalendarEvent(CalendarEvent event) {
        if (event != null && event.getUri() != null) {

            Intent intent = new Intent(Intent.ACTION_EDIT)
                    .setData(event.getUri())
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startTimeInMillis)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endTimeInMillis)
                    .putExtra(CalendarContract.Events.TITLE, event.title)
                    .putExtra(CalendarContract.Events.DESCRIPTION, event.description);

            mCtx.startActivity(intent);
        }
    }

    public void addAlarmForEvent(long minutes, long eventID) {

        ContentResolver cr = mCtx.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.MINUTES, minutes);
        values.put(CalendarContract.Reminders.EVENT_ID, eventID);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
    }

    public boolean saveCalendarEvent(CalendarEvent event) {
        if (event != null && event.getUri() != null) {

            ContentResolver cr = mCtx.getContentResolver();
            ContentValues values = new ContentValues();

            values.put(CalendarContract.Events.DTSTART, event.getStartTimeInMillis());
            values.put(CalendarContract.Events.DTEND, event.getEndTimeInMillis());
            values.put(CalendarContract.Events.TITLE, event.getTitle());
            values.put(CalendarContract.Events.DESCRIPTION, event.getDescription());

            int rows = cr.update(event.getUri(), values, null, null);

            return rows >= 0;
        }
        return false;
    }

    /**
     * 删除
     * */
    public boolean removeEvent(CalendarEvent event) {

        if (event != null && event.getUri() != null) {

            ContentResolver cr = mCtx.getContentResolver();

            int rows = cr.delete(event.getUri(), null, null);

            return rows != -1;

        }
        return false;
    }
}
