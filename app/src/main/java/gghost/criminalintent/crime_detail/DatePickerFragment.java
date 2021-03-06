package gghost.criminalintent.crime_detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import gghost.criminalintent.R;



public class DatePickerFragment extends DialogFragment {
    //Интерфейс делегата
    interface DatePickerFragmentDelegate extends Serializable {
        public void onDatePickerConfirmed(Date date);
    }
    /**
     * Константа дублирует ту, что в активности. Я нашёл в этом смысл.
     * Активности нужна константа даты для того, чтобы создавать интенты для других активностей.
     * Этому фрагменту нужна константа, чтобы устанавливать Result для активности, чтобы она могла
     * принимать дату и передавать
     */
    private static final String CRIME_DATE_KEY = "CRIME_DATE_KEY";
    public static final String DELEGATE_SERIALIZABLE_KEY = "DELEGATE_SERIALIZABLE_KEY";

    private DatePicker mCrimeDatePicker;
    private Button mOkButton;
    private DatePickerFragmentDelegate delegate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Здесь по идеи должен быть Re-parceled объект
        if (savedInstanceState != null) {
            if (savedInstanceState.getSerializable(DELEGATE_SERIALIZABLE_KEY) != null) {
                //Сохраняем делегата
                DatePickerFragment.this.delegate = (DatePickerFragmentDelegate) savedInstanceState.getSerializable(DELEGATE_SERIALIZABLE_KEY);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
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

                DatePickerFragment.this.delegate.onDatePickerConfirmed(c.getTime());
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

    //Новый проприетарный инициализатор, принимающий на вход дату преступления
    public static DatePickerFragment newInstance(Date crimeDate, @Nullable DatePickerFragmentDelegate delegate) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_DATE_KEY, crimeDate);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        if (delegate != null) {
            fragment.delegate = delegate;
        }
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //remark: Serializable preserves hashCode
        outState.putSerializable(DELEGATE_SERIALIZABLE_KEY, this.delegate);
    }
}
