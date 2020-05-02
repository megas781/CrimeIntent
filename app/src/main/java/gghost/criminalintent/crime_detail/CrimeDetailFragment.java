package gghost.criminalintent.crime_detail;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import gghost.criminalintent.R;
import gghost.criminalintent.model.Crime;
import gghost.criminalintent.model.CrimeLab;

public class CrimeDetailFragment extends Fragment {

    private static final String ARG_CRIME_ID_KEY = "ARG_CRIME_ID_KEY";
    private static final String ARG_IS_NEW_KEY = "ARG_IS_NEW_KEY";
    //Тег для FragmentManager'a
//    private static final String DATE_PICKER_FRAGMENT_TAG = "DATE_PICKER_FRAGMENT_TAG";
    //Код для TargetFragment'a
    private static final int DATE_PICKER_FRAGMENT_REQUEST_CODE = 0;
    private static final int TIME_PICKER_FRAGMENT_REQUEST_CODE = 1;
    private static final int PICK_CONTACT_REQUEST_CODE = 2;
    private static final int READ_CONTACTS_TO_PICK_CRIMINAL_PERMISSION_REQUEST_CODE = 10;

    private static final int MENU_ITEM_DELETE_CRIME_ID = 1;

    private Crime mCrime;
    private boolean mIsNew;
    private EditText mTitleTextField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mIsSolvedCheckbox;
    private Button mChooseSuspectButton;
    private Button mReportCrimeButton;
    private Button mCallCriminalButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("getArguments()    : " + (getArguments() != null));
        System.out.println("savedInstanceState: " + (savedInstanceState != null));

        //Миссия: достать crimeId

        UUID crimeId = null;
        //Сначала пытаемся достать ID из состояния. Оно приорететно над аргументом
        if (savedInstanceState != null) {
            //Если есть сохранение в состоянии, то используем его
            crimeId = (UUID) savedInstanceState.getSerializable(ARG_CRIME_ID_KEY);
        }
        //Если в состоянии ничего не нашлось, то хотя бы из аргументов нужно подгрузить
        if (crimeId == null) {
            crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID_KEY);
        }
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mIsNew = getArguments().getBoolean(ARG_IS_NEW_KEY, false);

        this.setHasOptionsMenu(true);


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_CRIME_ID_KEY, mCrime.getId());
    }

    @Override
    public void onPause() {
        super.onPause();
        //Обновляем данные о преступлении. Это нужно, например, при свайпе, или при нажатии
        //кнопки back после окончания редактирования
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_detail, container, false);

        mDateButton = v.findViewById(R.id.crime_date_button);
        mDateButton.setOnClickListener(onDateButtonClickListener);

        mTimeButton = v.findViewById(R.id.crime_time_button);
        mTimeButton.setOnClickListener(onTimeButtonClickListener);

        mIsSolvedCheckbox = v.findViewById(R.id.crime_solved_id);
        mIsSolvedCheckbox.setOnCheckedChangeListener(mCheckboxListener);

        mTitleTextField = v.findViewById(R.id.crime_title_edit_view_id);
        mTitleTextField.addTextChangedListener(this.crimeEditTextListener);

        mReportCrimeButton = v.findViewById(R.id.report_crime_button_id);
        mReportCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                /* Создаем неявный интент */
//                Intent i = new Intent(Intent.ACTION_SEND);
//                //Указываем MIME type, а Android сам решит, какие приложения могут захендлить этот Intent
//                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
//                i.putExtra(Intent.EXTRA_SUBJECT, R.string.crime_report_subject);
//                /* Странно, но чтобы указать надпись при выборе приложения, нужно
//                * воспользоваться методом Intent.createChooser(Intent, String) */
//                //i = Intent.createChooser(i, getString(R.string.choose_app_to_share));
//                startActivity(i);

                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(R.string.choose_app_to_share)
                        .getIntent();
                startActivity(i);

            }
        });


        //ВЫБОР ПОДОЗРЕВАЕМОГО
        mChooseSuspectButton = v.findViewById(R.id.choose_suspect_button_id);
        //Устанавливаем значение для кнопки, если оно есть
        if (mCrime.getSuspect() != null) {
            mChooseSuspectButton.setText(mCrime.getSuspect());
        }

        /* TODO: не знаю решения, как обойтись только одним Intent'ом. Пока приходится создавать его
         *   и тут и там */
        //Intent для на открытие приложения контактов с последующим выбором контакта
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
        /* Нужно сделать проверку на то, есть ли у операционной системы компонент для обработки
         * запроса контакта. PackageManager – это такой подопечный Android'a, который знает всё обо всех.
         * У него можно спросить, существует ли на устройстве такая активность, которая обработает данный интент.
         * Так же мы можем отфильтровать поиск активностей по категориям, чтобы искались только активности
         * под категорией DEFAULT*/
        //Если данные об обработчике интента не найдены, то кнопка блокируется
        if (getActivity().getPackageManager().resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mChooseSuspectButton.setEnabled(false);
        } else {
            //Если приложение-обработчик существует, то добавляем слушатель
            mChooseSuspectButton.setOnClickListener(onChooseCriminalButtonClickListener);
        }

        //по-моему бесполезная строка
