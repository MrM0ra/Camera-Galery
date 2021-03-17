package com.example.permissionappexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int PERMISSIONS_CALLBACK=11;
    public static final int CAMERA_CALLBACK=12;
    public static final int GALLERY_CALLBACK=13;

    private File file;

    private Button cameraBtn;
    private Button galleryBtn;
    private Button downloadBtn;
    private EditText urlField;

    private ImageView mainImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraBtn=findViewById(R.id.cameraBtn);
        downloadBtn=findViewById(R.id.downloadBtn);
        galleryBtn=findViewById(R.id.galleryBtn);
        urlField=findViewById(R.id.urlField);
        mainImage=findViewById(R.id.mainImage);

        ActivityCompat.requestPermissions(this, new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSIONS_CALLBACK);

        cameraBtn.setOnClickListener(this);
        downloadBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==PERMISSIONS_CALLBACK){
            boolean allGranted=true;
            for (int i=0;i<grantResults.length;i++){
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    Log.e("", "Aqui entró: "+grantResults[i]);
                    allGranted=false;
                    break;
                }
            }
            if(allGranted){
                Toast.makeText(this, "Todos los permisos concedidos", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Alerta!, no todos los permisos fueron concedidos", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.cameraBtn:
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Null para poder que quede en el directorio raiz de la app
                file = new File(getExternalFilesDir(null) + "/photo.png");
                Log.e(">>>>>", "" + file);
                Uri uri = FileProvider.getUriForFile(this, getPackageName(), file);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, CAMERA_CALLBACK);
                break;

            case R.id.galleryBtn:
                Intent j = new Intent(Intent.ACTION_GET_CONTENT);
                // Para video sería j.setType("video/*");
                j.setType("image/*");
                startActivityForResult(j, GALLERY_CALLBACK);
                break;

            case R.id.downloadBtn:
                String url = urlField.getText().toString();
                Glide.with(this).load(url).fitCenter().into(mainImage);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_CALLBACK && resultCode==RESULT_OK){
            Bitmap image = BitmapFactory.decodeFile(file.getPath());
            Bitmap thumbnail = Bitmap.createScaledBitmap(image, image.getWidth()/4, image.getHeight()/4, true);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedImg = Bitmap.createBitmap(thumbnail, 0,0, thumbnail.getWidth(),thumbnail.getHeight(), matrix, true);
            mainImage.setImageBitmap(thumbnail);
        }else if(requestCode==GALLERY_CALLBACK && resultCode==RESULT_OK){
            Uri uri = data.getData();
            Log.e(">>>>", uri+"");
            String path = UtilDomi.getPath(this, uri);
            Log.e(">>>>", path+"");
            Bitmap image = BitmapFactory.decodeFile(path);
            mainImage.setImageBitmap(image);
        }
    }
}