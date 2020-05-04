package gghost.criminalintent.crime_detail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.File;

import gghost.criminalintent.R;
import gghost.criminalintent._helpers.PictureUtils;

public class CrimePhotoDetailFragment extends DialogFragment {

    private static String ARG_PHOTO_URI_KEY = "ARG_PHOTO_URI_KEY";

    private ImageView mImageView;
    private Button mCloseButton;
    private String mPhotoPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPhotoPath = getArguments().getString(ARG_PHOTO_URI_KEY, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_photo_detail, container, false);

        mImageView = v.findViewById(R.id.photo_detail_image_view_id);

        //Пытаемся достать файл по данному пути
        if (new File(mPhotoPath).exists()) {
            mImageView.setImageBitmap(PictureUtils.getScaledBitmap(mPhotoPath, getActivity()));
        } else {

        }

        mCloseButton = v.findViewById(R.id.photo_detail_close_button_id);
        mCloseButton.setOnClickListener(mOnCloseButtonClickListener);

        ViewTreeObserver vto = v.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Найдем aspectRatio нашего bitmap'a
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mPhotoPath, options);

                int srcHeight = options.outHeight;
                int srcWidth = options.outWidth;

                //Отношение высоты к ширине
                float aspectRatio = (float) srcHeight / (float) srcWidth;

                Point size = new Point();
                //Ааррр, процедурное извелечение данных. За что?!
                getActivity().getWindowManager().getDefaultDisplay().getSize(size);

                int width = size.x;
                int height = Math.round((float) size.x * aspectRatio);

                //Теперь мы имеем нужные размеры. Пора загрузить изображение
                options = new BitmapFactory.Options();
                options.outHeight = height;
                options.outWidth = width;
                //Создаем bitmap согласно высчитанным размерам, которые мы поместили в options
                Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, options);
                mImageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                mImageView.setImageBitmap(bitmap);
            }
        });

        return v;
    }


    private OnClickListener mOnCloseButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            CrimePhotoDetailFragment.this.dismiss();
        }
    };

    public static CrimePhotoDetailFragment newInstance(String photoPath) {

        Bundle args = new Bundle();

        args.putString(ARG_PHOTO_URI_KEY, photoPath);

        CrimePhotoDetailFragment fragment = new CrimePhotoDetailFragment();

        fragment.setArguments(args);
        return fragment;
    }
}
