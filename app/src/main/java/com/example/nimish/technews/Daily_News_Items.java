package com.example.nimish.technews;

public class Daily_News_Items {

    private String imageurl;
    private String news_headlines;
    private String news_reporter;
    private String news_time;
    private String complete_news_url;

    public Daily_News_Items(String imageurl, String news_headlines, String news_reporter, String news_time,String complete_news_url) {
        this.imageurl = imageurl;
        this.news_headlines = news_headlines;
        this.news_reporter = news_reporter;
        this.news_time = news_time;
        this.complete_news_url = complete_news_url;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getNews_headlines() {
        return news_headlines;
    }

    public String getNews_reporter() {
        return news_reporter;
    }

    public String getNews_time() {
        return news_time;
    }

    public String getComplete_news_url() {
        return complete_news_url;
    }
}
