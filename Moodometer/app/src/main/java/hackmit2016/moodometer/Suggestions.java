package hackmit2016.moodometer;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.model.Calendar;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import android.support.v4.app.FragmentManager;

import java.io.IOException;
import java.util.*;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class Suggestions extends Activity
        implements EasyPermissions.PermissionCallbacks,
        TimePickerDialog.OnTimeSetListener {
    GoogleAccountCredential mCredential;
    //private TextView mOutputText;
    //private Button mCallApiButton;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Calendar API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };


    private DatePicker datePickerStart;
    private java.util.Calendar startCalendar;
    private TextView dateViewStart;
    private int startYear, startMonth, startDay;

    private DatePicker datePickerEnd;

    private java.util.Calendar endCalendar;
    private TextView dateViewEnd;
    private int endYear, endMonth, endDay;

    private TextView timeViewStart;
    private java.util.Calendar startTimeCalendar;


    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //date stuff
        startCalendar = java.util.Calendar.getInstance();
        startYear = startCalendar.get(java.util.Calendar.YEAR);

        startMonth = startCalendar.get(startCalendar.MONTH);
        startDay = startCalendar.get(java.util.Calendar.DAY_OF_MONTH);

        endCalendar = java.util.Calendar.getInstance();
        endYear = endCalendar.get(java.util.Calendar.YEAR);

        endMonth = endCalendar.get(endCalendar.MONTH);
        endDay = endCalendar.get(java.util.Calendar.DAY_OF_MONTH);

        //time stuff
        startTimeCalendar = java.util.Calendar.getInstance();


        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Calendar API ...");

        setContentView(R.layout.activity_suggestions);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    public void returnToMainLoop(View view) {
        Intent intent = new Intent(Suggestions.this, ContactsActivity.class);
        startActivity(intent);
    }

    //time stuff
    public void showTimePickerDialog(View view) {
        int hour_of_day = startTimeCalendar.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = startTimeCalendar.get(java.util.Calendar.MINUTE);
        getFragmentManager().beginTransaction().add(TimePickerDialogFragment.instantiate(hour_of_day, minute), "time-picker").commit();
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(java.util.Calendar.MINUTE, minute);

        timeViewStart = (TextView) findViewById(R.id.start_time_text);

        StringBuilder timeString = new StringBuilder();
        if (hourOfDay < 10) {
            timeString.append("0");
        }
        timeString.append(hourOfDay).append(":");
        if (minute < 10) {
            timeString.append("0");
        }
        timeString.append(minute);
        timeViewStart.setText(timeString);
    }

    //date stuff
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(view.getId());

        //Toast.makeText(getApplicationContext(), "X", Toast.LENGTH_SHORT)
        //        .show();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == R.id.new_goal_startDate) {
            return new DatePickerDialog(this, myDateListenerStart, startYear, startMonth, startDay);
        } else if (id == R.id.new_goal_endDate){
            return new DatePickerDialog(this, myDateListenerEnd, endYear, endMonth, endDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListenerStart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3, R.id.new_goal_startDate);
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListenerEnd = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3, R.id.new_goal_endDate);
        }
    };

    private void showDate(int year, int month, int day, int id) {
        if (id == R.id.new_goal_startDate) {
            dateViewStart = (TextView) findViewById(R.id.start_date_text);
            StringBuilder dateText = new StringBuilder().append(year).append("-");
            if (month < 10) {
                dateText.append("0");
            }
            dateText.append(month).append("-");
            if (day < 10) {
                dateText.append("0");
            }
            dateText.append(day);
            dateViewStart.setText(dateText);
        } else if (id == R.id.new_goal_endDate) {
            dateViewEnd = (TextView) findViewById(R.id.end_date_text);
            StringBuilder dateText = new StringBuilder().append(year).append("-");
            if (month < 10) {
                dateText.append("0");
            }
            dateText.append(month).append("-");
            if (day < 10) {
                dateText.append("0");
            }
            dateText.append(day);
            dateViewEnd.setText(dateText);
        }
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount("get", null);
        } else if (! isDeviceOnline()) {
            //mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential, "get", this).execute();
        }
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    public void createGoal(View view) {
        Log.d("debug", "why is this not triggering?");
        if (! isGooglePlayServicesAvailable()) {
            Log.d("debug", "googleplay");
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            Log.d("debug","null cred, calling choose account with 'add' param");
            chooseAccount("add", view);
        } else if (! isDeviceOnline()) {
            //mOutputText.setText("No network connection available.");
            Log.d("debug", "no internet");
        } else {
            Log.d("debug","addinggg");
            new MakeRequestTask(mCredential, "add", this).execute();

            TextView msgTextView = ((TextView) findViewById(R.id.goals_message));

            System.out.println("where");
            msgTextView.setVisibility(View.VISIBLE);
            Log.d("debug", "did");
            msgTextView.setText("Goal created! Check your calendar!");
            //System.out.println("did");

            System.out.println("this");
            ((Button) findViewById(R.id.goal_return_button)).setVisibility(View.VISIBLE);
            System.out.println("fail");
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount(String requestType, View view) {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                Log.d("debug", "what is requesttype: " + requestType);
                if (requestType == "get") {
                    getResultsFromApi();
                } else {
                    createGoal(view);
                }
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    /*
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                                    */
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                Suggestions.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;
        private String requestType = null;
        private Context ctx = null;

        public MakeRequestTask(GoogleAccountCredential credential, String requestType, Context context) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Moodometer")
                    .build();
            this.requestType = requestType;
            Log.d("androidapp", "requesttype: " + this.requestType);
            ctx = context;
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                if (requestType.equals("get")) {
                    Log.d("androidapp", "fetching");
                    return getDataFromApi();
                } else {
                    Log.d("androidapp", "adding calendar event");
                    addCalendarEvent();
                    return new ArrayList<String>();
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private void addCalendarEvent() throws IOException {
            Event suggestion = new Event()
                    .setSummary(((EditText)findViewById(R.id.new_goal)).getText().toString())
                    .setDescription("Moodometer task");
            String startDateString = ((TextView)findViewById(R.id.start_date_text)).getText().toString() + "T" +
                    ((TextView)findViewById(R.id.start_time_text)).getText().toString() + ":00Z";
                Log.d("debug", "startdatestring: " + startDateString);
            DateTime startDateTime = new DateTime(startDateString);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime);
            suggestion.setStart(start);

            DateTime endDateTime = new DateTime(startDateTime.getValue() + 3600 * 1000 * Integer.parseInt(((TextView)findViewById(R.id.end_time_text)).getText().toString()) );
                Log.d("debug", "startdate long val: " + startDateTime.getValue());
            Log.d("debug", "enddate long val: " + endDateTime.getValue());
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime);
            suggestion.setEnd(end);

            Event.Reminders reminders = new Event.Reminders()
                    .setUseDefault(false);
            suggestion.setReminders(reminders);

            String calendarId = mService.calendars().get("primary").getCalendarId();

            mService.events().insert(calendarId, suggestion).execute();
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));
            }

            return eventStrings;
        }

        @Override
        protected void onPreExecute() {
            //mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (requestType == "get") {
                if (output == null || output.size() == 0) {
                    //mOutputText.setText("No results returned.");
                } else {
                    output.add(0, "Data retrieved using the Google Calendar API:");
                    //mOutputText.setText(TextUtils.join("\n", output));
                }
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Suggestions.REQUEST_AUTHORIZATION);
                } else {
                    //mOutputText.setText("The following error occurred:\n"
                    //        + mLastError.getMessage());
                }
            } else {
                //mOutputText.setText("Request cancelled.");
            }
        }
    }
}