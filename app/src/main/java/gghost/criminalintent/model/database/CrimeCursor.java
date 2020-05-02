package gghost.criminalintent.model.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import gghost.criminalintent.model.database.CrimeDbSchema.CrimeTable;
import gghost.criminalintent.model.Crime;

/**
 * Почему мы не extend'им просто Cursor, а именно CursorWrapper. Да потому что Cursor - это интерфейс,
 * а CursorWrapper - это класс
 */
public class CrimeCursor extends CursorWrapper {

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursor(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        /*
        Достаем значения из зафетченного объекта/строки
         */
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(this.getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        String phoneNumber = getString(getColumnIndex(CrimeTable.Cols.PHONE_NUMBER));
        /*
        Создаем экземпляр Crime по данным, которые достали
         */
        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        crime.setPhoneNumber(phoneNumber);

        return crime;
    }
}
