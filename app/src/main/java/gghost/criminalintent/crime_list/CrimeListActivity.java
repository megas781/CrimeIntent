package gghost.criminalintent.crime_list;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import gghost.criminalintent.R;
import gghost.criminalintent._helpers.AbstractSingleFragmentActivity;
import gghost.criminalintent.model.CrimeLab;

public class CrimeListActivity extends AbstractSingleFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return CrimeListFragment.newInstance(CrimeLab.get(this).getCrimeList());
    }
}
