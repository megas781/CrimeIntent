package gghost.criminalintent.crime_detail;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import gghost.criminalintent.R;
import gghost.criminalintent._helpers.PictureUtils;
import gghost.criminalintent.model.Crime;
import gghost.criminalintent.model.CrimeLab;

public class CrimeDetailFragment extends Fragment {

    public interface Delegate {
        void onCrimeUpdated(Crime crime);
        void onCrimeDeleted();
    }

    private static final String PACKAGE_AUTHORITY = "gghost.criminalintent";

    private static final String ARG_CRIME_KEY = "ARG_CRIME_ID_KEY";
    private static final String ARG_CRIME_PHOTO_FILE_KEY = "ARG_CRIME_PHOTO_FILE_KEY";
    private static final String ARG_IS_NEW_KEY = "ARG_IS_NEW_KEY";
    private static final String ARG_DELEGATE_KEY = "ARG_DELEGATE_KEY";
    //Код для TargetFragment'a
    private static final int DATE_PICKER_FRAGMENT_REQUEST_CODE = 0; //Explicit Intent
    private static final int TIME_PICKER_FRAGMENT_REQUEST_CODE = 1; //Explicit Intent

    private static final int PICK_CONTACT_REQUEST_CODE = 2; //Implicit Intent
    private static final int CAPTURE_PHOTO_REQUEST_CODE = 3; //Implicit Intent

    private static final int READ_CONTACTS_TO_PICK_CRIMINAL_PERMISSION_REQUEST_CODE = 10; //Permission accepted code

    private static final int MENU_ITEM_DELETE_CRIME_ID = 1; //Code of menu item

    private Crime mCrime;
    private File mPhotoFile;
    private boolean mIsNew;

    private EditText mTitleTextField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mIsSolvedCheckbox;
    private Button mChooseSuspectButton;
    private Button mReportCrimeButton;
    private Button mCallCriminalButton;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;

    private Delegate mDelegate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        System.out.println("getArguments()    : " + (getArguments() != null));
//        System.out.println("savedInstanceState: " + (savedInstanceState != null));

        //Миссия: достать crime
        //Сначала пытаемся достать ID из состояния. Оно приорететно над аргументом
        if (savedInstanceState == null) {
            //Если state пустой, то загружаем данные из аргументов
            if (getArguments() != null) {
                mCrime = (Crime) getArguments().getSerializable(ARG_CRIME_KEY);
                mIsNew = getArguments().getBoolean(ARG_DELEGATE_KEY, false);
                mPhotoFile = (File) getArguments().getSerializable(ARG_CRIME_PHOTO_FILE_KEY);
            } else {
                //Аргументы должны быть всегда, иначе бросаем исключение
                throw new NullPointerException("getArguments() must not be null");
            }
        } else {
            //Если state имеется, то подгружаем данные из него
            mCrime = (Crime) savedInstanceState.getSerializable(ARG_CRIME_KEY);
            mIsNew = savedInstanceState.getBoolean(ARG_DELEGATE_KEY, false);
            mPhotoFile = (File) savedInstanceState.getSerializable(ARG_CRIME_PHOTO_FILE_KEY);
        }

