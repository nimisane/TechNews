package com.example.nimish.technews;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class Daily_news_complete_article extends AppCompatActivity{

    WebView complete_article;
    ProgressBar complete_article_progress;
    String author;
    String news_headlines;
    String news_url;
    String image_url;
    String date_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_news_complete_article_layout);

        Intent i = getIntent();
        author = i.getStringExtra("author");
        news_url = i.getStringExtra("news_url");
        news_headlines = i.getStringExtra("headlines");
        image_url = i.getStringExtra("image");
        date_time = i.getStringExtra("date_time");
        complete_article = findViewById(R.id.daily_news_complete_article_webview);
        complete_article_progress = findViewById(R.id.daily_news_complete_article_progress);

        complete_article.loadUrl(news_url);
        complete_article.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    complete_article_progress.setVisibility(View.GONE);
                } else {
                    complete_article_progress.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_news_feed,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem items)
    {
        switch (items.getItemId())
        {
            case R.id.daily_news_share:
                share_news();
                return true;
            case R.id.daily_news_bookmark:
                if(SharedPreferenceManager.getInstance(this).getUserName().equals("Guest")){
                    Toast.makeText(this,"Login to Bookmark this Article",Toast.LENGTH_SHORT).show();
                }
                else {
                    bookmark_news();
                }
        }

        return super.onOptionsItemSelected(items);
    }

    public void share_news()   //share option menu
    {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT,news_headlines);
        share.putExtra(Intent.EXTRA_TEXT,news_url);
        startActivity(Intent.createChooser(share,"Share via"));
    }

    public void bookmark_news(){
        final String username = SharedPreferenceManager.getInstance(this).getUserName();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.bookmark, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(Daily_news_complete_article.this,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Log.e(TAG, "DailyCompleteError", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(Daily_news_complete_article.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e(TAG, "DailyNewsCompleteVolleyError", error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userid",username);
                params.put("author",author);
                params.put("headlines",news_headlines);
                params.put("news_url",news_url);
                params.put("image_url",image_url);
                params.put("date_time",date_time);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
