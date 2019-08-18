package com.example.nimish.technews;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.android.volley.VolleyLog.TAG;

public class Add_User_News extends AppCompatActivity {

    ImageView add_news_image;
    EditText news_headlines;
    EditText news_content;
    Button add_user_news;
    ProgressBar pb;
    String userid;
    String upload_news_content;
    String upload_news_headlines;
    private Uri image_result_Uri ;//= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add__user__news);
        news_headlines = findViewById(R.id.add_news_healines);
        news_content = findViewById(R.id.add_news_content_et);
        add_news_image = findViewById(R.id.add_news_image);
        add_user_news = findViewById(R.id.add_news_post);
        pb = (ProgressBar)findViewById(R.id.add_news_progress_bar);
        pb.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        pb.setVisibility(View.GONE);

        add_news_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(ContextCompat.checkSelfPermission(Add_User_News.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(Add_User_News.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else {
                        getImage();
                    }

                }
                else {
                    getImage();
                }
            }
        });

        add_user_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userid = SharedPreferenceManager.getInstance(getApplicationContext()).getUserName();
                upload_news_headlines = news_headlines.getText().toString().trim();
                upload_news_content = news_content.getText().toString().trim();
                if((upload_news_content.isEmpty()) || (upload_news_headlines.isEmpty())){
                    Toast.makeText(getApplicationContext(),"Enter all the details",Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        pb.setVisibility(View.VISIBLE);
                        uploadDetails();
                        Intent i = new Intent(getApplicationContext(),News_feed.class);
                        startActivity(i);
                    }
                    catch (Exception e){
                        pb.setVisibility(View.VISIBLE);
                        regiterUser();
                        Intent i = new Intent(getApplicationContext(),News_feed.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(),"Data Uploaded",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                image_result_Uri = result.getUri();
                add_news_image.setImageURI(image_result_Uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG, "AddNewsCameraError", error);
            }
        }
    }

    public void getImage()
    {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Add_User_News.this);
    }

    public void uploadDetails() {
        String path = FilePath.getPath(this, image_result_Uri);
        if (path == null) {
            Toast.makeText(this, "Retry", Toast.LENGTH_LONG).show();
        } else {
            try {
                String uploadId = UUID.randomUUID().toString();

                new MultipartUploadRequest(this, uploadId, Constants.add_news_url)
                        .addParameter("userid", userid)
                        .addParameter("news_headlines", upload_news_headlines)
                        .addParameter("news_content", upload_news_content)
                        .addFileToUpload(path, "image")
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload();
                Toast.makeText(this, "uploading", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "MultipartUploadError", e);
            }
        }
    }

    public void regiterUser(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.add_news_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                    //pb.setVisibility(View.GONE);
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Log.e(TAG, "addnewsError", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userid",userid);
                params.put("news_headlines",upload_news_headlines);
                params.put("news_content",upload_news_content);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
