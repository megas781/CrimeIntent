package gghost.criminalintent.crime_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

import gghost.criminalintent.R;
import gghost.criminalintent.crime_detail.CrimeDetailPagerActivity;
import gghost.criminalintent.model.Crime;
import gghost.criminalintent.model.CrimeLab;

/**
 * Фрагмент сцены CrimeList, по сути реализующий весь интерфейс с RecyclerView
 */
public class CrimeListFragment extends Fragment {

    public interface Delegate extends Serializable {
        void onCrimeSelected(Crime crime, int position);
    }

    private static final int CRIME_DETAIL_REQUEST_CODE = 1;
    private static final int CRIME_NEW_REQUEST_CODE = 2;
    private static final String STATE_IS_SUBTITLE_VISIBLE_KEY = "STATE_IS_SUBTITLE_VISIBLE_KEY";

    private static final String ARG_ARRAY_LIST_KEY = "ARG_ARRAY_LIST_KEY";
    //Экземпляр RecyclerView
    private RecyclerView mCrimeRecyclerView;
    //Адаптер
    private CrimeHolderAdapter mCrimeHolderAdapter;

    private LinearLayout mOnEmptyCrimeButtonContainer;
    private Button mOnEmptyCrimeButton;

    private Delegate mDelegate;

    private boolean mIsSubtitleVisible = false;


    /**
     * В onCreate фрагмент обычно fetch'ит данные модели. Установка данных в представления
     * производится в другом методе (onCreateView)
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Фрагмент должен изъявить желание участвовать в формировании меню, чтобы
         * FragmentManager вызвал его метод onCreateOptionsMenu */
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            mIsSubtitleVisible = savedInstanceState.getBoolean(STATE_IS_SUBTITLE_VISIBLE_KEY, false);
        }

        if (getArguments() != null) {
            ArrayList<Crime> crimes = (ArrayList<Crime>) getArguments().getSerializable(ARG_ARRAY_LIST_KEY);
            mCrimeHolderAdapter = new CrimeHolderAdapter(crimes);
        }


    }

    /**
     * Здесь происходит присваивание значений представлениям фрагмента. На момент исполнения метода
     * Представления уже инициализированы и готовы к использованию (в отличие от onCreate)
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*Связываем файл fragment_crime_list.xml с помощью infalter. Помещаем его в container (видимо
         * container – это ссылка на View активности).
         */
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);
        /* Файл связали. Теперь внутри этого файла нужно связать тег <RecyclerView> со
         * свойством mCrimeRecyclerView */
        mCrimeRecyclerView = v.findViewById(R.id.crime_recycler_view);
        /* Абсолютно любой RecyclerView нуждается в LayoutManager'e. Имеено LayoutManager отвечает
         * за позиционирование ячеек и когда их переиспользовать. Можно создавать кастомные LayoutManager'ы
         * но чаще хватает использовния заготовленных LinearLayoutManager и GridLayoutManager */

        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        /*
        И конечно же RecyclerView нуждается в поставщике данных или адаптере,
        как это называется в Андроид-программировании.
         */
        mCrimeRecyclerView.setAdapter(mCrimeHolderAdapter);

        mOnEmptyCrimeButtonContainer = v.findViewById(R.id.on_empty_new_crime_button_container_id);
        mOnEmptyCrimeButton = mOnEmptyCrimeButtonContainer.findViewById(R.id.on_empty_new_crime_button_id);
        mOnEmptyCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrimeListFragment.this.goToCreateNewCrimeActivity();
            }
        });
        return v;
    }

    /**
     * Метод для сбора данных с дочерних активностей
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //noinspection AlibabaSwitchStatement
        switch (requestCode) {
            case CRIME_DETAIL_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {

                    if (CrimeDetailPagerActivity.getPageLeftFromIntent(data) != -1) {
                        Objects.requireNonNull(mCrimeRecyclerView.getLayoutManager()).scrollToPosition(CrimeDetailPagerActivity.getPageLeftFromIntent(data));
                    }
                    Objects.requireNonNull(mCrimeRecyclerView.getAdapter()).notifyDataSetChanged();

                    /*Здесь должен быть вызван статический метод класса CrimeActivity, достающий из
                    интента data нужные данные */
