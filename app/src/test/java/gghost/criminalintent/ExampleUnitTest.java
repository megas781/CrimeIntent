package gghost.criminalintent;

import android.app.Activity;
import android.os.Bundle;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import gghost.criminalintent.crime_list.CrimeListFragment;
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
//        System.out.println(storage.mIndexTable);

    }

    @Test
    public void stringFormattingTest() {

        String query = "create table %1$s (" +
                "%2$s," +
                "%3$s," +
                "%4$s" +
                ")";
        System.out.println(String.format(query, 1,3,4,5));

    }

    public void addCrimeTest() {
        CrimeLab storage = CrimeLab.get(new Activity());
        Crime newCrime = new Crime();
        storage.addCrime(newCrime);

        System.out.println(storage.getCrime(newCrime.getId()));

    }

    @Test
    public void testArraySaveToBundle() {
        Bundle args = new Bundle();
        ArrayList<Crime> crimes = new ArrayList<>();
        crimes.add(new Crime());
//        Crime[] rawCrimes = crimes.toArray();
        args.putSerializable("list", crimes.toArray());

        System.out.println(args.getSerializable("list"));
    }
}