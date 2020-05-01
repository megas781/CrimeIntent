package gghost.criminalintent.crime_detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import gghost.criminalintent.R;
import gghost.criminalintent.model.Crime;
import gghost.criminalintent.model.CrimeLab;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID_KEY = "ARG_CRIME_ID_KEY";
    private static final String ARG_IS_NEW_KEY = "ARG_IS_NEW_KEY";
    //Тег для FragmentManager'a
//    private static final String DATE_PICKER_FRAGMENT_TAG = "DATE_PICKER_FRAGMENT_TAG";
    //Код для TargetFragment'a
    private static final int DATE_PICKER_FRAGMENT_REQUEST_CODE = 0;
    private static final int TIME_PICKER_FRAGMENT_REQUEST_CODE = 1;

    private static final int MENU_ITEM_DELETE_CRIME_ID = 1;

    private Crime mCrime;
    private boolean mIsNew;
    private EditText mTitleTextField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mIsSolvedCheckbox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("getArguments()    : " + (getArguments() != null));
        System.out.println("savedInstanceState: " + (savedInstanceState != null));

        //Миссия: достать crimeId

        UUID crimeId = null;
        //Сначала пытаемся достать ID из состояния. Оно приорететно над аргументом
        if (savedInstanceState != null) {
            //Если есть сохранение в состоянии, то используем его
            crimeId = (UUID) savedInstanceState.getSerializable(ARG_CRIME_ID_KEY);
        }
        //Если в состоянии ничего не нашлось, то хотя бы из аргументов нужно подгрузить
        if (crimeId == null) {
            crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID_KEY);
        }
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mIsNew = getArguments().getBoolean(ARG_IS_NEW_KEY, false);

        this.setHasOptionsMenu(true);


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_CRIME_ID_KEY, mCrime.getId());
    }

    @Override
    public void onPause() {
        super.onPause();
        //Обновляем данные о преступлении. Это нужно, например, при свайпе, или при нажатии
        //кнопки back после окончания редактирования
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mDateButton = v.findViewById(R.id.crime_date_button);
        mDateButton.setOnClickListener(onDateButtonClickListener);

        mTimeButton = v.findViewById(R.id.crime_time_button);
        mTimeButton.setOnClickListener(onTimeButtonClickListener);

        mIsSolvedCheckbox = v.findViewById(R.id.crime_solved_id);
        mIsSolvedCheckbox.setOnCheckedChangeListener(mCheckboxListener);

        mTitleTextField = v.findViewById(R.id.crime_title_edit_view_id);
        mTitleTextField.addTextChangedListener(this.crimeEditTextListener);

        ((AppCompatActivity) Objects.requireNonNull(this.getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (mIsNew) {
            mTitleTextField.requestFocus();
        }

        System.out.println("mCrime: " + mCrime);

        this.updateUI();

        return v;
    }

    /**
     * обработчик данных от потомков
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DATE_PICKER_FRAGMENT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        mCrime.setDate(DatePickerActivity.fetchDateFromIntent(data));
                    } else {
                        throw new NullPointerException("No data from datePickerFragment, but expected");
                    }
                }
                updateUI();
                break;
            case TIME_PICKER_FRAGMENT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        mCrime.setDate(TimePickerFragment.fetchDateFromIntent(data));
                    } else {
                        throw new NullPointerException("No data from timePickerFragment, but expected");
                    }
                }
                updateUI();
            default:
                break;
        }
    }

    /**
     * Listener изменения названия преступления
     */
    private final TextWatcher crimeEditTextListener = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Именно .toString, а не (String) s. Последний почему-то вызывает ошибку
            mCrime.setTitle(s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Event-listener смены значения checkbox'a
     */
    private final CompoundButton.OnCheckedChangeListener mCheckboxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mCrime.setSolved(isChecked);
        }
    };

    /**
     * Event listener нажатия на кнопку даты
     */
    private final View.OnClickListener onDateButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            startActivityForResult(DatePickerActivity.createIntent(getActivity(), mCrime.getDate()), DATE_PICKER_FRAGMENT_REQUEST_CODE);

//            FragmentManager fm = getFragmentManager();
//            DatePickerFragment datePicker = DatePickerFragment.newInstance(mCrime.getDate());
//
//            //datePicker устанавлевает целевой фрагмент, которому будет отдавать данные
//            datePicker.setTargetFragment(CrimeFragment.this, DATE_PICKER_FRAGMENT_REQUEST_CODE);
//            /*datePicker просит дать ему контакты  fragmentManager'a, шоб с ним договориться, чтоб
//             * он его показал. DatePicker представился как DATE_PICKER_FRAGMENT_TAG */
//            datePicker.show(fm, DATE_PICKER_FRAGMENT_TAG);
        }
    };
    private final View.OnClickListener onTimeButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Нажали на время. Нужно отобразить TimePicker
            TimePickerFragment timePicker = TimePickerFragment.newInstance(
                    CrimeFragment.this,
                    TIME_PICKER_FRAGMENT_REQUEST_CODE,
                    mCrime.getDate());
            timePicker.show(Objects.requireNonNull(getFragmentManager()), null);

        }
    };

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(Menu.NONE, MENU_ITEM_DELETE_CRIME_ID,Menu.NONE,R.string.delete_crime);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_DELETE_CRIME_ID:
                CrimeLab.get(getActivity()).deleteCrime(mCrime.getId());
                getActivity().finish();
                return true;
            default:
        return super.onOptionsItemSelected(item);
        }
    }

    public static CrimeFragment newInstance(UUID crimeId, boolean isNew) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID_KEY, crimeId);
        args.putBoolean(ARG_IS_NEW_KEY, isNew);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateUI() {

        if (mCrime != null) {
            mTitleTextField.setText(mCrime.getTitle());
            DateFormat df = new SimpleDateFormat("dd MMM, YYYY", new Locale("ru"));
            mDateButton.setText(df.format(mCrime.getDate()));
            df = new SimpleDateFormat("HH:mm", new Locale("ru"));
            mTimeButton.setText(df.format(mCrime.getDate()));
            mIsSolvedCheckbox.setChecked(mCrime.isSolved());
        } else {
            System.out.println("атас! mCrime == null");
        }

    }

//    /**
//     * Метод для дополнения интента родителя. Задача состоит в том, чтобы фрагмент знал, загружает
//     * ли он уже существующее преступление или новое. Метод не обязательно будет вызываться родителем.
//     * На этот случай и предусмотрены дефолтные значения методов getExtra.
//     * @param i
//     * @param isNew
//     */
//    public static void decorateIntent(Intent i, boolean isNew) {
//        i.putExtra(IS_NEW_KEY,isNew);
//    }

}
