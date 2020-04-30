package gghost.criminalintent.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private List<Crime> mCrimeList;

    private static CrimeLab sInstance;

    public static CrimeLab get(Context context) {
        if (sInstance == null) {
            sInstance = new CrimeLab(context);
        }
        return sInstance;
    }

    public Hashtable<UUID, Integer> mIndexTable = new Hashtable<>();

    private CrimeLab(Context context) {
        //

        mCrimeList = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            Crime newCrime = new Crime();
            newCrime.setTitle("Crime #" + (i + 1) + "....................");
            newCrime.setDate(new Date());
            newCrime.setSolved((int) Math.round(Math.random()) == 1);
            newCrime.setRequiresPolice((int) Math.round(Math.random()) == 1);

            //newCrime
            mIndexTable.put(newCrime.getId(), i);

            mCrimeList.add(newCrime);
        }
    }

    public List<Crime> getCrimeList() {
        return this.mCrimeList;
    }

    public Crime getCrime(UUID uuid) {

        if (mIndexTable.get(uuid) != null) {
            return mCrimeList.get(mIndexTable.get(uuid));
        } else {
            return null;
        }

//        for (Crime crime : this.mCrimeList) {
//            if (crime.getId().equals(uuid)) {
//                return crime;
//            }
//        }
//        return null;
    }

    public void addCrime(Crime c) {
        this.mCrimeList.add(c);
        this.indexCrime(c);
    }

    public void deleteCrime(UUID crimeId) {

        System.out.println("asdf: " + mIndexTable);

        //Удаляем по айди
        //Нужно достать индекс и удалить по индексу, после чего обновить всех, кто после
        if (mIndexTable.get(crimeId) != null) {
            //достали индекс по ID
            int index = mIndexTable.get(crimeId);

            //Удаляем из хранилища
            mCrimeList.remove(index);

            //Теперь удаляем индексирование
            mIndexTable.remove(crimeId);

            //Обновить индексы справа от элемента
            for (int i = index; i <= mCrimeList.size() - 1; i++) {
                System.out.println(i);
                /* Для всех элементов, которые стояли после удаленного элемента
                нужно обновить индексирование, потому что индекс устарел, т.к. на единицу больше */
                mIndexTable.put(mCrimeList.get(i).getId(),i);
            }

        } else {
            //не делаем ничего
        }

    }

    private void indexCrime(Crime c) {
        for (int i = 0; i < mCrimeList.size(); i++) {
            if (c.getId().equals(mCrimeList.get(i).getId())) {
                mIndexTable.put(c.getId(), i);
                return;
            }
        }
    }
    private void reindexList() {
        for (Crime c :
                mCrimeList) {
            indexCrime(c);
        }
    }

}
