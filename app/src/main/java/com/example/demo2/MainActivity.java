package com.example.demo2;
import android.app.AlertDialog;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v13.view.inputmethod.EditorInfoCompat;
import android.support.v13.view.inputmethod.InputConnectionCompat;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.os.BuildCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.AppCompatEditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity{

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final int MY_PERMISSIONS_REQUEST_EXTORAGE= 101;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView myImageView;
    private Button start;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myImageView=findViewById(R.id.myImageView);
        start=findViewById(R.id.start);

        start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
                else
                {
                    openCamera();
                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (data != null && data.getExtras() != null) {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                myImageView.setImageBitmap(imageBitmap);
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(MainActivity.this, "Enable the Storage to save files",Toast.LENGTH_LONG);

                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_EXTORAGE);

                }
                else
                {
                    SaveImage(imageBitmap);
                }


            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_CAMERA:
            {
                    for (int i = 0, len = permissions.length; i < len; i++)
                    {
                        String permission = permissions[i];

                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        Toast.makeText(this, "Access Denied",Toast.LENGTH_LONG);
                    }

                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        {
                            openCamera();
                        }
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
            case MY_PERMISSIONS_REQUEST_EXTORAGE:
            {
                for (int i = 0, len = permissions.length; i < len; i++)
                {
                    String permission = permissions[i];

                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        Toast.makeText(this, "Storage Denied",Toast.LENGTH_LONG);
                    }

                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    {
                       Toast.makeText(this, "Storage Enabled",Toast.LENGTH_LONG);
                       SaveImage(imageBitmap);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    private void openCamera() {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
    }



    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();

        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
            //     Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
// Tell the media scanner about the new file so that it is
// immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

}
