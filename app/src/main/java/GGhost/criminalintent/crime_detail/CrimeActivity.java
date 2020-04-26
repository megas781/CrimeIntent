package GGhost.criminalintent.crime_detail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.UUID;

import GGhost.criminalintent._helpers.SingleFragmentActivity;

public class CrimeActivity extends SingleFragmentActivity {

    //public - потому что ключ нужен в CrimeFragment
    private static final String INTENT_CRIME_ID_KEY = "EXTRA_CRIME_ID_KEY";
    private static final String INTENT_CRIME_RECYCLER_VIEW_POSITION_KEY = "INTENT_CRIME_RECYCLER_VIEW_POSITION_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Начальное установление результата. Если в дочернем фрагменте не будет вызываться setCustomResult,
        * то как минимум гарантированно будет известна позиция выбранного item'a */
        this.setCustomResult(RESULT_OK, new Intent());
    }

    @Override
    protected Fragment createFragment() {
        //Теперь нельзя просто создать пустой CrimeFragment. Нужно вызывать  .newInstance() с передачей аргументов
        UUID crimeId = (UUID) getIntent().getSerializableExtra(INTENT_CRIME_ID_KEY);
        return CrimeFragment.newInstance(crimeId);
    }

    public static Intent createIntentForCrimeListActivity(Context context, UUID crimeId, int position) {
        Intent i = new Intent(context, CrimeActivity.class);
        i.putExtra(INTENT_CRIME_ID_KEY, crimeId);
        i.putExtra(INTENT_CRIME_RECYCLER_VIEW_POSITION_KEY, position);
        return i;
    }

    /**
     * Метод переопределяет setResult, кладя в intent индекс нажатого item'a. Если в дочернем фрагменте нужно
     * будет обновить результат, стоит пользоваться этими методом, так как фрагмент не должен знать о позиции
     * выбранного элемента (хотя и активность тоже не должна, ну да ладно)
     * @param requestCode
     * @param intent
     */
    public void setCustomResult(int requestCode, Intent intent) {
        intent.putExtra(INTENT_CRIME_RECYCLER_VIEW_POSITION_KEY, getIntent().getIntExtra(INTENT_CRIME_RECYCLER_VIEW_POSITION_KEY, -228));
        setResult(requestCode, intent);
    }
    public static int getPickedCrimeIndexFromIndent(Intent i) {
        return i.getIntExtra(INTENT_CRIME_RECYCLER_VIEW_POSITION_KEY, -1);
    }
}
