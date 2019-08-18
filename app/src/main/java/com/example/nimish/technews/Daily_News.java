package com.example.nimish.technews;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;

public class Daily_News extends Fragment implements Daily_News_Adapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    //recycler_view_daily_news

    private RecyclerView daily_news_recycler_view;  //daily_news
    private ArrayList<Daily_News_Items> daily_news_items;   //daily_news
    private Daily_News_Adapter daily_news_adapter;  //daily_news
    private RequestQueue daily_news_request_queue;  //daily_news
    String API_KEY = "68dfee37255240398ddf12ead920334a";
    String NEWS_SOURCE = "TechCrunch";
    String NEWS_SOURCE_WIRED = "wired";
    String daily_news_url;
    TextView no_internet_connection;
    Button refresh;
   // ProgressBar progress;
    private SwipeRefreshLayout daily_news_refresh;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.daily_news_layout, container, false);

        //recycler_view_daily_news
        daily_news_refresh = (SwipeRefreshLayout)rootView.findViewById(R.id.daily_news_refresh);
        daily_news_recycler_view=(RecyclerView)rootView.findViewById(R.id.daily_news_recyclerview);

        daily_news_recycler_view.setHasFixedSize(true);
        daily_news_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        daily_news_items = new ArrayList<>();
        daily_news_request_queue = Volley.newRequestQueue(getContext());
        no_internet_connection = (TextView)rootView.findViewById(R.id.daily_news_no_internet);
        refresh = (Button)rootView.findViewById(R.id.daily_news_refresh_button);
        checkConnection();
        daily_news_refresh.setOnRefreshListener(this);

        daily_news_refresh.post(new Runnable() { //swipedownlayout
            @Override
            public void run() {
                daily_news_parsejson();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() { //refresh button
            @Override
            public void onClick(View v) {
                daily_news_parsejson();
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected boolean online()
    {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info != null && info.isConnectedOrConnecting())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void checkConnection()
    {
        if(online())
        {
            visibilityGone();
        }
        else
        {
            visible();
        }
    }

    public void visibilityGone()
    {
        no_internet_connection.setVisibility(View.GONE);
        refresh.setVisibility(View.GONE);
    }

    public void visible()
    {
        refresh.setVisibility(View.VISIBLE);
        no_internet_connection.setVisibility(View.VISIBLE);
    }

    //recycler_view_daily_news
    public void daily_news_parsejson()
    {
        daily_news_refresh.setRefreshing(true);
        String ind_url="https://newsapi.org/v2/top-headlines?sources=wired&apiKey=68dfee37255240398ddf12ead920334a";
        String url="https://newsapi.org/v1/articles?source=" + NEWS_SOURCE + "&sortBy=top&apiKey=" + API_KEY;
        JsonObjectRequest daily_news_json_request = new JsonObjectRequest(Request.Method.GET,url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray daily_news_JSONArray = response.getJSONArray("articles"); //News api JSONarray name
                    daily_news_items.clear();
                    for(int i=0;i<daily_news_JSONArray.length();i++)
                    {
                        JSONObject daily_news_json_object = daily_news_JSONArray.getJSONObject(i);
                        String daily_news_author = daily_news_json_object.getString("author");
                        String daily_news_headlines = daily_news_json_object.getString("title");
                        daily_news_url = daily_news_json_object.getString("url");
                        String daily_news_image_url = daily_news_json_object.getString("urlToImage");
                        String daily_news_times = daily_news_json_object.getString("publishedAt");
                        daily_news_items.add(new Daily_News_Items(daily_news_image_url,daily_news_headlines,daily_news_author,daily_news_times,daily_news_url));
                        daily_news_adapter = new Daily_News_Adapter(getContext(),daily_news_items);
                        daily_news_recycler_view.setAdapter(daily_news_adapter);
                        daily_news_adapter.setOnItemClickListener(Daily_News.this);
                        if(daily_news_items.isEmpty())
                        {
                            daily_news_recycler_view.setVisibility(View.GONE);
                            visible();
                        }
                        else
                        {
                            daily_news_recycler_view.setVisibility(View.VISIBLE);
                            visibilityGone();
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "DailyNewsError", e);
                }
                daily_news_refresh.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error.printStackTrace();
                Log.e(TAG, "DailyNewsVolleyError", error);
                daily_news_refresh.setRefreshing(false);
            }
        });
        daily_news_request_queue.add(daily_news_json_request);
    }

    @Override
    public void onItemClick(int position) {

        Intent complete_article = new Intent(getContext(),Daily_news_complete_article.class);
        Daily_News_Items complete_item_details = daily_news_items.get(position);
        complete_article.putExtra("author",complete_item_details.getNews_reporter());
        complete_article.putExtra("headlines",complete_item_details.getNews_headlines());
        complete_article.putExtra("news_url",complete_item_details.getComplete_news_url());
        complete_article.putExtra("image",complete_item_details.getImageurl());
        complete_article.putExtra("date_time",complete_item_details.getNews_time());
        startActivity(complete_article);
    }

    @Override
    public void onRefresh() {
        daily_news_parsejson();
        daily_news_refresh.setRefreshing(false);
    }
}
