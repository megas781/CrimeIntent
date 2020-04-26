package GGhost.criminalintent.crime_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import GGhost.criminalintent.R;
import GGhost.criminalintent.crime_detail.CrimeActivity;
import GGhost.criminalintent.crime_detail.CrimePagerActivity;
import GGhost.criminalintent.model.Crime;
import GGhost.criminalintent.model.CrimeLab;

/**
 * Фрагмент сцены CrimeList, по сути реализующий весь интерфейс с RecyclerView
 */
public class CrimeListFragment extends Fragment {

    private static final int CRIME_DETAIL_REQUEST_CODE = 1;

    //Экземпляр RecyclerView
    private RecyclerView mCrimeRecyclerView;

    /**
     * В onCreate фрагмент обычно fetch'ит данные модели. Установка данных в представления
     * производится в другом методе (onCreateView)
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Здесь происходит присваивание значений представлениям фрагмента. На момент исполнения метода
     * Представления уже инициализированы и готовы к использованию (в отличие от onCreate)
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
        mCrimeRecyclerView.setAdapter(new CrimeAdapter(CrimeLab.get(getActivity()).getCrimeList()));
        return v;
    }

    /**
     * Метод для сбора данных с дочерних активностей
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CRIME_DETAIL_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {

                    mCrimeRecyclerView.getAdapter().notifyDataSetChanged();

                    /*Здесь должен быть вызван статический метод класса CrimeActivity, достающий из
                    интента data нужные данные */
//                    int updateIndex = CrimeActivity.getPickedCrimeIndexFromIndent(data);
//                    if (updateIndex >= 0) {
//                        mCrimeRecyclerView.getAdapter().notifyItemChanged(updateIndex);
//                    }
                }
            default:
                break;
        }
    }

    /** Класс ячейки преступления. Внутри Holder'a имеется свойство itemView. Т.е. по сути ViewHolder
     * может обладать своей собственной логикой. Например, устанавливать внутри себя прослушивание event'ов и т.д.*/
    public class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected Crime mCrime;

        protected TextView mTitleTextView;
        protected TextView mDateTextView;

        @Nullable private ImageView mCrimeSolvedImageView;

        public CrimeHolder(@NonNull View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.crime_title_id);
            mDateTextView = itemView.findViewById(R.id.crime_date_id);
            mCrimeSolvedImageView = itemView.findViewById(R.id.crime_solved_image_view_id);

            //Добавляем действие по нажатии
            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());

//            new SimpleDateFormat().for
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YYYY, HH:mm", new Locale("ru"));
            mDateTextView.setText(sdf.format(mCrime.getDate()));

            //Этого элемента может не быть, если слишком серьезнное преступление
            if (mCrimeSolvedImageView != null) {
                mCrimeSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.INVISIBLE);
            }
        }

        /**
         * Реагирует на нажатие на самого себя
         * @param v
         */
        @Override
        public void onClick(View v) {
//            System.out.println("AdapterPos: " + getAdapterPosition() + "; LayoutPos: " + getLayoutPosition());
            Intent i = CrimePagerActivity.createIntentForCrimeListActivity(getActivity(), mCrime.getId(), getAdapterPosition());
            startActivityForResult(i,CRIME_DETAIL_REQUEST_CODE);
        }
    }

    /** Более серьёзное предступление, ячейка которого имеет кнопку "Вызвать полицию" */
    public class SeriousCrimeHolder extends CrimeHolder {

        protected Button mCallPoliceButton;

        public SeriousCrimeHolder(@NonNull View itemView) {
            super(itemView);
            mCallPoliceButton = itemView.findViewById(R.id.call_police_button_id);
            mCallPoliceButton.setOnClickListener(mOnCallButtonTapped);
        }

        private final View.OnClickListener mOnCallButtonTapped = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast t = Toast.makeText(getActivity(), getResources().getString(R.string.you_called_police_alert,(int) Math.round(Math.random() * 30 + 10)), Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 60);
                t.show();
            }
        };
    }

    /** Адаптер таблицы. Создает ячейки, выбирает, для кого какой макет использовать и т.д.*/
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimeList;

        /**
         * Кастомный конструктор CrimeAdapter'a принимает на вход список преступлений
         * @param crimeList
         */
        public CrimeAdapter(List<Crime> crimeList) {
            mCrimeList = crimeList;
        }

        /**
         * Что мы тут делаем?
         * Адаптер загружает представления ячеек таблицы. Метод выполняется столько раз, сколько нужно
         * для непрерывного скроллинга. Метод onCreateViewHolder должен создать CrimeHolder,
         * связать его с представлением и вернуть.
         *
         * @param parent
         * @param viewType
         * @return
         */
        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            //Если тип 0 (т.е. обычный), то создаем, как обычно
            if (viewType == 0) {
                View v = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
                return new CrimeHolder(v);
            } else {
                //Если преступление серьёзное (т.е. viewType == 1), то инициализируем SeriousCrimeHolder
                View v = layoutInflater.inflate(R.layout.list_item_crime_serious, parent, false);
                return new SeriousCrimeHolder(v);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimeList.get(position);
            holder.bind(crime);
        }

        //Определяет количество ячеек
        @Override
        public int getItemCount() {
            return mCrimeList.size();
        }

        //Метод, определяющий, какого типа является конкретная ячейка
        @Override
        public int getItemViewType(int position) {
            return mCrimeList.get(position).isRequiresPolice() ? 1 : 0;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        this.updateUI();
    }

    //Самописный метод для обновления UI
    private void updateUI() {

        //Чтобы обновить UI заново достаем список преступлений
        List<Crime> crimeList = CrimeLab.get(getActivity()).getCrimeList();

        //Если адаптера еще нет (что наврядли, потому что он инициализируется в onCreateView),  то устанавливаем
        //Новый адаптер
        if (mCrimeRecyclerView.getAdapter() == null) {
            mCrimeRecyclerView.setAdapter(new CrimeAdapter(crimeList));
        } else {
            //В случае наличия адаптера (что скорее всего) просто уведомляем о том, что нужно перезагрудить ячейки
            mCrimeRecyclerView.getAdapter().notifyDataSetChanged();
//            mCrimeRecyclerView.getAdapter().notifi
            //На самом деле лучше испльзовать .notifyItemChanged для выборочного обновления. Но в книге
            //пока что говорят сделать через общее обновление ячеек
        }
    }

}
