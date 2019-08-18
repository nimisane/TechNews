package com.example.nimish.technews;

public class User_News_Items {
    private String user_imageurl;
    private String user_news_headlines;
    private String user_news_content;
    private String user_news_time;
    private String userid;
    private int news_id;
    private int report_news;

    public User_News_Items(String user_imageurl, String user_news_headlines, String user_news_content, String user_news_time, String userid,int news_id,int report_news) {
        this.user_imageurl = user_imageurl;
        this.user_news_headlines = user_news_headlines;
        this.user_news_content = user_news_content;
        this.user_news_time = user_news_time;
        this.userid = userid;
        this.news_id = news_id;
        this.report_news = report_news;
    }

    public int getReport_news() {
        return report_news;
    }

    public int getNews_id() {
        return news_id;
    }

    public String getUser_imageurl() {
        return user_imageurl;
    }

    public String getUser_news_headlines() {
        return user_news_headlines;
    }

    public String getUser_news_content() {
        return user_news_content;
    }

    public String getUser_news_time() {
        return user_news_time;
    }

    public String getUserid() {
        return userid;
    }
}
