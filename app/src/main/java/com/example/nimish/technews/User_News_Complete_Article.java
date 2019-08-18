package com.example.nimish.technews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;
import static com.example.nimish.technews.User_News.DATE;
import static com.example.nimish.technews.User_News.EXTRA_CONTENT;
import static com.example.nimish.technews.User_News.EXTRA_HEADLINES;
import static com.example.nimish.technews.User_News.EXTRA_URL;
import static com.example.nimish.technews.User_News.EXTRA_USERID;

public class User_News_Complete_Article extends AppCompatActivity {

    ImageView user_image;
    TextView Uheadlines;
    TextView news_content;
    TextView news_user;
    TextView date_time;
    String userid;
    int news_id;
    int i=0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_news_complete_article);

        user_image = findViewById(R.id.user_image_id);
        Uheadlines = findViewById(R.id.user_headlines_id);
        news_content = findViewById(R.id.user_content_id);
        news_user = findViewById(R.id.userid_id);
        date_time = findViewById(R.id.date_id);

        Intent i = getIntent();
        String imgurl = i.getStringExtra(EXTRA_URL);
        String headlines = i.getStringExtra(EXTRA_HEADLINES);
        String cotent = i.getStringExtra(EXTRA_CONTENT);
        userid = i.getStringExtra(EXTRA_USERID);
        String date = i.getStringExtra(DATE);
        news_id = i.getIntExtra("news_id",0);

        Glide.with(this).load(imgurl).into(user_image);
        Uheadlines.setText(headlines);
        news_content.setText(cotent);
        news_user.setText("@"+userid);
        date_time.setText(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.report:
                if(!SharedPreferenceManager.getInstance(this).getUserName().equals("Guest")){
                    updateReportCount();
                }
                else {
                    Toast.makeText(User_News_Complete_Article.this,"Login to Report this article",Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void updateReportCount(){
        final String user_id=SharedPreferenceManager.getInstance(this).getUserName();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.report_news_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                   // e.printStackTrace();
                    Log.e(TAG, "UserNewsCompleteError", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e(TAG, "UserNewsCompleteVolleyError", error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userid",user_id);
                params.put("id",String.valueOf(news_id));
                return params;
            }
        };
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
