package com.tkjelectronics.oticonmedicaid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MedicAidActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static final String TAG = "MedicAidActivity";
    public static final boolean D = BuildConfig.DEBUG; // This is automatically set when building

    public static final String EXTRA_ALARM = "ALARM";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private CalendarReminderReceiver mCalendarReminderReceiver = new CalendarReminderReceiver(this);

    AlarmFragment mAlarmFragment;
    CalendarFragment mCalendarFragment;

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (D)
            Log.d(TAG, "---- onDestroy ----");
        boolean alarmFlag = intent.getBooleanExtra(EXTRA_ALARM, false);

        if (D)
            Log.i(TAG, "Alarm flag: " + alarmFlag);
        if (alarmFlag) {
            replaceFragment(0, true);
            mNavigationDrawerFragment.setFocus(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (D)
            Log.d(TAG, "---- onDestroy ----");
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
        // Update the main content by replacing fragments
        replaceFragment(position, false);
    }

    private void replaceFragment(int position, boolean alarmFlag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position == 0) {
            mTitle = getString(R.string.reminders_title);
            if (mAlarmFragment == null || alarmFlag)
                mAlarmFragment = new AlarmFragment(alarmFlag);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mAlarmFragment, mTitle.toString())
                    .commit();
        } else if (position == 1) {
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
}
