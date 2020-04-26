package GGhost.criminalintent.crime_detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import java.util.Date;
import java.util.UUID;

import GGhost.criminalintent.R;
import GGhost.criminalintent.model.Crime;
import GGhost.criminalintent.model.CrimeLab;

public class CrimeFragment extends Fragment {

    private static final String BUNDLE_CRIME_ID_KEY = "BUNDLE_CRIME_ID_KEY";

//    private static final String RESULT_INDEX

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
        mDateButton.setText(mCrime.getDate().toString());
        mDateButton.setEnabled(false);

        mIsSolvedCheckbox = v.findViewById(R.id.crime_solved_id);
        mIsSolvedCheckbox.setChecked(mCrime.isSolved());
        mIsSolvedCheckbox.setOnCheckedChangeListener(mCheckboxListener);

        mTitleTextField = v.findViewById(R.id.crime_title_edit_view_id);
        mTitleTextField.setText(mCrime.getTitle());
        mTitleTextField.addTextChangedListener(this.crimeEditTextListener);

        return v;
    }


    private TextWatcher crimeEditTextListener = new TextWatcher(){

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Именно .toString, а не (String) s. Последний почему-то вызывает ошибку
            mCrime.setTitle(s.toString());
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void afterTextChanged(Editable s) { }
    };

    private CompoundButton.OnCheckedChangeListener mCheckboxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mCrime.setSolved(isChecked);
        }
    };

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_CRIME_ID_KEY, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Кастомный абстрактный метод, показывающий, как устанавливать Result фрагменту
     */
    private void setAbstractResult() {
        /* Хотя фрагменты и могут сами обрабатывать результаты активностей методом onActivityResult,
        * сами одни не могут их устанавливать. Для возвращения результата им нужно обращаться к
        * своей активности */
        getActivity().setResult(Activity.RESULT_CANCELED, new Intent());
    }


}
