package com.example.dipto.postaimagetoserver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    Button UploadImage, ChooseImage ;
    EditText ImageName ;
    ImageView UploadImageView ;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap ;
    String image_name, uploaded_image ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initilization();

        ChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFile();
            }
        });

        UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_name = ImageName.getText().toString() ;
                uploaded_image = getStringImage(bitmap) ;

                Log.d("UPLOAD IMAGE ::::::::", String.valueOf(uploaded_image.length())) ;
                ApiTaskUploadImage ApiCall = new ApiTaskUploadImage(MainActivity.this) ;
                ApiCall.execute(uploaded_image, image_name) ;
            }
        });
    }

    private void initilization(){
        UploadImage = (Button) findViewById(R.id.uploadimage_btn);
        ChooseImage = (Button) findViewById(R.id.chooseimage_btn);
        ImageName = (EditText) findViewById(R.id.imagename) ;
        UploadImageView = (ImageView) findViewById(R.id.uploadimage) ;
    }

    private void chooseImageFile(){
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

    private String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() ;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream) ;
        byte[] imagebyte = byteArrayOutputStream.toByteArray() ;
        String encodeString = Base64.encodeToString(imagebyte, Base64.DEFAULT) ;
        return encodeString ;
    }

    class ApiTaskUploadImage extends AsyncTask<String, Void, String>{

        Context context ;
        ProgressDialog progressDialog ;
        String upload_image_url = "http://programmerimtiaz.000webhostapp.com/PhotoUploadWithText/upload.php" ;

        ApiTaskUploadImage(Context context){
            this.context = context ;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context) ;
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Image Uploading...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder stringBuilder = null;

            try {
                URL url = new URL(upload_image_url) ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream() ;
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8")) ;

                String image = params[0];
                String name = params[1];
                String data = URLEncoder.encode("image", "UTF-8")+"="+URLEncoder.encode(image, "UTF-8")+"&"+
                        URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8") ;

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();


                int responsecode = httpURLConnection.getResponseCode() ;
                if(responsecode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = httpURLConnection.getInputStream() ;
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")) ;
                    stringBuilder = new StringBuilder() ;
                    String line = "" ;
                    while((line = bufferedReader.readLine()) != null){
                        stringBuilder.append(line + "\n") ;
                    }
                }
                httpURLConnection.disconnect();

            }
            catch (MalformedURLException e) {
                Log.d("++++Malformed+++++", String.valueOf(e)) ;
            }
            catch (IOException e) {
                Log.d("++++IOException+++++", String.valueOf(e)) ;
            }

            return stringBuilder.toString().trim();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String json) {

            try {
                progressDialog.dismiss();
                JSONObject mainJSONobj = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1)) ;
                Log.d("mainJSONobj=====", String.valueOf(mainJSONobj)) ;

                String response = mainJSONobj.getString("response") ;
                Log.d("RESPONSE=====", response) ;

                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT) ;
            }
            catch (JSONException e) {
                Log.d("++++JSONException+++++", String.valueOf(e)) ;
            }

        }
    }
}
