package GGhost.criminalintent.model;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private List<Crime> mCrimeList;

    private static CrimeLab sInstance;

    private Hashtable<UUID, Integer> mIndexTable = new Hashtable<>();

    public static CrimeLab get(Context context) {
        if (sInstance == null) {
            sInstance = new CrimeLab(context);
        }
        return sInstance;
    }

    private CrimeLab(Context context) {
        //

        mCrimeList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Crime newCrime = new Crime();
            newCrime.setTitle("Crime #" + (i + 1));
            newCrime.setDate(new Date());
            newCrime.setSolved((int) Math.round(Math.random()) == 1);
            newCrime.setRequiresPolice((int) Math.round(Math.random()) == 1);

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

}