        this.setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_detail, container, false);

        mPhotoView = v.findViewById(R.id.crime_photo_id);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrimePhotoDetailFragment fragment = CrimePhotoDetailFragment.newInstance(mPhotoFile.getPath());
                fragment.show(getActivity().getSupportFragmentManager(),null);
            }
        });


        mPhotoButton = v.findViewById(R.id.crime_camera_button_id);
        //Интент число для проверки возможности снимка
        final Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Intent.resolveActivity делает почти то же самое, что и packageManager.resolveActivity,
        //только последний по-моему дает больше информации об активности, в отличие от Intent'овского
        //который возвращает только имя пакета и имя класса(приложения)
        boolean canTakePhoto = captureImageIntent.resolveActivity(getActivity().getPackageManager()) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Создаем неявный интент для камеры
                //Создаем путь для файла. Путь генерируется FileProvider'ом и преобразуется в Uri
                //т.к. приложение работает только с этим типом данных
                Uri newPhotoUri = FileProvider.getUriForFile(getActivity(), PACKAGE_AUTHORITY, mPhotoFile);
                //Мы говорим интенту, куда сохранить данные фотографии, указывая ключом
                //константу MediaStore.EXTRA_OUTPUT и кладя туда сгенерированный uri
                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, newPhotoUri);

                //метод queryIntentActivities круче чем resolveActivity. ResolveActivity просто выбирает
                //одну активность, которая на взгляд ОС больше всего подходит для данного интента.
                //А queryIntentActivities возвращает список из информаций обо всех активностях,
                //способных захендлить intent.
                //Thus, cameraActivities содержит инфу обо всех "камерных" активностях
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImageIntent, PackageManager.MATCH_DEFAULT_ONLY);
                //Но логично предположить, что каждому из этих активностей нужно единичное
                //разрешение на запись в нашу файловую систему. Нужно им его подарить
                for (ResolveInfo resolveInfo : cameraActivities) {
                    /* TODO: почитать, чем отличается resolveInfo от activityInfo */
                    /* Что я понял:
                     * Активность может раздавать разовые разрешения (типа токены) на запись
                     * в файловую систему. В методе grantUriPermission указывается, какому пакету
                     * дается разрешение, далее нужно указать uri, в который активность будет записывать данные
                     * и последним штрихоим нужно поставить флаг, какой тип разрешения дается:
                     * FLAG_GRANT_READ_URI_PERMISSION или FLAG_GRANT_WRITE_URI_PERMISSION */
                    //TODO: выяснить, почему камеры работают без разрешения. Код закомменчен специально
                    //getActivity().grantUriPermission(resolveInfo.activityInfo.packageName, newPhotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                //После того, как мы раздали единовременную возможность записывать всем возможным
                //активностям, можно вызывать неявный интент
                startActivityForResult(captureImageIntent, CAPTURE_PHOTO_REQUEST_CODE);
            }
        });

        mDateButton = v.findViewById(R.id.crime_date_button);
        mDateButton.setOnClickListener(onDateButtonClickListener);

        mTimeButton = v.findViewById(R.id.crime_time_button);
        mTimeButton.setOnClickListener(onTimeButtonClickListener);

        mIsSolvedCheckbox = v.findViewById(R.id.crime_solved_id);
        mIsSolvedCheckbox.setOnCheckedChangeListener(mCheckboxListener);

        mTitleTextField = v.findViewById(R.id.crime_title_edit_view_id);
        mTitleTextField.addTextChangedListener(crimeEditTextListener);
        mTitleTextField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        mReportCrimeButton = v.findViewById(R.id.report_crime_button_id);
        mReportCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        /* TODO: не знаю решения, как обойтись только одним Intent'ом. Пока приходится создавать его
         *   и тут и там */
        //Intent для на открытие приложения контактов с последующим выбором контакта
        final Intent pickContactIntent = new Intent(Intent.ACTION_PICK);
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
        //Устанавливаем значение для кнопки, если оно есть
        if (mCrime.getSuspect() != null) {
            mChooseSuspectButton.setText(mCrime.getSuspect());
            mChooseSuspectButton.setContentDescription(getString(R.string.choose_another_suspect_description, mCrime.getSuspect()));
        } else {
            mChooseSuspectButton.setContentDescription(getString(R.string.crime_choose_suspect_label));
        }

        //по-моему бесполезная строка
//        ((AppCompatActivity) Objects.requireNonNull(this.getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCallCriminalButton = v.findViewById(R.id.call_criminal_button_id);
        mCallCriminalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Здесь нужно позвонить преступнику
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mCrime.getPhoneNumber()));
                startActivity(callIntent);
            }
        });


