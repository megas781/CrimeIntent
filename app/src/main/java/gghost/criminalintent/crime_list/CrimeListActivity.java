package gghost.criminalintent.crime_list;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import gghost.criminalintent.R;
import gghost.criminalintent._helpers.AbstractSingleFragmentActivity;

public class CrimeListActivity extends AbstractSingleFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {

        return new CrimeListFragment();
    }
}
