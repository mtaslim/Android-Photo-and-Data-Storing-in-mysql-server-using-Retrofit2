package com.ityadi.app.tourmate.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ityadi.app.tourmate.Common.CurrentTime;
import com.ityadi.app.tourmate.Common.Network;
import com.ityadi.app.tourmate.Common.UserApi;
import com.ityadi.app.tourmate.Common.UserApiWithoutPhoto;
import com.ityadi.app.tourmate.PhotoLibrary.PhotoLibrary;
import com.ityadi.app.tourmate.R;
import com.ityadi.app.tourmate.Response.UserResponse;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    String name,username,password,email;
    EditText editTextName,editTextUsername,editTextPassword,editTextEmail;
    ImageView photoView;

    String timeStamp,realPath="";

    public Uri fileUri;

    CurrentTime currentTime = new CurrentTime();
    Call<UserResponse> call;
    PhotoLibrary photoLibrary = new PhotoLibrary();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeStamp = new SimpleDateFormat("yyyMMdd_HHmm", Locale.ENGLISH).format(new Date());
        currentTime.setTimestamp(timeStamp.toString());

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);

        inputData();
    }
    public void inputData(){
        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextUsername = (EditText)findViewById(R.id.editTextUsername);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        photoView = (ImageView) findViewById(R.id.photoView);
    }

    public void selectImage(View view) {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, photoLibrary.RESULT_LOAD_IMAGE);
    }

    public void captureImage(View view) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(photoLibrary.getOutputPhotoFile());
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(i, photoLibrary.RESULT_CAPTURE_IMAGE );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == photoLibrary.RESULT_LOAD_IMAGE) {
                if(data != null) {
                    Uri selectedImage = data.getData();
                    Bitmap bmp = null;
                    try {
                        bmp = photoLibrary.getBitmapFromUri(getBaseContext(),selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photoView.setImageBitmap(bmp);
                    Uri uri = photoLibrary.getImageUri(getBaseContext(), bmp);
                    realPath =  photoLibrary.getRealPathFromURI(getBaseContext(),uri);
                } // if(data != null)
            }
            else if (requestCode == photoLibrary.RESULT_CAPTURE_IMAGE){
                realPath = String.valueOf(photoLibrary.getOutputPhotoFile());
                File file = new File(realPath);
                if(file.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    photoView.setImageBitmap(myBitmap);
                }
            }
        }
    }

    public void addUser(View view) {
        name = editTextName.getText().toString();
        username = editTextUsername.getText().toString();
        password = editTextPassword.getText().toString();
        email = editTextEmail.getText().toString();

        String req="";
        if("".equals(name)) req+= "Name,";
        if("".equals(username)) req+= "Username,";
        if("".equals(password)) req+= "Password,";
        if("".equals(email)) req+= "Email,";
        if(req.length()>0) {
            req = req.substring(0,req.length()-1);
            Toast.makeText(getBaseContext(),req+" is required", Toast.LENGTH_LONG).show();
        }
        else{
            final ProgressDialog progressDialog;
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait. Your data is storing...");
            progressDialog.show();


            if(realPath!=""){
                UserApi userApi = Network.createService(UserApi.class);
                File file = new File(realPath);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body =  MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);
                call = userApi.getAccessToken(name,username,password,email,body);
            }
            else{
                UserApiWithoutPhoto userApiWithoutPhoto = Network.createService(UserApiWithoutPhoto.class);
                call = userApiWithoutPhoto.getAccessToken(name,username,password,email);
            }


            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    progressDialog.dismiss();
                    UserResponse userResponse=response.body();

                    String msg = userResponse.getMsg();
                    String err = userResponse.getErr();
                    Toast.makeText(getBaseContext(),msg+err,Toast.LENGTH_LONG).show();

                    //if(!"".equals(msg)) check for null or empty
                    Log.e("response", userResponse.getMsg());
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Log.e("error", t.toString());
                }
            });
        }


    }














}
