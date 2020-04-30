package gghost.criminalintent;

import android.app.Activity;

import org.junit.Test;

import java.util.List;

import gghost.criminalintent.model.Crime;
import gghost.criminalintent.model.CrimeLab;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void crimeLabDeleteTest() {
        CrimeLab storage = CrimeLab.get(new Activity());

        List<Crime> crimeList = storage.getCrimeList();

        //второе преступление по индексу 1
        Crime crime = crimeList.get(1);

        System.out.println(crime);

        storage.deleteCrime(crime.getId());


        System.out.println(crimeList.toString());
        System.out.println(storage.mIndexTable);



    }
}