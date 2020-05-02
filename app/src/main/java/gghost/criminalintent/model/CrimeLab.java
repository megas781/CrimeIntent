package gghost.criminalintent.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import gghost.criminalintent.model.database.CrimeDatabaseBuilder;
import gghost.criminalintent.model.database.CrimeCursor;
import gghost.criminalintent.model.database.CrimeDbSchema.CrimeTable;

public class CrimeLab {

    private static CrimeLab sInstance;
    //Массив преступлений

    private Context mContext;
    private SQLiteDatabase mDatabase;

    //Инициализатор
    private CrimeLab(Context context) {
        //Достаем глобальный контекст приложения
        mContext = context.getApplicationContext();
        //Инициализируем базу данных
        mDatabase = new CrimeDatabaseBuilder(mContext).getWritableDatabase();

//        mCrimeList = new ArrayList<>();
    }


    //Публичные методы
    public static CrimeLab get(Context context) {
        if (sInstance == null) {
            sInstance = new CrimeLab(context);
        }
        return sInstance;
    }
    public ArrayList<Crime> getCrimeList() {

        ArrayList<Crime> crimeList = new ArrayList<>();

        //два null обозначают "брать все!"
        CrimeCursor cursorWrapper = queryCrimes(null, null);
        try {
            cursorWrapper.moveToFirst();
            /* Свойство cursor.IsAfterLast() сообщает, что указатель вышел за пределы последней
            * строки и сейчас указывает на пустоту. В цикле while мы говорим, что когда это произойдет
            * цикл нужно завершить */
            while (!cursorWrapper.isAfterLast()) {
                /* Каждый раз, когда мы двигаемся вперед .moveToNext(), cursor начинает указывать
                * на новую строку, и, следовательно, мы получим другой объект при вызове метода
                * cursor.getCrime() */
                crimeList.add(cursorWrapper.getCrime());
                cursorWrapper.moveToNext();
            }
        } finally {
            //Зачем-то вызывется cursor.close(). Наверное, это освобождает память

            /* В книге написано, что если не закрывать курсоры, то могут закончиться файловые
            * дескрипторы, что бы это не значило. По инфе из интернета файловый дескриптор - это
            * неотрицательное число, являющееся идентефикатром потока данных (inout). Типа как порт. */
            cursorWrapper.close();
        }

        return crimeList;
    }
    public Crime getCrime(UUID uuid) {

        CrimeCursor crimeCursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()});

        try {
            //Если из базы данных ничего не найдено
            if (crimeCursor.getCount() == 0) {
                return null;
            } else {
                //По идеи по uuid мы должны получить только одну строку, поэтому идем
                //К первому попавшемуся и возвращаем его
                crimeCursor.moveToFirst();
                return crimeCursor.getCrime();
            }
        } finally {
            //Не забываем закрывать курсоры
            crimeCursor.close();
        }
    }
    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        mDatabase.insertOrThrow(CrimeTable.NAME,null,values);
    }
    public void deleteCrime(UUID crimeId) {
        mDatabase.delete(
                CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?",
                new String[] {crimeId.toString()});
    }
    public void updateCrime(Crime crime) {
        /*
        Конструкция update принимает:
        1) Имя таблицы, чтобы определить, куда вставлять
        2) Объект класса ContentValues, чтобы определить, что вставлять
        3) логическое выражение, которое будет вставлено в запрос после ключегого "where"
        4) Массив параметров, которые будут вставляться вапрос вместо знаков вопроса (?)
        Аргументы не вставляются в логическое выражение напрямую, а передаются через массив строк
        во избежание SQL-инъекций
         */
        mDatabase.update(
                CrimeTable.NAME,
                getContentValues(crime),
                CrimeTable.Cols.UUID + " = ?",
                new String[] {crime.getId().toString()});

    }


    //Метод для преобразования объекта Java в строку таблицы
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();

        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.UUID,crime.getId().toString());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        //TODO: вернуть на место isSolved
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.Cols.PHONE_NUMBER, crime.getPhoneNumber());

        return values;
    }
    private CrimeCursor queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,whereClause,
                whereArgs,
                null,
                null,
                null);

        return new CrimeCursor(cursor);
    }


}
