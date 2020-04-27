package GGhost.criminalintent.crime_detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import GGhost.criminalintent.R;

import static android.content.ContentValues.TAG;

public class DatePickerFragment extends DialogFragment {

    private static final String CRIME_DATE_KEY = "CRIME_DATE_KEY";

    private DatePicker mCrimeDatePicker;

    /**
     * Метод, определяющий инициализацию создаваемого модального окна.
     *
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        /* Не знаю, почему, но вся логика, которая раньше производилась в onCreateView, в DialogFragment'е
         * перетекла в onCreateDialog. */
        /* Сначала достаем значения аргументов (в данном случае только один: дата преступления) */
        /* Сам по себе класс Date это не более чем временная метка. У него нет никаких полезных методов.
         * Экземпляр Date не выдать значение года или дня месяца и т.д. Всем этим занимается объект Calendar */
        final Date date = (Date) getArguments().getSerializable(CRIME_DATE_KEY);
        /* Видимо, Calendar выполнен в Android синглтоном. Хотя никто не мешает создать свой календарь. */
        Calendar c = Calendar.getInstance();
        /* Говорим календарю, с какой датой нужно работать. */
        c.setTime(date);

        /* для удобства сохраняем компоненты даты в переменные */
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        mCrimeDatePicker = v.findViewById(R.id.dialog_date_picker);
        mCrimeDatePicker.init(year, month, day, mOnCrimeDateChangeListener);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_of_crime_label)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mCrimeDatePicker.getYear();
                        int month = mCrimeDatePicker.getMonth();
                        int day = mCrimeDatePicker.getDayOfMonth();
                        /* Java убрала все полезные методы класса Data и перенесла их в календари.
                        Почему здесь мы используем GregorianCalendar, а не
                        */
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day);
                        Date newDate = new GregorianCalendar(year, month, day).getTime();
                        DatePickerFragment.this.sendResult(Activity.RESULT_OK,newDate);
                    }
                })
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    //Слушатель изменений в  mCrimeDatePicker'e
    private DatePicker.OnDateChangedListener mOnCrimeDateChangeListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        }
    };

//    public void sendResult(int)

    //Новый проприетарный инициализатор, принимающий на вход дату преступления
    public static DatePickerFragment newInstance(Date crimeDate) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_DATE_KEY, crimeDate);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Метод отправляет данные родительскому фрагменту по средствам непосредственного обращения к нему
     * через метод getTargetFragment() и getTargetRequestCode()
     * @param resultCode
     * @param date
     */
    private void sendResult(int resultCode, Date date) {
        Intent i = new Intent();
        i.putExtra(CRIME_DATE_KEY, date);
        try {
            this.getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
        } catch (NullPointerException e) {
            Log.e("NullPointerException", "sendResult: Null target fragment for DatePickerFragment instance");
        }
    }

    public static Date getDateFromIntent(Intent i) {
        return (Date) i.getSerializableExtra(CRIME_DATE_KEY);
    }
}
