package gghost.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gghost.criminalintent.database.CrimeDbSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
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
                "%5$s" + //solved
                ")";

        db.execSQL(String.format(createTableQuery,
                CrimeTable.NAME,
                CrimeTable.Cols.UUID,
                CrimeTable.Cols.TITLE,
                CrimeTable.Cols.DATE,
                CrimeTable.Cols.SOLVED));


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
