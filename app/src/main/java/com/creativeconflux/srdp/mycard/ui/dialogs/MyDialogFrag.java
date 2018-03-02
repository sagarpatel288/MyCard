package com.creativeconflux.srdp.mycard.ui.dialogs;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creativeconflux.srdp.mycard.BuildConfig;
import com.creativeconflux.srdp.mycard.R;
import com.creativeconflux.srdp.mycard.listeners.OnSelectImageListener;
import com.creativeconflux.srdp.mycard.utils.AppConstants;
import com.creativeconflux.srdp.mycard.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by srdpatel on 2/25/2018.
 */

public class MyDialogFrag extends DialogFragment implements View.OnClickListener {

    //region Compile time UI Variables
    TextView tvGallery;
    TextView tvCamera;
    TextView tvCancel;
    //endregion

    //region Listeners
    OnSelectImageListener onSelectImageListener;
    //endregion

    //region Class Variables
    private String imagePath;
    //endregion

    public OnSelectImageListener getOnSelectImageListener() {
        return onSelectImageListener;
    }

    public void setOnSelectImageListener(OnSelectImageListener onSelectImageListener) {
        this.onSelectImageListener = onSelectImageListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check and go ahead only if result is success
        if (resultCode == RESULT_OK) {
            //Check request match
            if (requestCode == AppConstants.REQUEST_CAMERA) {
                //Get data from returned intent
                if (data != null) {

                    if (data.getExtras() != null) {
                        Bundle extras = data.getExtras();
                        Bitmap bitmap = null;

                        //Check if returned intent has data
                        if (extras != null) {
                            /*This will give thumbnail and not the full size bitmap.
                            * To get the full size bitmap, use file provider and its absolute path
                            * */
                            bitmap = (Bitmap) extras.get("data");
                        }
                        //Check and go ahead only if returned intent has bitmap data
                        if (bitmap != null) {
                            //You have got bitmap thumbnail to use directly.
                            galleryAddPic(imagePath);
                            sendImagePath(imagePath);
                        }
                    }
                    //Intent data will be null if we have set EXTRA_OUTPUT with pre-defined uri to use.
                } else {
                    if (imagePath != null && onSelectImageListener != null) {
                        onSelectImageListener.onSelectImage(imagePath);
                    }
                }
            } else if (requestCode == AppConstants.REQUEST_GALLERY) {
                if (data != null) {
                    Uri contentUri = data.getData();
//                    imagePath = getImagePathFromUri(contentUri);
//                    sendImagePath(imagePath);
//                    Bitmap bitmap = getBitmapFromUri(contentUri);
                    Bitmap bitmap = FileUtils.getBitmapByContentResolver(getActivity(), contentUri);
                    imagePath = FileUtils.saveImage(getActivity(), bitmap).getAbsolutePath();
                    sendImagePath(imagePath);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_image_selection, container);
        tvGallery = view.findViewById(R.id.tv_from_gallery);
        tvCamera = view.findViewById(R.id.tv_by_camera);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvGallery.setOnClickListener(this);
        tvCamera.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        return view;
    }

    private void galleryAddPic(String imagePath) {
        /*// ScanFile so it will be appeared on Gallery
        MediaScannerConnection.scanFile(getActivity(),
                new String[]{imageUri.getPath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });*/
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private void sendImagePath(String imagePath) {
        if (onSelectImageListener != null) {
            onSelectImageListener.onSelectImage(imagePath);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_from_gallery:
                openGallery();
                break;

            case R.id.tv_by_camera:
                openCamera();
                break;

            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    /**
     * Opens gallery by setting up action on intent to open gallery
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (resolveActivity(intent)) {
            startActivityForResult(intent, AppConstants.REQUEST_GALLERY);
        }
    }

    /**
     * Sets Intent for camera action and Creates file that will store image captured by camera
     * <p>
     * By setting up explicit uri and EXTRA_OUTPUT, return intent will be null but then we
     * will have pre-defined path to use full size photo
     * However, if we want to use only thumbnail, then do not set uri and EXTRA_OUTPUT on intent.
     * Instead,
     * <p>
     * Use {@link #sendImagePath(String)} to send pre-defined file path to play with content of this intent data
     * see
     *
     * @throws IOException while creating pre-defined file path
     * @see <a href="https://developer.android.com/training/camera/photobasics.html#TaskPhotoView">https://developer.android.com/training/camera/photobasics.html#TaskPhotoView</a>
     * @since 1.0
     */
    private void openCamera() {
        //An intent having action set for camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Check if there is app or activity that can handle this action
        if (resolveActivity(intent)) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);

                /*By setting up explicit uri and EXTRA_OUTPUT, return intent will be null but then we
                * will have pre-defined path to use*/
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, AppConstants.REQUEST_CAMERA);
            }
        }
    }

    /**
     * Checks if there is any app or activity that can perform action set in Intent
     */
    private boolean resolveActivity(Intent intent) {
        return intent.resolveActivity(getActivity().getPackageManager()) != null;
    }

    /**
     * Will be used by camera after taking the picture to store the image
     * with Unique Image name in pre-defined Image file (folder) here
     * so that it can be used later easily by file provider.
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";

        // Not available for other apps
        //  File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        //Publicly available for all apps
        File storageDir = FileUtils.getAppImageDir();

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        if (!image.exists()) {
            image.mkdirs();
        }
        // Save a file: path for use with ACTION_VIEW intents
        imagePath = "file:" + image.getAbsolutePath();
        return image;
    }
}
