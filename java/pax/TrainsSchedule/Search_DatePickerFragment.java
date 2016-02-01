package pax.TrainsSchedule;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

public class Search_DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnDatePicked mOnDatePicked;


    public interface OnDatePicked {
        void onDatePicked(int year, int monthOfYear, int dayOfMonth);
    }

    private long date;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnDatePicked = (OnDatePicked) activity;
        } catch (ClassCastException e) {

            throw new ClassCastException(activity.toString() + " must implement onDatePicked");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            date = args.getLong("DATE");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        Date dDate = new Date(date);
        c.setTime(dDate);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mOnDatePicked.onDatePicked(year, month, day);
    }

}
