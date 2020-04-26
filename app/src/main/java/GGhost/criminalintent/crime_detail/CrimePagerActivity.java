package GGhost.criminalintent.crime_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import GGhost.criminalintent.R;
import GGhost.criminalintent.model.Crime;
import GGhost.criminalintent.model.CrimeLab;

public class CrimePagerActivity extends AppCompatActivity {

    private static final String INTENT_CRIME_ID_KEY = "EXTRA_CRIME_ID_KEY";
    private static final String INTENT_CRIME_RECYCLER_VIEW_POSITION_KEY = "INTENT_CRIME_RECYCLER_VIEW_POSITION_KEY";

    private List<Crime> mCrimeList;
    private int mCurrentCrimeIndex;

    private ViewPager2 mViewPager2;
    private Button mToBeginningButton;
    private Button mToEndButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Устанавливаем макет для активности
        setContentView(R.layout.activity_crime_pager);

        mCrimeList = CrimeLab.get(this).getCrimeList();
        mCurrentCrimeIndex = getIntent().getIntExtra(INTENT_CRIME_RECYCLER_VIEW_POSITION_KEY,0);

        mToBeginningButton = findViewById(R.id.crime_view_pager_to_beginning_button_id);
        mToEndButton = findViewById(R.id.crime_view_pager_to_end_button_id);

        //Создаем инстанцию основного виджета активности
        mViewPager2 = findViewById(R.id.crime_view_pager_id);
        mViewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return CrimeFragment.newInstance(mCrimeList.get(position).getId());
            }

            @Override
            public int getItemCount() {
                return CrimeLab.get(getParent()).getCrimeList().size();
            }
        });
        mViewPager2.setCurrentItem(mCurrentCrimeIndex, false);
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                /*Мы не передаем никакие данные с Intent'ом, потому что было решено вернуться к использованию
                метода notifyDataSetChanged() */
                setResult(RESULT_OK);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        updateUI();
    }

    public static Intent createIntentForCrimeListActivity(Context context, UUID crimeId, int position) {
        Intent i = new Intent(context, CrimePagerActivity.class);
        i.putExtra(INTENT_CRIME_ID_KEY, crimeId);
        i.putExtra(INTENT_CRIME_RECYCLER_VIEW_POSITION_KEY, position);
        return i;
    }

    public void onJumpToEdgeButtonClick(View v) {
        switch (v.getId()) {
            case R.id.crime_view_pager_to_beginning_button_id:
                mViewPager2.setCurrentItem(0, false);
                break;
            case R.id.crime_view_pager_to_end_button_id:
                mViewPager2.setCurrentItem(mViewPager2.getAdapter().getItemCount() - 1, false);
                break;
            default:
                break;
        }
        updateUI();
    }


    private void updateUI() {
        mToBeginningButton.setEnabled(mViewPager2.getCurrentItem() > 0);
        mToEndButton.setEnabled(mViewPager2.getCurrentItem() < (Objects.requireNonNull(mViewPager2.getAdapter()).getItemCount() - 1));
    }
}
