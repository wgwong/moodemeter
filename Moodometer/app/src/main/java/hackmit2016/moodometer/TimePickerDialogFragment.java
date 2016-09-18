package hackmit2016.moodometer;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private static final String ARG_HOUR_OF_DAY = "hour_of_day";
    private static final String ARG_MINUTE = "minute";

    private int hourOfDay;
    private int minute;

    public static TimePickerDialogFragment instantiate(int hour_of_day, int minute) {
        final Bundle args = new Bundle();
        args.putInt(ARG_HOUR_OF_DAY, hour_of_day);
        args.putInt(ARG_MINUTE, minute);

        final TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        hourOfDay = args.getInt(ARG_HOUR_OF_DAY);
        minute = args.getInt(ARG_MINUTE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(
                getActivity(),
                this,
                hourOfDay,
                minute,
                DateFormat.is24HourFormat(getActivity())
        );
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Activity activity = getActivity();
        if (activity instanceof TimePickerDialog.OnTimeSetListener) {
            ((TimePickerDialog.OnTimeSetListener) activity).onTimeSet(view, hourOfDay, minute);
        }

    }
}