//                    int updateIndex = CrimeActivity.getPickedCrimeIndexFromIndent(data);
//                    if (updateIndex >= 0) {
//                        mCrimeRecyclerView.getAdapter().notifyItemChanged(updateIndex);
//                    }
                }
                break;
            case CRIME_NEW_REQUEST_CODE:
                Objects.requireNonNull(mCrimeRecyclerView.getAdapter()).notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_SUBTITLE_VISIBLE_KEY, mIsSubtitleVisible);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        /*TODO: на само деле так делать не совсем правильно. Делегат нужно присваивать
           в инициализаторе newInstance. Ну да ладно. В книжке показано так */
        //поставил хоть какую-то проверку на реализацию интерфейса
        if (context instanceof Delegate) {
            mDelegate = (Delegate) context;
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        //Убираем связь с CrimeListActivity
        mDelegate = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        menu.findItem(R.id.show_subtitle_item_id).setTitle(mIsSubtitleVisible ? R.string.hide_subtitle : R.string.show_subtitle);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateSubtitle();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime_menu_item_id:
                goToCreateNewCrimeActivity();
                return true;
            case R.id.show_subtitle_item_id:
                mIsSubtitleVisible = !mIsSubtitleVisible;
                this.updateSubtitle();
                this.getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //MARK: Custom methods
    private void updateSubtitle() {
        int crimeCount = CrimeLab.get(getActivity()).getCrimeList().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        /* Учебник хочет, чтобы я кастил getActivity() (который возвращает FragmentActivity)
        до AppCompatActivity, чтобы использовать getSupportActionBar() вместо Activity.getActionBar().
        Не знаю, вызовет ли getActionBar() ошибку при исполнении, но проверим как-нибудь...*/
        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setSubtitle(mIsSubtitleVisible ? subtitle : null);
    }

    //Самописный метод для обновления UI
    public void updateUI() {
        /*ИМЕННО ЗДЕСЬ МЫ ОБНОВЛЯЕМ RecyclerView. Уведомление Adapter.notifyDataSetChanged()
         * вызывается прямо в adapter'e */
        mCrimeHolderAdapter.setCrimeList(CrimeLab.get(getActivity()).getCrimeList());
        //Если количество преступлений равно нулю, то показываем предложение создать первое преступление
        mOnEmptyCrimeButtonContainer.setVisibility((this.mCrimeHolderAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE));
        //Обновляем subtitle в соответствии с количеством преступлений
        updateSubtitle();
    }

    private void goToCreateNewCrimeActivity() {
        //Создаем новое преступление
        Crime newCrime = new Crime();
        CrimeLab.get(getActivity()).addCrime(newCrime);
        newCrime.setTitle(getString(R.string.new_crime) + " #" + CrimeLab.get(getActivity()).getCrimeList().size());

        mDelegate.onCrimeSelected(newCrime, CrimeLab.get(getActivity()).getCrimeList().size());
//        updateUI();

//        Intent i = CrimeDetailPagerActivity.createIntentForCrimeListActivity(getActivity(), newCrime.getId(), CrimeLab.get(getActivity()).getCrimeList().size(), true);
//        startActivityForResult(i, CRIME_NEW_REQUEST_CODE);
    }

    /**
     * Класс ячейки преступления. Внутри Holder'a имеется свойство itemView. Т.е. по сути ViewHolder
     * может обладать своей собственной логикой. Например, устанавливать внутри себя прослушивание event'ов и т.д.
     */
    public class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected Crime mCrime;
        protected TextView mTitleTextView;
        protected TextView mDateTextView;

        @Nullable
        private ImageView mCrimeSolvedImageView;

        public CrimeHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.crime_title_id);
            mDateTextView = itemView.findViewById(R.id.crime_date_id);
            mCrimeSolvedImageView = itemView.findViewById(R.id.crime_solved_image_view_id);

            itemView.setOnClickListener(this);


        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());

            String dateString = "";
            dateString += DateFormat.getDateInstance().format(mCrime.getDate()) + ", ";
            if (android.text.format.DateFormat.is24HourFormat(this.itemView.getContext())) {
                dateString += new SimpleDateFormat("H:mm").format(mCrime.getDate());
            } else {
                dateString += new SimpleDateFormat("h:mm a").format(mCrime.getDate());
            }

            mDateTextView.setText(dateString);

            //Этого элемента может не быть, если слишком серьезнное преступление
            if (mCrimeSolvedImageView != null) {
                mCrimeSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.INVISIBLE);
            }


            String accDescription = getString(R.string.crime_item_description,mCrime.getTitle(), DateFormat.getDateInstance().format(mCrime.getDate()), getString(mCrime.isSolved() ? R.string.solved : R.string.not_solved));


            itemView.setContentDescription(accDescription);

        }

        /**
         * Реагирует на нажатие на самого себя
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            mDelegate.onCrimeSelected(mCrime, getAdapterPosition());
            updateUI();
//            Intent i = CrimeDetailPagerActivity.createIntentForCrimeListActivity(this.itemView.getContext(), mCrime.getId(), getAdapterPosition(), false);
//            startActivityForResult(i, CRIME_DETAIL_REQUEST_CODE);
        }
    }

    public class CrimeHolderAdapter extends RecyclerView.Adapter<CrimeHolder> {

        //MARK: RecyclerView.Adapter implementation
        private ArrayList<Crime> mCrimeList;

        public void setCrimeList(ArrayList<Crime> crimeList) {
            mCrimeList = crimeList;
            //Уведомляем RecyclerView прямо внутри Adapter'a
            this.notifyDataSetChanged();
        }

        /* Ответственное решение: адаптер принимает делегат на слушанье нажатий на ячейки */
        public CrimeHolderAdapter(ArrayList<Crime> crimeList) {
            this.mCrimeList = crimeList;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            holder.bind(mCrimeList.get(position));
        }

        @Override
        public int getItemCount() {
            return mCrimeList.size();
        }
    }

    public static CrimeListFragment newInstance(ArrayList<Crime> crimeList) {
        Bundle args = new Bundle();
        //Преобразовываем в обычный Java-массив, чтобы положить в args
        args.putSerializable(ARG_ARRAY_LIST_KEY, crimeList);
        CrimeListFragment fragment = new CrimeListFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
