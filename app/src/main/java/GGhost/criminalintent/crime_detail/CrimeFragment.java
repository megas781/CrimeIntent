package GGhost.criminalintent.crime_detail;

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

import GGhost.criminalintent.R;
import GGhost.criminalintent.model.Crime;

public class CrimeFragment extends Fragment {

    private Crime mCrime;
    private EditText mTitleTextField;
    private Button mDateButton;
    private CheckBox mIsSolvedCheckbox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCrime = new Crime();
        mCrime.setDate(new Date());
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
        mIsSolvedCheckbox.setOnCheckedChangeListener(mListener);

        mTitleTextField = v.findViewById(R.id.crime_title_edit_view_id);
        mTitleTextField.addTextChangedListener(this.crimeEditTextListener);

        return v;
    }


    private TextWatcher crimeEditTextListener = new TextWatcher(){

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void afterTextChanged(Editable s) { }
    };

    private CompoundButton.OnCheckedChangeListener mListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mCrime.setSolved(isChecked);
        }
    };

}
