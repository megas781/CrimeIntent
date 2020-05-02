package gghost.criminalintent.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gghost.criminalintent.model.database.CrimeDbSchema.CrimeTable;

public class CrimeDatabaseBuilder extends SQLiteOpenHelper {

    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeDatabaseBuilder(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * Метод, вызывающийся, если база данных еще не создана (только при первом включении)
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "create table %1$s (" +
                "_id integer primary key autoincrement," +
                "%2$s," + //uuid
                "%3$s," + //title
                "%4$s," + //date
                "%5$s," + //solved
                "%6$s," + //suspect
                "%7$s" + //phone number
                ")";

        db.execSQL(String.format(createTableQuery,
                CrimeTable.NAME,
                CrimeTable.Cols.UUID,
                CrimeTable.Cols.TITLE,
                CrimeTable.Cols.DATE,
                CrimeTable.Cols.SOLVED,
                CrimeTable.Cols.SUSPECT,
                CrimeTable.Cols.PHONE_NUMBER));
    }

    /**
     * Метод вызывается, весли значение вресии инициализируемой базы данных
     * не совпадает с хранящимся значением.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
