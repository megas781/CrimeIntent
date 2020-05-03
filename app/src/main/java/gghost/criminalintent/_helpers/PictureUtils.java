package gghost.criminalintent._helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {
    /**
     * Метод Scale'ит файл изображения в bitmap, соответствующий размерам виджета,
     * в который он будет установлен. Т.к. Bitmap не имеет сжатия, его размеры могут сильно
     * увеличиваться в отношении к размерам изображений в формате png, jpg и т.д.
     * @param path – путь к изображению
     * @param destWidth – ширина ImageView, в который будет устанавливаться изображение
     * @param destHeight – высота ImageView, в который будет устанавливаться изображение
     * @return Изображение собственной персоной
     */
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        //Понял! свойство inJustDecodeBounds говорит, что BitmapFactory нужно декодировать только
        //размер изображения и outMimeType, а не все изобажение попиксельно.
        //Это позволяет не расходовать память на загрузку большого изображения.
        options.inJustDecodeBounds = true;
        //Значения ширины и высоты устанавливаются в options после вызова decodeFile
        //(да, я тоже почему-то вспомнил про php с этим поцедурным подходом)
        BitmapFactory.decodeFile(path,options);

        //После декодирования и обновления объекта options находим исходные размеры изображения
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //inSmapleSize – это коэффициент больше либо равный единице, определяющий, сколько
        //горизонтальных пикселов исходного файла будет приходиться на один пиксел выводимого
        //декодируемого bitmap'a. Чем больше inSampleSize, тем меньше декодируемое изображение.
        int inSampleSize = 1;

        //Если хотя бы одна сторона больше, чем требуемые размеры
        if ((srcWidth > destWidth) || (srcHeight > destHeight)) {
            //то масштабируем
            float widthScale = srcWidth / destWidth;
            float heightScale = srcHeight / destHeight;

            // Т.к. мы масштабируем в духе css contain, рациональнее будет больше сжать, чем меньше.
            //Поэтому какой стороны больше коэффициент (которой стороне нужней), то сторону нужно больше сжать.
            inSampleSize = Math.round(Math.max(widthScale, heightScale));
        }

        //Создаем новые свойства декодирования
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Теперь наше изображение декодируется согласно параметрам destWidth и destHeight,
        //оптимизировано для полного вмещения картинки в ImageView
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        /* TODO: не понятно, как что за getWindowManager, и почему нужен getDefaultDisplay().
        * Есть другие дисплеи? И почему size устанавливается процедурно. Это пипец. */
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }
}
