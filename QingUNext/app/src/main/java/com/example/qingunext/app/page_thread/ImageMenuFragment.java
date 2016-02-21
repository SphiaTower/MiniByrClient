package com.example.qingunext.app.page_thread;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Voyager on 9/23/2015.
 */
public class ImageMenuFragment extends DialogFragment {
    public ImageProvider mImageProvider;
    String mCurrentPhotoPath;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mImageProvider = ((ImageProvider) activity);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("choose what to do")
                .setItems(new String[]{"save", "send", "other"}, (dialog, which) -> {
                    String text;
                    switch (which) {
                        case 0:
                            text = "save";
                            File file = null;
                            try {
                                file = createImageFile();
                                Bitmap bitmap = mImageProvider.getBitmap();
                                FileOutputStream os = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                                os.close();
                                MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "failed to save the image", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            text = "send";
                            break;
                        case 2:
                            text = "other";
                            break;
                        default:
                            throw new IndexOutOfBoundsException();

                    }
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                });
        return builder.create();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    public interface ImageProvider {
        Bitmap getBitmap();
    }
}
