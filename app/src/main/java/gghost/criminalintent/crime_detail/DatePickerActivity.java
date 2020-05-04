package gghost.criminalintent.crime_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.Date;
import gghost.criminalintent.R;
import gghost.criminalintent._helpers.AbstractSingleFragmentActivity;

public class DatePickerActivity extends AbstractSingleFragmentActivity implements DatePickerFragment.DatePickerFragmentDelegate, Serializable {

    private static final String CRIME_DATE_KEY = "CRIME_DATE_KEY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
    }

    @Override
    protected Fragment createFragment() {
        //Достаем дату из интента
        return DatePickerFragment.newInstance((Date) getIntent().getSerializableExtra(CRIME_DATE_KEY), this);
    }

    public static Intent createIntent(Context context, Date date) {
        Intent i = new Intent(context, DatePickerActivity.class);
        i.putExtra(CRIME_DATE_KEY, date);
        return i;
    }

    @Override
    public void onDatePickerConfirmed(Date date) {
        Intent i = new Intent();
        i.putExtra(CRIME_DATE_KEY, date);
        this.setResult(RESULT_OK, i);
        finish();
    }
    public static Date fetchDateFromIntent(Intent i) {
        return (Date) i.getSerializableExtra(CRIME_DATE_KEY);
    }

}
