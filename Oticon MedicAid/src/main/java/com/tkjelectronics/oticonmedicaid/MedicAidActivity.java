package com.tkjelectronics.oticonmedicaid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

// TODO: Dismiss notification using button and make notification
public class MedicAidActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "MedicAidActivity";
    public static final boolean D = BuildConfig.DEBUG; // This is automatically set when building

    public static final String EXTRA_ALARM = "ALARM";
    //public static final String EXTRA_PLAYER_POS = "PLAYER_POS";

    public static final int REMINDER_FRAGMENT = 0;
    public static final int CALENDAR_FRAGMENT = 1;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private CalendarReminderReceiver mCalendarReminderReceiver = new CalendarReminderReceiver();

    private MediaPlayer mMediaPlayer;
    //private int playerPosition = 0; // Zero by default
    //private static final int notificationID = 0;

    private AlarmFragment mAlarmFragment;
    private CalendarFragment mCalendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D)
            Log.d(TAG, "---- onCreate ----");
        setContentView(R.layout.activity_medic_aid);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        IntentFilter filter = new IntentFilter(CalendarContract.ACTION_EVENT_REMINDER);
        filter.addDataScheme("content");
        registerReceiver(mCalendarReminderReceiver, filter);
    }

    public void startMediaPlayer() {
        //playerPosition = pos;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

/*
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (D)
            Log.d(TAG, "---- onRestoreInstanceState ----");

        int pos = savedInstanceState.getInt(EXTRA_PLAYER_POS, -1);
        if (pos != -1) {
            if (D)
                Log.i(TAG, "Restoring MediaPlayer");
            startMediaPlayer(pos); // Restore last player position
            restoreAlarmFragment(); // Restore the fragment as well
        } else if (D)
            Log.i(TAG, "DO NOT Restoring MediaPlayer");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (D)
            Log.d(TAG, "---- onSaveInstanceState ----");

        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
            playerPosition = mMediaPlayer.getCurrentPosition();
        else
            playerPosition = -1;
        outState.putInt(EXTRA_PLAYER_POS, playerPosition);
    }
*/
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (D)
            Log.d(TAG, "---- onNewIntent ----");
        boolean alarmFlag = intent.getBooleanExtra(EXTRA_ALARM, false);

        if (D)
            Log.i(TAG, "Alarm flag: " + alarmFlag);
        if (alarmFlag)
            restoreAlarmFragment();
    }

    private void restoreAlarmFragment() {
        replaceFragment(REMINDER_FRAGMENT, true);
        mNavigationDrawerFragment.setFocus(REMINDER_FRAGMENT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (D)
            Log.d(TAG, "---- onDestroy ----");
        stopMediaPlayer();
        unregisterReceiver(mCalendarReminderReceiver);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .create().show();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        replaceFragment(position, false); // Update the main content by replacing fragments
    }

    private void replaceFragment(int position, boolean alarmFlag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position == REMINDER_FRAGMENT) {
            mTitle = getString(R.string.reminders_title);
            if (mAlarmFragment == null || alarmFlag)
                mAlarmFragment = new AlarmFragment(alarmFlag);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mAlarmFragment, mTitle.toString())
                    .commit();
        } else if (position == CALENDAR_FRAGMENT) {
            mTitle = getString(R.string.calendar_title);
            if (mCalendarFragment == null)
                mCalendarFragment = new CalendarFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mCalendarFragment, mTitle.toString())
                    .commit();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.medic_aid, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
            return true;
        return super.onOptionsItemSelected(item);
    }

    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            //showNotification(notificationID);

            mMediaPlayer = MediaPlayer.create(this, R.raw.sounds_882_solemn);
            mMediaPlayer.setVolume(1.0f, 1.0f);
            mMediaPlayer.setLooping(true);
            /*if (playerPosition > 0)
                mMediaPlayer.seekTo(playerPosition);*/
            mMediaPlayer.start(); // No need to call prepare(); create() does that for you
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        } else
            stopMediaPlayer();
    }
/*
    private void showNotification(int id) {
        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this)
                .addParentStack(MedicAidActivity.class) // Adds the back stack for the Intent (but not the Intent itself)
                .addNextIntent(new Intent(this, MedicAidActivity.class)); // Adds the Intent that starts the Activity to the top of the stack

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(getString(R.string.takePill))
                //.setContentText("Press here to dismiss")
                .setSmallIcon(android.R.drawable.ic_dialog_alert);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            notification = builder.build(); // This is only available from API level 16
        else
            notification = builder.getNotification();

        notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR | Notification.FLAG_SHOW_LIGHTS;

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, notification); // Show notification
    }

    private void hideNotification(int id) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id); // Hide notification
    }
*/
    public void stopMediaPlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;

            //hideNotification(notificationID);
        }
    }
}
