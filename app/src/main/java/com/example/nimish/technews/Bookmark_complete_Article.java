package com.example.nimish.technews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class Bookmark_complete_Article extends AppCompatActivity {

    String author;
    String headlines;
    String news_url;
    String image_url;
    String date_time;
    int bid;
    WebView bk_news;
    ProgressBar bk_progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_complete__article);

        Intent i = getIntent();
        author = i.getStringExtra("author");
        headlines = i.getStringExtra("headlines");
        news_url = i.getStringExtra("news_url");
        image_url = i.getStringExtra("image");
        date_time = i.getStringExtra("date");
        bid = i.getIntExtra("bid",0);

        bk_news = findViewById(R.id.bookmark_complete_article_webview);
        bk_progress = findViewById(R.id.bookmark_news_complete_article_progress);

        bk_news.getSettings().setBuiltInZoomControls(true);
        bk_news.getSettings().setDisplayZoomControls(true);
        bk_news.loadUrl(news_url);
        bk_news.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress){
                if (progress == 100) {
                    bk_progress.setVisibility(View.GONE);
                } else {
                    bk_progress.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookmark_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem items)
    {
        switch (items.getItemId())
        {
            case R.id.daily_news_share:
                share_news();
                return true;
        }

        return super.onOptionsItemSelected(items);
    }

    public void share_news()   //share option menu
    {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT,headlines);
        share.putExtra(Intent.EXTRA_TEXT,news_url);
        startActivity(Intent.createChooser(share,"Share via"));
    }


}
