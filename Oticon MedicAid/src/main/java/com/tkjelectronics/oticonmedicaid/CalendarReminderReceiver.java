package com.tkjelectronics.oticonmedicaid;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

public class CalendarReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "CalendarReminderReceiver";
    public static final boolean D = MedicAidActivity.D; // This is automatically set when building

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(CalendarContract.ACTION_EVENT_REMINDER)) {
            Uri uri = intent.getData(); // See: https://github.com/android/platform_frameworks_base/blob/master/core/java/android/provider/CalendarContract.java#L2393
            if (uri == null)
                return;

            if (D)
                Log.i(TAG, "URI: " + uri.toString() + " " + ContentUris.parseId(uri));

            long begin = ContentUris.parseId(uri) - 60 * 1000; // Starting time in milliseconds
            long end = ContentUris.parseId(uri) + 60 * 1000; // Ending time in milliseconds
            String[] projection = new String[]{
                    CalendarContract.Instances._ID,
                    CalendarContract.Instances.BEGIN,
                    CalendarContract.Instances.END,
                    CalendarContract.Instances.EVENT_ID
            };
            Cursor cursor = CalendarContract.Instances.query(context.getContentResolver(), projection, begin, end);

            String eventID = "";
            if (cursor.moveToFirst()) {
                // TOOD: Just replaces this with:
                // cursor.moveToLast();
                // eventID = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.EVENT_ID));
                do {
                    if (D)
                        Log.i(TAG, "Data: " + cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3) + " " + cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.EVENT_ID)));
                    eventID = cursor.getString(cursor.getColumnIndex(CalendarContract.Instances.EVENT_ID));
                } while (cursor.moveToNext());
            }
            cursor.close();

            if (!eventID.isEmpty()) {
                uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(eventID));
                intent = new Intent(Intent.ACTION_VIEW)
                        .setData(uri);
                context.startActivity(intent);
            }
        }
    }
}
