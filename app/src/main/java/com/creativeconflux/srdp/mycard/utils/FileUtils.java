package com.creativeconflux.srdp.mycard.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.creativeconflux.srdp.mycard.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by srdpatel on 2/28/2018.
 */

public class FileUtils {

    public static String getImagePathFromUri(Uri contentUri) {
        return getFileFromUri(contentUri).getAbsolutePath();
    }

    public static File getFileFromUri(Uri contentUri) {
        File file = null;
        if (contentUri != null) {
            file = new File(contentUri.getPath());
        }
        return file;
    }

    public static Bitmap getBitmapFromUri(Uri conentUri) {
        Bitmap bitmap = null;
        File file = getFileFromUri(conentUri);
        try {
            InputStream ims = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(ims);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getBitmapByContentResolver(Context context, Uri contentUri) {
        Bitmap bitmap = null;
        if (contentUri != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        context.getContentResolver(), contentUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static File getAppImageDir(){
        File myFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + AppConstants.DIR_APP
                + AppConstants.DIR_APP_IMAGES);
        if (!myFolder.exists()) {
            myFolder.mkdirs();
        }
        return myFolder;
    }

    public static File saveImage(Context mContext, Bitmap bitmap) {
        String imagePath = null;
        File imageFile = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);

        File myFolder = getAppImageDir();

        try {
            imageFile = new File(myFolder, Calendar.getInstance().getTimeInMillis() + ".PNG");
            imageFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());

            MediaScannerConnection.scanFile(mContext, new String[]{imageFile.getPath()},
                    new String[]{"image/*"}, null);

            fileOutputStream.close();
            Toast.makeText(mContext,
                    String.format("%s, %s", mContext.getResources().getString(R.string.toast_msg_file_saved_to), imageFile.getAbsolutePath()),
                    Toast.LENGTH_SHORT).show();
            imagePath = imageFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }
}
