package gghost.criminalintent.crime_list;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import gghost.criminalintent.R;
import gghost.criminalintent._helpers.AbstractSingleFragmentActivity;
import gghost.criminalintent.crime_detail.CrimeDetailFragment;
import gghost.criminalintent.crime_detail.CrimeDetailPagerActivity;
import gghost.criminalintent.model.Crime;
import gghost.criminalintent.model.CrimeLab;

public class CrimeListActivity extends AbstractSingleFragmentActivity implements CrimeListFragment.Delegate, CrimeDetailFragment.Delegate {

    private CrimeListFragment getCrimeListFragment() {
        return (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected Fragment createFragment() {
        return CrimeListFragment.newInstance(CrimeLab.get(this).getCrimeList());
    }

    @Override
    public void onCrimeSelected(Crime crime, int position) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            //создаем intent
            Intent i = CrimeDetailPagerActivity.createIntentForCrimeListActivity(this,crime.getId(),position, false);
            startActivity(i);
        } else {
            CrimeDetailFragment crimeDetailFragment = CrimeDetailFragment.newInstance(crime, CrimeLab.get(this).getPhotoFile(crime), false);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, crimeDetailFragment)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        getCrimeListFragment().updateUI();
    }

    @Override
    public void onCrimeDeleted() {
        getCrimeListFragment().updateUI();
    }
}