//        ((AppCompatActivity) Objects.requireNonNull(this.getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCallCriminalButton = v.findViewById(R.id.call_criminal_button_id);
        mCallCriminalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Здесь нужно позвонить преступнику

//                ContentResolver resolver = getActivity().getContentResolver();
////                String[] proj = new String[]{
////                        Contacts.DISPLAY_NAME,
////                        Contacts._ID,
////                        CommonDataKinds.Phone.NUMBER
////                };
//                Cursor contactsCursor = resolver.query(CommonDataKinds.Phone.CONTENT_URI, null, CommonDataKinds.Phone._ID + " = ?", new String[]{"58"}, null);
//
//                System.out.println();
//                contactsCursor.moveToFirst();
//                while (!contactsCursor.isAfterLast()) {
//                    String name = contactsCursor.getString(0);
//                    String id = contactsCursor.getString(1);
//                    String phoneNumber = contactsCursor.getString(2);
//                    System.out.println(name + " (" + id + ") – " + phoneNumber);
//                    contactsCursor.moveToNext();
//                }
//                System.out.println();

            }
        });


        if (mIsNew) {
            mTitleTextField.requestFocus();
        }

        System.out.println("mCrime: " + mCrime);

        this.updateUI();

        return v;
    }


    /**
     * обработчик данных от потомков
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DATE_PICKER_FRAGMENT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        mCrime.setDate(DatePickerActivity.fetchDateFromIntent(data));

                    } else {
                        throw new NullPointerException("No data from datePickerFragment, but expected");
                    }
                }
                updateUI();
                break;
            case TIME_PICKER_FRAGMENT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        mCrime.setDate(TimePickerFragment.fetchDateFromIntent(data));
                    } else {
                        throw new NullPointerException("No data from timePickerFragment, but expected");
                    }
                }
                updateUI();
            case PICK_CONTACT_REQUEST_CODE:

                if (data != null) {
                    Uri contactUri = data.getData();
                    ContentResolver resolver = getActivity().getContentResolver();

                    /* Определение полей, которые должны быть возвращены запросом */
                    String[] queryFields = new String[]{Contacts.DISPLAY_NAME, Contacts._ID};
                    /* Выполнение запроса по URI */
                    Cursor c = resolver.query(contactUri, queryFields, null, null, null);
                    if (c.getCount() != 0) {
                        //Извлечение столбца из списка подозреваемых
                        c.moveToFirst();
                        String suspectString = c.getString(c.getColumnIndex(Contacts.DISPLAY_NAME));
                        mCrime.setSuspect(suspectString);
                        mChooseSuspectButton.setText(mCrime.getSuspect());


                        /* А теперь я по Id контакта хочу достать его номер телефона */
                        String contactId = c.getString(c.getColumnIndex(Contacts._ID));
                        //Создаем запрос на данные о контакте с данным ID
                        Cursor dataCursor = resolver.query(CommonDataKinds.Phone.CONTENT_URI, new String[]{
                                CommonDataKinds.Phone.NUMBER,
                                CommonDataKinds.Phone.CONTACT_ID
                        }, CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);

                        dataCursor.moveToFirst();

                        System.out.println("phone number: " + dataCursor.getString(dataCursor.getColumnIndex(CommonDataKinds.Phone.NUMBER)));
                        dataCursor.close();
                    } else {

                    }


                    c.close();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_CONTACTS_TO_PICK_CRIMINAL_PERMISSION_REQUEST_CODE:
                //Так как мы знаем, что мы запрашивали только одно разрешение, можем догадаться
                //Что в GrantResults только одно число, определяющее статус разрешения на чтение контактов
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Снова вызываем слушателя, чтобы уже отобразить выбор контакта
                    mChooseSuspectButton.callOnClick();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Listener изменения названия преступления
     */
    private final TextWatcher crimeEditTextListener = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Именно .toString, а не (String) s. Последний почему-то вызывает ошибку
            mCrime.setTitle(s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Event-listener смены значения checkbox'a
     */
    private final CompoundButton.OnCheckedChangeListener mCheckboxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mCrime.setSolved(isChecked);
        }
    };

    /**
     * Event listener нажатия на кнопку даты
     */
    private final View.OnClickListener onDateButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            startActivityForResult(DatePickerActivity.createIntent(getActivity(), mCrime.getDate()), DATE_PICKER_FRAGMENT_REQUEST_CODE);

