package com.example.dipto.postaimagetoserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Button UploadImage, ChooseImage ;
    EditText ImageName ;
    ImageView UploadImageView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initilization();
    }

    private void initilization(){
        UploadImage = (Button) findViewById(R.id.uploadimage_btn);
        ChooseImage = (Button) findViewById(R.id.chooseimage_btn);
        ImageName = (EditText) findViewById(R.id.imagename) ;
        UploadImageView = (ImageView) findViewById(R.id.uploadimage) ;
    }
}
