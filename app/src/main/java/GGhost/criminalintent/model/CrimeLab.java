package GGhost.criminalintent.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
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

    private CrimeLab(Context context) {
        //
        mCrimeList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Crime newCrime = new Crime();
            newCrime.setTitle("Crime #" + (i + 1));
            newCrime.setDate(new Date());
            newCrime.setSolved((int) Math.round(Math.random()) == 1);
            newCrime.setRequiresPolice((int) Math.round(Math.random()) == 1);
            mCrimeList.add(newCrime);
        }
    }

    public List<Crime> getCrimeList() {
        return this.mCrimeList;
    }

    public Crime getCrime(UUID uuid) {
        for (Crime crime : this.mCrimeList) {
            if (crime.getId().equals(uuid)) {
                return crime;
            }
        }
        return null;
    }

}
