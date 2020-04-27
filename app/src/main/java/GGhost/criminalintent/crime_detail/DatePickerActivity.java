package GGhost.criminalintent.crime_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Date;

import GGhost.criminalintent.R;
import GGhost.criminalintent._helpers.SingleFragmentActivity;

public class DatePickerActivity extends SingleFragmentActivity {


    private static final String CRIME_DATE_KEY = "CRIME_DATE_KEY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

    }


    @Override
    protected Fragment createFragment() {
        //Достаем дату из интента
        return DatePickerFragment.newInstance((Date) getIntent().getSerializableExtra(CRIME_DATE_KEY));
    }

    public static Intent createIntent(Context context, Date date) {
        Intent i = new Intent(context, DatePickerActivity.class);
        i.putExtra(CRIME_DATE_KEY, date);
        return i;
    }
}
