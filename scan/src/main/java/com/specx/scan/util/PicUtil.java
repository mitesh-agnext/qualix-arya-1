package com.specx.scan.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.core.app.R;
import com.core.app.util.AlertUtil;
import com.tarek360.instacapture.utility.Logger;
import com.vansuita.pickimage.bean.PickResult;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class PicUtil {

    private static final String SCREENSHOTS_DIRECTORY_NAME = "picked";
    private static final int JPEG_COMPRESSION_QUALITY = 75;

    private PicUtil() {
    }

    private static File getScreenshotFile(@NonNull final Context applicationContext) {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss.SS", Locale.getDefault());

        String fileName = "screenshot-" + dateFormat.format(new Date()) + ".jpg";

        final File screenshotsDir =
                new File(applicationContext.getFilesDir(), SCREENSHOTS_DIRECTORY_NAME);
        screenshotsDir.mkdirs();
        return new File(screenshotsDir, fileName);
    }

    private static File saveBitmapToFile(@NonNull final Context context, @NonNull final Bitmap bitmap) {

        OutputStream outputStream = null;
        File screenshotFile = getScreenshotFile(context);

        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(screenshotFile));

            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_COMPRESSION_QUALITY, outputStream);

            outputStream.flush();

            Timber.d("Screenshot saved to %s", screenshotFile.getAbsolutePath());
        } catch (final IOException e) {
            Timber.e(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (final IOException ignored) {
                    Timber.e("Failed to close OutputStream.");
                }
            }
        }
        return screenshotFile;
    }

    public static Observable<File> getScreenshotFileObservable(@NonNull final Context context,
                                                               @NonNull final Bitmap bitmap) {

        return Observable.create((ObservableOnSubscribe<File>) subscriber -> {
            OutputStream outputStream = null;
            try {
                File screenshotFile = PicUtil.getScreenshotFile(context);

                outputStream = new BufferedOutputStream(new FileOutputStream(screenshotFile));

                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_COMPRESSION_QUALITY, outputStream);

                outputStream.flush();

                subscriber.onNext(screenshotFile);
                subscriber.onComplete();

                Logger.INSTANCE.d("Screenshot saved to " + screenshotFile.getAbsolutePath());
            } catch (final IOException e) {
                subscriber.onError(e);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (final IOException ignored) {
                        Timber.e("Failed to close OutputStream.");
                    }
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public static Uri parseResult(Context context, PickResult result, boolean checkSize, int maxSize) {
        if (result != null) {
            if (!TextUtils.isEmpty(result.getPath())) {

                Uri uri = Uri.parse(result.getPath());

                Timber.d("Uri: %s", uri);

                File file = new File(uri.toString());

                if (file.exists()) {
                    long fileSizeInBytes = file.length();
                    long fileSizeInMB = fileSizeInBytes / 1048576;

                    Timber.d("File size: " + fileSizeInBytes / 1024 + " KB");

                    if (checkSize && fileSizeInMB >= maxSize) {
                        AlertUtil.showToast(context, context.getString(R.string.file_upload_size_error));
                        return null;
                    }

                    return uri;
                } else {
                    Timber.d("File doesn't exist!");
                    AlertUtil.showToast(context, "File doesn't exist!");
                }
            } else {
                if (result.getError() != null) {
                    AlertUtil.showToast(context, result.getError().getMessage());
                } else {
                    AlertUtil.showToast(context, context.getString(R.string.empty_filepath_msg));
                }
            }
        } else {
            AlertUtil.showToast(context, context.getString(R.string.unknown_error_msg));
        }
        return null;
    }
}
