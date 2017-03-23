package com.example.ronmad.calendarhelper;

import android.animation.LayoutTransition;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout contentMain;
    private Button dateButton;
    private Spinner mSpinner;
    private Button temporaryOptionsButton;
    private Button shiftStartButton, shiftEndButton;
    private EditText eventTitleText;
    private GridLayout temporaryOptions;

    private static Calendar mCalendar;

    public static final int DAY = 0;
    public static final int NIGHT = 1;
    public static final int SAT = 2;
    public static final int SHIFT_START = 3;
    public static final int SHIFT_END = 4;

    private String startTime, endTime;
    private int startHour, endHour, startMinutes, endMinutes;

    private static String dayStart;
    private static String dayEnd;
    private static String nightStart;
    private static String nightEnd;
    private static String satStart;
    private static String satEnd;

    private String eventTitle;
    private String eventDescription;
    private String eventLocation;

    public boolean temporaryOptionsShown;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contentMain = (RelativeLayout) findViewById(R.id.include);

        dateButton = (Button) findViewById(R.id.calendarEventDate);

        mSpinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinner.setAdapter(adapter);

        temporaryOptionsButton = (Button) findViewById(R.id.temporaryOptionsButton);
        shiftStartButton = (Button) findViewById(R.id.shiftStartButton);
        shiftEndButton = (Button) findViewById(R.id.shiftEndButton);
        eventTitleText = (EditText) findViewById(R.id.eventTitleEditText);
        temporaryOptions = (GridLayout) findViewById(R.id.temporaryOptionsLayout);
        temporaryOptionsShown = true;

        mCalendar = new GregorianCalendar();
        setupDateDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getSharedPreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getSharedPreferences() {
        dayStart = sharedPreferences.getString(getString(R.string.day_start_key), getString(R.string.pref_default_day_start));
        dayEnd = sharedPreferences.getString(getString(R.string.day_end_key), getString(R.string.pref_default_day_end));
        nightStart = sharedPreferences.getString(getString(R.string.night_start_key), getString(R.string.pref_default_night_start));
        nightEnd = sharedPreferences.getString(getString(R.string.night_end_key), getString(R.string.pref_default_night_end));
        satStart = sharedPreferences.getString(getString(R.string.sat_start_key), getString(R.string.pref_default_sat_start));
        satEnd = sharedPreferences.getString(getString(R.string.sat_end_key), getString(R.string.pref_default_sat_end));
        eventTitle = sharedPreferences.getString(getString(R.string.event_title_key), getString(R.string.pref_default_event_title));
        eventDescription = sharedPreferences.getString(getString(R.string.event_description_key), "");
        eventLocation = sharedPreferences.getString(getString(R.string.event_location_key), "");
        if (temporaryOptionsShown)
            hideTemporaryOptions();
    }

    public void setupDateDisplay() {
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);
        dateButton.setText(format.format(mCalendar.getTime()));
        if (mCalendar.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY)
            mSpinner.setSelection(SAT);
        else if (mSpinner.getSelectedItemPosition() == SAT)
            mSpinner.setSelection(DAY);
    }

    public void setupTemporaryOptions() {

        String startTime = "";
        String endTime = "";
        switch (mSpinner.getSelectedItemPosition()) {
            case DAY:
                startTime = dayStart;
                endTime = dayEnd;
                break;
            case NIGHT:
                startTime = nightStart;
                endTime = nightEnd;
                break;
            case SAT:
                startTime = satStart;
                endTime = satEnd;
                break;
            default:
                break;
        }
        shiftStartButton.setText(startTime);
        shiftEndButton.setText(endTime);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String startTime = "";
                String endTime = "";
                switch (position) {
                    case DAY:
                        startTime = dayStart;
                        endTime = dayEnd;
                        break;
                    case NIGHT:
                        startTime = nightStart;
                        endTime = nightEnd;
                        break;
                    case SAT:
                        startTime = satStart;
                        endTime = satEnd;
                        break;
                    default:
                }
                shiftStartButton.setText(startTime);
                shiftEndButton.setText(endTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        eventTitleText.setText(eventTitle);
    }

    public void showDatePickerDialog(final View v) {
        DateDialog.listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mCalendar.set(year, month, day);
                setupDateDisplay();
                getSharedPreferences();
            }
        };
        DialogFragment newFragment = new DateDialog();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void addEventButton(View v) {
        int year = mCalendar.get(GregorianCalendar.YEAR);
        int month = mCalendar.get(GregorianCalendar.MONTH);
        int day = mCalendar.get(GregorianCalendar.DAY_OF_MONTH);

        Calendar begin = new GregorianCalendar();
        Calendar end = new GregorianCalendar();

        setupTimesFromSpinner();

        if (!timeToIntegers()) {
            Toast.makeText(this, "One or more time settings are invalid. Check your settings", Toast.LENGTH_LONG).show();
            return;
        }

        begin.set(year, month, day, startHour, startMinutes);
        end.set(year, month, day, endHour, endMinutes);

        String title = temporaryOptionsShown ? eventTitleText.getText().toString() : eventTitle;

        addEvent(begin, end, title);
    }

    public void addEvent(Calendar begin, Calendar end, String title) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, eventDescription)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, eventLocation)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.getTimeInMillis());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void setupTimesFromSpinner() {
        switch (mSpinner.getSelectedItemPosition()) {
            case DAY:
                startTime = dayStart;
                endTime = dayEnd;
                break;
            case NIGHT:
                startTime = nightStart;
                endTime = nightEnd;
                break;
            case SAT:
                startTime = satStart;
                endTime = satEnd;
                break;
            default:
                break;
        }
    }

    private boolean timeToIntegers() {
        if (!startTime.matches("([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]") ||
                !endTime.matches("([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]"))
            return false;

        String[] splitTime = startTime.split(":");
        startHour = Integer.parseInt(splitTime[0]);
        startMinutes = Integer.parseInt(splitTime[1]);

        splitTime = endTime.split(":");
        endHour = Integer.parseInt(splitTime[0]);
        endMinutes = Integer.parseInt(splitTime[1]);

        return true;
    }

    public void showTemporaryOptions(View v) {
        setupTemporaryOptions();
        contentMain.setLayoutTransition(new LayoutTransition());
        v.setVisibility(View.GONE);
        temporaryOptions.setVisibility(View.VISIBLE);
        temporaryOptionsShown = true;
    }

    public void hideTemporaryOptions() {
        contentMain.setLayoutTransition(null);
        temporaryOptions.setVisibility(View.GONE);
        temporaryOptionsButton.setVisibility(View.VISIBLE);
        temporaryOptionsShown = false;
    }

    public void showTimePickerDialog(final View v) {
        TimeDialog.listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = hourOfDay + ":";
                if (minute < 10)
                    time += "0";
                time += minute;
                switch (mSpinner.getSelectedItemPosition()) {
                    case DAY:
                        if (v.getId() == R.id.shiftStartButton)
                            dayStart = time;
                        else if (v.getId() == R.id.shiftEndButton)
                            dayEnd = time;
                        break;
                    case NIGHT:
                        if (v.getId() == R.id.shiftStartButton)
                            nightStart = time;
                        else if (v.getId() == R.id.shiftEndButton)
                            nightEnd = time;
                        break;
                    case SAT:
                        if (v.getId() == R.id.shiftStartButton)
                            satStart = time;
                        else if (v.getId() == R.id.shiftEndButton)
                            satEnd = time;
                        break;
                    default:
                        break;
                }
                ((Button)v).setText(time);
            }
        };
        switch (mSpinner.getSelectedItemPosition()) {
            case DAY:
                if (v.getId() == R.id.shiftStartButton)
                    TimeDialog.startend = SHIFT_START;
                else if (v.getId() == R.id.shiftEndButton)
                    TimeDialog.startend = SHIFT_END;
                TimeDialog.shift = DAY;
                break;
            case NIGHT:
                if (v.getId() == R.id.shiftStartButton)
                    TimeDialog.startend = SHIFT_START;
                else if (v.getId() == R.id.shiftEndButton)
                    TimeDialog.startend = SHIFT_END;
                TimeDialog.shift = NIGHT;
                break;
            case SAT:
                if (v.getId() == R.id.shiftStartButton)
                    TimeDialog.startend = SHIFT_START;
                else if (v.getId() == R.id.shiftEndButton)
                    TimeDialog.startend = SHIFT_END;
                TimeDialog.shift = SAT;
                break;
            default:
                break;
        }
        DialogFragment newFragment = new TimeDialog();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }


    public static class DateDialog extends DialogFragment {

        public static DatePickerDialog.OnDateSetListener listener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year = mCalendar.get(GregorianCalendar.YEAR);
            int month = mCalendar.get(GregorianCalendar.MONTH);
            int day = mCalendar.get(GregorianCalendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), listener, year, month, day);
        }
    }

    public static class TimeDialog extends DialogFragment {

        public static TimePickerDialog.OnTimeSetListener listener;
        public static int shift;
        public static int startend;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hour;
            int minute;
            String[] splitTime = {};
            switch (shift) {
                case DAY:
                    if (startend == SHIFT_START)
                        splitTime = dayStart.split(":");
                    else if (startend == SHIFT_END)
                        splitTime = dayEnd.split(":");
                    break;
                case NIGHT:
                    if (startend == SHIFT_START)
                        splitTime = nightStart.split(":");
                    else if (startend == SHIFT_END)
                        splitTime = nightEnd.split(":");
                    break;
                case SAT:
                    if (startend == SHIFT_START)
                        splitTime = satStart.split(":");
                    else if (startend == SHIFT_END)
                        splitTime = satEnd.split(":");
                    break;
            }
            hour = Integer.parseInt(splitTime[0]);
            minute = Integer.parseInt(splitTime[1]);
            return new TimePickerDialog(getActivity(), listener, hour, minute, true);
        }
    }
}
