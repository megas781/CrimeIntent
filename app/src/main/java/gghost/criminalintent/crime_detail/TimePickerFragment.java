package gghost.criminalintent.crime_detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import gghost.criminalintent.R;

public class TimePickerFragment extends DialogFragment {

    private static final String CRIME_TIME_KEY = "CRIME_TIME_KEY";
    private TimePicker mTimePicker;

    private Date mDateTime;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = v.findViewById(R.id.crime_time_picker_id);

        mDateTime = (Date) Objects.requireNonNull(getArguments()).getSerializable(CRIME_TIME_KEY);
        Calendar c = Calendar.getInstance();
        c.setTime(mDateTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(c.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setMinute(c.get(Calendar.MINUTE));
        } else {
            mTimePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setCurrentMinute(c.get(Calendar.MINUTE));
        }

        mTimePicker.setIs24HourView(true);

        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                //Изменяет время в дате
                //Закомментил, т.к. думаю, не имеет смысла постоянно обновлять mDateTime
                //Лучше просто сгенерирую дату на выходе
//                Calendar c = Calendar.getInstance();
//                c.setTime(mDateTime);
//                c.set(Calendar.HOUR, hourOfDay);
//                c.set(Calendar.MINUTE,minute);
//                mDateTime = c.getTime();
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //here we should rebuild the date, i.e. change its time
                        Intent i = new Intent();

                        Calendar c = Calendar.getInstance();
                        c.setTime(mDateTime);

                        //Достаем данные из mTimePicker'a и усанавливаем их в календарь c
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            c.set(Calendar.HOUR_OF_DAY, mTimePicker.getHour());
                            c.set(Calendar.MINUTE, mTimePicker.getMinute());
                        } else {
                            c.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
                            c.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
                        }

                        //Достаем из календаря новоиспеченноую дату
                        i.putExtra(CRIME_TIME_KEY, c.getTime());
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                    }
                })
                .create();
    }

    public static Date fetchDateFromIntent(Intent data) {
        return (Date) data.getSerializableExtra(CRIME_TIME_KEY);
    }

    public static TimePickerFragment newInstance(Fragment targetFragment, int requestCode, Date dateTime) {


        Bundle args = new Bundle();
        args.putSerializable(CRIME_TIME_KEY, dateTime);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);

        fragment.setTargetFragment(targetFragment, requestCode);

        return fragment;
    }
}