//        System.out.println("mCrime: " + mCrime);

        //Event, когда подсчитаются размеры виджетов
        ViewTreeObserver vto = v.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhotoImageView();
            }
        });

        this.updateUI();

        if (mIsNew) {
            mTitleTextField.requestFocus();
        }

        return v;
    }


    @Override
    public void onPause() {
        super.onPause();
        //Обновляем данные о преступлении. Это нужно, например, при свайпе, или при нажатии
        //кнопки back после окончания редактирования
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        /*TODO: на само деле так делать не совсем правильно. Делегат нужно присваивать
           в инициализаторе newInstance. Ну да ладно. В книжке показано так */
        //поставил хоть какую-то проверку на реализацию интерфейса
        if (context instanceof CrimeDetailFragment.Delegate) {
            mDelegate = (CrimeDetailFragment.Delegate) context;
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        //Убираем связь с CrimeListActivity
        mDelegate = null;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_CRIME_KEY, mCrime);
        outState.putBoolean(ARG_IS_NEW_KEY,mIsNew);
        outState.putSerializable(ARG_CRIME_PHOTO_FILE_KEY,mPhotoFile);
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
                updateCrime();
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
                updateCrime();
                updateUI();
                break;
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
                        Cursor phoneCursor = resolver.query(CommonDataKinds.Phone.CONTENT_URI, new String[]{
                                CommonDataKinds.Phone.NUMBER,
                                CommonDataKinds.Phone.CONTACT_ID
                        }, CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);

                        if (phoneCursor.getCount() > 0) {
                            phoneCursor.moveToFirst();
                            String derivedPhoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(CommonDataKinds.Phone.NUMBER));
                            System.out.println("derived phone number: " + derivedPhoneNumber);
                            mCrime.setPhoneNumber(derivedPhoneNumber);
                        } else {
                            //если нет номера, то обнуляем его в mCrime
                            mCrime.setPhoneNumber(null);
                        }

                        mCallCriminalButton.setVisibility(mCrime.getPhoneNumber() != null ? View.VISIBLE : View.GONE);
                        phoneCursor.close();
                    } else {

                    }


                    c.close();
                }
                break;
            case CAPTURE_PHOTO_REQUEST_CODE:
                //После того, как изображение установилось, нужно обнулить разрешения
                //Извлекаем uri файла, который был создан
                Uri photoUri = FileProvider.getUriForFile(getActivity(), PACKAGE_AUTHORITY, mPhotoFile);
                //revokeUriPermission забирает все данные разрешения на данный uri единовременно.
                getActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //Есть еще revokeUriPermission с другой сигнатурой, в которой указывается имя пакета
                //у которого отнимается разрешение.
                updatePhotoImageView();

                //Accessablility event announcement about change of photo
                getView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getView().announceForAccessibility(getString(R.string.photo_was_changed_acc_announce));
                    }
                }, 300);
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
            updateCrime();
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
            updateCrime();
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
                mDelegate.onCrimeDeleted();
                getFragmentManager().beginTransaction().remove(this).commit();
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
            mCallCriminalButton.setVisibility(mCrime.getPhoneNumber() != null ? View.VISIBLE : View.GONE);
            //Из-за того, чтоы updatePhotoImageView() переместился в onGlobalLayout event,
            //есть мнение, что здесь его использовать не стоит
            //updatePhotoImageView();
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
    private void updatePhotoImageView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            //Используем setImageDrawable, потому что суда можно полжить null
            mPhotoView.setImageDrawable(null);
            mPhotoView.setContentDescription(getString(R.string.crime_photo_no_image_description));
        } else {
            /* Почему мы не можем использовать mPhotoView.getWidth и mPhotoView.getHeight?
            * Потому что mPhotoView будет вызываться внутри onCreateView. А первый подсчет размеров
            * виджетов происходит только после onResume(), который позже onCreateView.
            * Поэтому приходится довольствоваться тем, что есть. */
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), mPhotoView.getWidth(), mPhotoView.getHeight());
            System.out.println("bitmap size: " + bitmap.getWidth() + "px " + bitmap.getHeight() + "px");
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setContentDescription(getString(R.string.crime_photo_image_description));
        }
    }
    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mDelegate.onCrimeUpdated(mCrime);
    }

    @NonNull
    public static CrimeDetailFragment newInstance(Crime crime, File photoFile, boolean isNew) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_KEY, crime);
        args.putSerializable(ARG_CRIME_PHOTO_FILE_KEY, photoFile);
        args.putBoolean(ARG_IS_NEW_KEY, isNew);
        CrimeDetailFragment fragment = new CrimeDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
