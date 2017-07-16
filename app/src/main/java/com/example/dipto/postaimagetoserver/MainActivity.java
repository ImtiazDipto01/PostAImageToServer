package com.example.dipto.postaimagetoserver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button UploadImage, ChooseImage ;
    EditText ImageName ;
    ImageView UploadImageView ;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initilization();

        ChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseimagefile();
            }
        });
    }

    private void initilization(){
        UploadImage = (Button) findViewById(R.id.uploadimage_btn);
        ChooseImage = (Button) findViewById(R.id.chooseimage_btn);
        ImageName = (EditText) findViewById(R.id.imagename) ;
        UploadImageView = (ImageView) findViewById(R.id.uploadimage) ;
    }

    private void chooseimagefile(){
        Intent intent = new Intent() ;
        intent.setType("image/*") ;
        intent.setAction(Intent.ACTION_GET_CONTENT) ;
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri imagepath = data.getData() ;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagepath) ;
                UploadImageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
