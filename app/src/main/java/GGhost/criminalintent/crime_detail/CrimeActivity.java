package GGhost.criminalintent.crime_detail;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import GGhost.criminalintent._helpers.SingleFragmentActivity;

public class CrimeActivity extends SingleFragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