//            FragmentManager fm = getFragmentManager();
//            DatePickerFragment datePicker = DatePickerFragment.newInstance(mCrime.getDate());
//
//            //datePicker устанавлевает целевой фрагмент, которому будет отдавать данные
//            datePicker.setTargetFragment(CrimeFragment.this, DATE_PICKER_FRAGMENT_REQUEST_CODE);
//            /*datePicker просит дать ему контакты  fragmentManager'a, шоб с ним договориться, чтоб
//             * он его показал. DatePicker представился как DATE_PICKER_FRAGMENT_TAG */
//            datePicker.show(fm, DATE_PICKER_FRAGMENT_TAG);
        }
    };
    private final View.OnClickListener onTimeButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Нажали на время. Нужно отобразить TimePicker
            TimePickerFragment timePicker = TimePickerFragment.newInstance(
                    CrimeDetailFragment.this,
                    TIME_PICKER_FRAGMENT_REQUEST_CODE,
                    mCrime.getDate());
            timePicker.show(Objects.requireNonNull(getFragmentManager()), null);

        }
    };
    private final View.OnClickListener onChooseCriminalButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);

            //Проверяем, есть ли доступ к контактам
            if (getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //У нас есть доступ к контактам, значит просто выполняем request
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST_CODE);
            } else {
                //Просим доступ к контактам
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {

                    TextView alertText = new TextView(getActivity());
                    alertText.setPadding(16, 16, 16, 16);
                    alertText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    alertText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    alertText.setText(R.string.read_contacts_permission_reason);

                    new AlertDialog.Builder(getActivity())
                            .setView(alertText)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_TO_PICK_CRIMINAL_PERMISSION_REQUEST_CODE);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                } else {
                    //Если по каким-то причинам OS не считает нужным объяснение причины разрешения,
                    //в таком случае просто просим разрешения
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_TO_PICK_CRIMINAL_PERMISSION_REQUEST_CODE);
                }

            }

        }
    };

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(Menu.NONE, MENU_ITEM_DELETE_CRIME_ID, Menu.NONE, R.string.delete_crime);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_DELETE_CRIME_ID:
                CrimeLab.get(getActivity()).deleteCrime(mCrime.getId());
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateUI() {

        if (mCrime != null) {
            mTitleTextField.setText(mCrime.getTitle());
            DateFormat df = new SimpleDateFormat("dd MMM, YYYY", new Locale("ru"));
            mDateButton.setText(df.format(mCrime.getDate()));
            df = new SimpleDateFormat("HH:mm", new Locale("ru"));
            mTimeButton.setText(df.format(mCrime.getDate()));
            mIsSolvedCheckbox.setChecked(mCrime.isSolved());

        } else {
            throw new NullPointerException("mCrime == null у CrimeDetailFragment");
        }

    }

    @NonNull
    private String getCrimeReport() {
        String solvedString = mCrime.isSolved() ? getString(R.string.crime_report_solved) : getString(R.string.crime_report_unsolved);
        String dateString = new SimpleDateFormat().format(mCrime.getDate());
        String suspectString = mCrime.getSuspect() == null ? getString(R.string.crime_report_no_suspect) : mCrime.getSuspect();
        String reportString = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspectString);
        return reportString;
    }


    @NonNull
    public static CrimeDetailFragment newInstance(UUID crimeId, boolean isNew) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID_KEY, crimeId);
        args.putBoolean(ARG_IS_NEW_KEY, isNew);
        CrimeDetailFragment fragment = new CrimeDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
