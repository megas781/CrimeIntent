package GGhost.criminalintent.crime_list;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import GGhost.criminalintent.R;
import GGhost.criminalintent.model.Crime;
import GGhost.criminalintent.model.CrimeLab;

public class CrimeListFragment extends Fragment {


    private RecyclerView mCrimeRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

        mCrimeRecyclerView.setAdapter(new CrimeAdapter(CrimeLab.get(getActivity()).getCrimeList()));
        return v;
    }

    public class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected Crime mCrime;

        protected TextView mTitleTextView;
        protected TextView mDateTextView;

        public CrimeHolder(@NonNull View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.crime_title_id);
            mDateTextView = itemView.findViewById(R.id.crime_date_id);

            //Добавляем действие по нажатии
            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
        }

        /**
         * Реагирует на нажатие на самого себя
         * @param v
         */
        @Override
        public void onClick(View v) {
            Toast t = Toast.makeText(getActivity(), "Вы нажали на " + this.mCrime.getTitle(), Toast.LENGTH_SHORT);
            t.setGravity(Gravity.BOTTOM,0,20 * (int) getResources().getDisplayMetrics().density);
            t.show();
        }
    }
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
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private int k = 0;
        private List<Crime> mCrimeList;

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
                System.out.println("onCreateViewHolder " + (++k));
                View v = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
                return new CrimeHolder(v);
            } else {
                View v = layoutInflater.inflate(R.layout.list_item_crime_serious, parent, false);
                return new SeriousCrimeHolder(v);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimeList.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimeList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mCrimeList.get(position).isRequiresPolice() ? 1 : 0;
        }
    }
}
