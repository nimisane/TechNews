package com.example.nimish.technews;

public class Bookmark_Items {
    private String author;
    private String headlines;
    private String news_url;
    private String image_url;
    private String date_time;
    private int bk_id;

    public Bookmark_Items(String author, String headlines, String news_url, String image_url, String date_time,int bk_id) {
        this.author = author;
        this.headlines = headlines;
        this.news_url = news_url;
        this.image_url = image_url;
        this.date_time = date_time;
        this.bk_id = bk_id;
    }

    public int getBk_id() {
        return bk_id;
    }

    public String getAuthor() {
        return author;
    }

    public String getHeadlines() {
        return headlines;
    }

    public String getNews_url() {
        return news_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getDate_time() {
        return date_time;
    }
}
