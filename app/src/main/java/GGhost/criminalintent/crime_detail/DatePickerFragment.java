package GGhost.criminalintent.crime_detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

import GGhost.criminalintent.R;

public class DatePickerFragment extends DialogFragment {

    /** Константа дублирует ту, что в активности. Я нашёл в этом смысл.
     * Активности нужна константа даты для того, чтобы создавать интенты для других активностей.
     * Этому фрагменту нужна константа, чтобы устанавливать Result для активности, чтобы она могла
     * принимать дату и передавать
     * */
    private static final String CRIME_DATE_KEY = "CRIME_DATE_KEY";

    private DatePicker mCrimeDatePicker;
    private Button mOkButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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


        View v = inflater.inflate(R.layout.dialog_date, null);

        mCrimeDatePicker = v.findViewById(R.id.dialog_date_picker_id);
        mCrimeDatePicker.init(year, month, day, mOnCrimeDateChangeListener);

        mOkButton = v.findViewById(R.id.dialog_date_picker_ok_button_id);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Java убрала все полезные методы класса Data и перенесла их в календари.
                Почему здесь мы используем GregorianCalendar, а не
                */
                Calendar c = Calendar.getInstance();

                int year = mCrimeDatePicker.getYear();
                int month = mCrimeDatePicker.getMonth();
                int day = mCrimeDatePicker.getDayOfMonth();

                //Устанавливаем изначально дату, которую приняли, чтобы сохранить время
                c.setTime(date);
                //Изменяем день-месяц-год
                c.set(year, month, day);

                DatePickerFragment.this.setResult(Activity.RESULT_OK, c.getTime());
                DatePickerFragment.this.dismiss();
            }
        });

        return v;
    }

    /**
     * Метод, определяющий инициализацию создаваемого модального окна.
     *
     * @param savedInstanceState
     * @return
     */


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
     *
     * @param resultCode
     * @param date
     */
    private void setResult(int resultCode, Date date) {
        Intent i = new Intent();
        i.putExtra(CRIME_DATE_KEY, date);
//        this.setResul
    }

    public static Date getDateFromIntent(Intent i) {
        return (Date) i.getSerializableExtra(CRIME_DATE_KEY);
    }
}
