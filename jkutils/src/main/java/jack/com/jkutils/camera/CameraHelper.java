package jack.com.jkutils.camera;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.PermissionChecker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jack.com.jkutils.JKUtils;
import jack.com.jkutils.R;

public class CameraHelper {

    /**
     * Take Picture
     * */
    public static File startCamera(Activity context, int requestCode) {

        File photoFile = null;
        if (context == null) {
            return photoFile;
        }

        boolean cameraPermission = PermissionChecker.checkSelfPermission(context, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED;
        boolean storageReadPermission = PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED;
        boolean storageWritePermission = PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED;

        if (!cameraPermission || !storageReadPermission || !storageWritePermission) {

            Toast.makeText(context,context.getString(R.string.jk_allow_camera_and_storage), Toast.LENGTH_LONG).show();

            return photoFile;
        }


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null)
        {

            try
            {
                photoFile = createImageFile(context);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null)
            {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageContentUri(context,photoFile));
                context.startActivityForResult(takePictureIntent, requestCode);
                return photoFile;
            }
        }
        return photoFile;
    }

    private static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String appName = JKUtils.getApplicationName(context);

        File storageDir = new File(Environment.getExternalStorageDirectory().getPath() + "/" + appName + "/photo/temp/");

        File dir1 = new File(Environment.getExternalStorageDirectory().getPath() + "/" + appName);
        File dir2 = new File(Environment.getExternalStorageDirectory().getPath() + "/" + appName + "/photo");
        File dir3 = new File(Environment.getExternalStorageDirectory().getPath() + "/" + appName + "/photo/temp/");


        if (!dir1.exists())
        {
            boolean b = dir1.mkdir();
        }
        if (!dir2.exists())
        {
            boolean b = dir2.mkdir();
        }
        if (!dir3.exists())
        {
            boolean b = dir3.mkdir();
        }


        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );

        return image;
    }

    private static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Photo Library
     * */
    public static void pickImageFromAlbum(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    public static void pickImageFromAlbum2(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    public static Uri getImageUriFromData(Intent data) {
        if (data != null) {
            Uri imageUri = data.getData();
            return imageUri;
        }
        return null;
    }

}
