package GGhost.criminalintent.crime_detail;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Date;
import java.util.UUID;

import GGhost.criminalintent.R;
import GGhost.criminalintent._helpers.SingleFragmentActivity;

public class DatePickerActivity extends SingleFragmentActivity implements DatePickerFragmentDelegate {


    private static final String CRIME_DATE_KEY = "CRIME_DATE_KEY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction().detach(fm.getPrimaryNavigationFragment()).commit();

        super.onConfigurationChanged(newConfig);
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<DatePickerActivity> CREATOR = new Parcelable.Creator<DatePickerActivity>() {

        @Override
        public DatePickerActivity createFromParcel(Parcel source) {
            return new DatePickerActivity();
        }

        @Override
        public DatePickerActivity[] newArray(int size) {
            return new DatePickerActivity[0];
        }
    };
}
