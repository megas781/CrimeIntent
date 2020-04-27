package GGhost.criminalintent.crime_detail;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

import GGhost.criminalintent.R;
import GGhost.criminalintent.model.Crime;
import GGhost.criminalintent.model.CrimeLab;

public class CrimeFragment extends Fragment {

    private static final String BUNDLE_CRIME_ID_KEY = "BUNDLE_CRIME_ID_KEY";
    //Тег для FragmentManager'a
    private static final String DATE_PICKER_FRAGMENT_TAG = "DATE_PICKER_FRAGMENT_TAG";
    //Код для TargetFragment'a
    private static final int DATE_PICKER_FRAGMENT_REQUEST_CODE = 0;
//    Константа ключа для значения страницы, на который был пользователь перед нажатием кнопки Back
    private static final String PAGE_LEFT_INDEX_KEY = "PAGE_LEFT_INDEX_KEY";

    private Crime mCrime;
    private EditText mTitleTextField;
    private Button mDateButton;
    private CheckBox mIsSolvedCheckbox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        System.out.println("getArguments()    : " + (getArguments() != null));
        System.out.println("savedInstanceState: " + (savedInstanceState != null));

        mCrime = new Crime();
        mCrime.setDate(new Date());

        //Миссия: достать crimeId из родительской активности CrimeActivity
        UUID crimeId = (UUID) getArguments().getSerializable(BUNDLE_CRIME_ID_KEY);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mDateButton = v.findViewById(R.id.crime_date_button);
//        mDateButton.setText(mCrime.getDate().toString());
        mDateButton.setOnClickListener(onDateButtonClickListener);

        mIsSolvedCheckbox = v.findViewById(R.id.crime_solved_id);
//        mIsSolvedCheckbox.setChecked(mCrime.isSolved());
        mIsSolvedCheckbox.setOnCheckedChangeListener(mCheckboxListener);

        mTitleTextField = v.findViewById(R.id.crime_title_edit_view_id);
//        mTitleTextField.setText(mCrime.getTitle());
        mTitleTextField.addTextChangedListener(this.crimeEditTextListener);

        this.updateUI();

        return v;
    }

    /** обработчик данных от потомков */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DATE_PICKER_FRAGMENT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        mCrime.setDate(DatePickerFragment.getDateFromIntent(data));
                    } else {
                        throw new NullPointerException("No data from datePicker, but expected");
                    }
                }
                updateUI();
                break;
            default:
                break;
        }
    }

    /** Listener изменения названия преступления */
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

    /** Event-listener смены значения checkbox'a */
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

            FragmentManager fm = getFragmentManager();
            DatePickerFragment datePicker = DatePickerFragment.newInstance(mCrime.getDate());

            //datePicker устанавлевает целевой фрагмент, которому будет отдавать данные
            datePicker.setTargetFragment(CrimeFragment.this, DATE_PICKER_FRAGMENT_REQUEST_CODE);
            /*datePicker просит дать ему контакты  fragmentManager'a, шоб с ним договориться, чтоб
             * он его показал. DatePicker представился как DATE_PICKER_FRAGMENT_TAG */
            datePicker.show(fm, DATE_PICKER_FRAGMENT_TAG);
        }
    };

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_CRIME_ID_KEY, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateUI() {
        mTitleTextField.setText(mCrime.getTitle());
        DateFormat df = new SimpleDateFormat("dd MMM, YYYY", new Locale("ru"));
        mDateButton.setText(df.format(mCrime.getDate()));
        mIsSolvedCheckbox.setChecked(mCrime.isSolved());
    }
}
