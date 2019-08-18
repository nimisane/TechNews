package com.example.nimish.technews;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

public class User_News extends Fragment implements User_News_Adapter.OnItemClickListener {

    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_USERID = "userid";
    public static final String EXTRA_HEADLINES = "headlines";
    public static final String EXTRA_CONTENT = "content";
    public static final String DATE = "date_time";

    private RecyclerView user_news_recycler_view;
    private ArrayList<User_News_Items> user_news_items;
    private User_News_Adapter user_news_adapter;
    private RequestQueue user_news_request_queue;

    TextView no_internet_connection;
    Button refresh;
    TextView no_data;
    private SwipeRefreshLayout user_news_refresh;
    FloatingActionButton add_news;
    Intent i;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_news_layout, container, false);

        no_internet_connection=(TextView) rootView.findViewById(R.id.user_news_no_internet);
        no_data=(TextView) rootView.findViewById(R.id.no_data);
        no_data.setVisibility(View.GONE);
        refresh = (Button)rootView.findViewById(R.id.user_news_refresh_button);
        user_news_refresh=(SwipeRefreshLayout)rootView.findViewById(R.id.user_news_refresh);
        user_news_recycler_view=(RecyclerView)rootView.findViewById(R.id.user_news_recyclerview);
        checkConnection();
        user_news_recycler_view.setHasFixedSize(true);
        user_news_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        user_news_items = new ArrayList<>();

        user_news_request_queue = Volley.newRequestQueue(getContext());
        add_news=(FloatingActionButton) rootView.findViewById(R.id.add_news);

        if(SharedPreferenceManager.getInstance(getContext()).getUserName().equals("Guest")){
            add_news.setVisibility(View.GONE);
        }


        user_news_parsejson();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_news_parsejson();
            }
        });
        add_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               i=new Intent(getActivity(),Add_User_News.class);
               startActivity(i);
            }
        });

        user_news_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                user_news_parsejson();
                user_news_refresh.setRefreshing(false);
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

    public void user_news_parsejson(){
        user_news_refresh.setRefreshing(true);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.user_news_details, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("usernews");
                    user_news_items.clear();
                    for(int i=0;i<jsonArray.length();i++){

                        JSONObject user_news_json_obj = jsonArray.getJSONObject(i);

                        int news_id = user_news_json_obj.getInt("id");
                        String news_author = user_news_json_obj.getString("userid");
                        String news_headlines = user_news_json_obj.getString("news_headlines");
                        String news_content = user_news_json_obj.getString("news_content");
                        String news_image_url = user_news_json_obj.getString("image");
                        String news_date = user_news_json_obj.getString("date");
                        int report = user_news_json_obj.getInt("report");

                        user_news_items.add(new User_News_Items(news_image_url,news_headlines,news_content,news_date,news_author,news_id,report));

                    }
                    user_news_adapter = new User_News_Adapter(getContext(),user_news_items);
                    user_news_recycler_view.setAdapter(user_news_adapter);

                    user_news_adapter.setOnItemClickListener(User_News.this);
                    if(user_news_items.isEmpty())
                    {
                        user_news_recycler_view.setVisibility(View.GONE);
                        no_data.setVisibility(View.VISIBLE);
                        //visible();
                    }
                    else
                    {
                        user_news_recycler_view.setVisibility(View.VISIBLE);
                        visibilityGone();
                        no_data.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                   // e.printStackTrace();
                    Log.e(TAG, "UserNewsError", e);
                }
                user_news_refresh.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error.printStackTrace();
                Log.e(TAG, "UserNewsVolleyError", error);
                user_news_refresh.setRefreshing(false);
            }
        });
        user_news_request_queue.add(request);
    }


    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(getContext(),User_News_Complete_Article.class);
        User_News_Items clickedItem = user_news_items.get(position);

        i.putExtra(EXTRA_URL,clickedItem.getUser_imageurl());
        i.putExtra(EXTRA_HEADLINES,clickedItem.getUser_news_headlines());
        i.putExtra(EXTRA_CONTENT,clickedItem.getUser_news_content());
        i.putExtra(EXTRA_USERID,clickedItem.getUserid());
        i.putExtra(DATE,clickedItem.getUser_news_time());
        i.putExtra("news_id",clickedItem.getNews_id());
        startActivity(i);
    }
}
