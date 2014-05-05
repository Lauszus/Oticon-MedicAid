package com.tkjelectronics.oticonmedicaid;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

public class CalendarReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "CalendarReminderReceiver";
    public static final boolean D = false && MedicAidActivity.D; // This is automatically set when building

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(CalendarContract.ACTION_EVENT_REMINDER)) {
            Uri uri = intent.getData(); // See: https://github.com/android/platform_frameworks_base/blob/master/core/java/android/provider/CalendarContract.java#L2393-L2394
            if (uri == null || intent.getExtras() == null)
                return;

            long alarmTime = intent.getExtras().getLong(CalendarContract.CalendarAlerts.ALARM_TIME);

            if (D)
                Log.i(TAG, "URI: " + uri.toString() + " " + alarmTime);

            String[] instanceProjection = new String[]{
                    CalendarContract.Instances._ID,
                    CalendarContract.Instances.BEGIN,
                    CalendarContract.Instances.END,
                    CalendarContract.Instances.EVENT_ID,
            };
            String[] reminderProjection = new String[]{
                    CalendarContract.Reminders.EVENT_ID,
                    CalendarContract.Reminders.MINUTES,
                    CalendarContract.Reminders.METHOD,
            };

            ContentResolver cr = context.getContentResolver();
            Cursor instanceCursor = CalendarContract.Instances.query(cr, instanceProjection, alarmTime - 12 * 60 * 60 * 1000, alarmTime + 12 * 60 * 60 * 1000); // Search for event from 12 hour before and after alarm time

            String eventID;
            boolean eventFound = false;
            if (instanceCursor.moveToFirst()) {
                do {
                    String id = instanceCursor.getString(instanceCursor.getColumnIndex(CalendarContract.Instances._ID));
                    eventID = instanceCursor.getString(instanceCursor.getColumnIndex(CalendarContract.Instances.EVENT_ID));
                    long begin = Long.parseLong(instanceCursor.getString(instanceCursor.getColumnIndex(CalendarContract.Instances.BEGIN)));
                    if (D)
                        Log.i(TAG, "Data: " + id + " " + eventID + " " + begin + " " + instanceCursor.getString(instanceCursor.getColumnIndex(CalendarContract.Instances.END)));

                    Cursor reminderCursor = CalendarContract.Reminders.query(cr, Long.parseLong(eventID), reminderProjection);
                    if (reminderCursor.moveToFirst()) {
                        do {
                            long offset = Long.parseLong(reminderCursor.getString(reminderCursor.getColumnIndex(CalendarContract.Reminders.MINUTES))) * 60 * 1000; // Get reminder offset and convert to milliseconds
                            if (D)
                                Log.i(TAG, "Begin: " + begin + " Offset: " + offset + " AlarmTime: " + alarmTime);
                            if (begin - offset == alarmTime) { // Calculate alarm time from the begin time and minutes and compare with alarmTime to make 100% sure that it's the right event
                                eventFound = true;
                                if (D)
                                    Log.i(TAG, "Event found!");
                            }
                        } while (!eventFound && reminderCursor.moveToNext());
                    }
                    reminderCursor.close();
                } while (!eventFound && instanceCursor.moveToNext());
            }
            instanceCursor.close();

            if (eventFound) { // If the event is found, then open up the calendar
                Intent actIntent = new Intent();
                actIntent.setClassName(context.getPackageName(), context.getPackageName() + '.' + MedicAidActivity.class.getSimpleName());
                actIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                actIntent.putExtra(MedicAidActivity.EXTRA_ALARM, true);
                context.startActivity(actIntent);
            }
        }
    }
}
