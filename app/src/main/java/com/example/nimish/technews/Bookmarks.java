package com.example.nimish.technews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class Bookmarks extends Fragment implements Bookmark_Adapter.OnItemClickListener {

    private RecyclerView bk_recycler;
    ArrayList<Bookmark_Items> bk_array_list;
    Bookmark_Adapter bk_adapter;
    private RequestQueue bk_request_queue;
    int bid;

    CardView user_card;
    TextView user_logout;
    TextView ulogout;
    TextView no_bookmark;
    TextView guest_bookmark;
    SwipeRefreshLayout bk_refresh;
    ProgressBar pb;
    Intent i;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bookmark_layout, container, false);
        user_card=(CardView)rootView.findViewById(R.id.user_card);
        ulogout=(TextView)rootView.findViewById(R.id.logout);
        user_logout=(TextView)rootView.findViewById(R.id.userid_logout);
        bk_refresh=(SwipeRefreshLayout)rootView.findViewById(R.id.bookmark_refresh);
        no_bookmark=(TextView)rootView.findViewById(R.id.nobookmark);
        guest_bookmark=(TextView)rootView.findViewById(R.id.guestbookmark);
        pb=(ProgressBar)rootView.findViewById(R.id.logout_progress);
        pb.setVisibility(View.GONE);
        if(SharedPreferenceManager.getInstance(getContext()).getUserName().equals("Guest")){
            guest_bookmark.setVisibility(View.VISIBLE);
        }
        else {
            guest_bookmark.setVisibility(View.GONE);
        }
        bk_recycler = (RecyclerView)rootView.findViewById(R.id.bookmark_recycler);
        bk_recycler.setHasFixedSize(true);
        bk_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        bk_array_list = new ArrayList<>();
        bk_request_queue = Volley.newRequestQueue(getContext());

        user_logout.setText(SharedPreferenceManager.getInstance(getContext()).getUserName());

        ulogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                SharedPreferenceManager.getInstance(getContext()).logout();
                i=new Intent(getContext(),MainActivity.class);
                startActivity(i);
            }
        });

        if (SharedPreferenceManager.getInstance(getContext()).getUserName().equals("Guest")){
            ulogout.setText("Login");
            user_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    i=new Intent(getContext(),MainActivity.class);
                    startActivity(i);
                }
            });
        }

        bookmark();
        no_bookmark();
        bk_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bookmark();
                bk_refresh.setRefreshing(false);
            }
        });

        return rootView;
    }

    private void bookmark(){
        final String username = SharedPreferenceManager.getInstance(getContext()).getUserName();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.user_bookmark, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    bk_array_list.clear();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String image = jsonObject.getString("image_url");
                        String author = jsonObject.getString("author");
                        String headlines = jsonObject.getString("headlines");
                        String news_url = jsonObject.getString("news_url");
                        String date_time = jsonObject.getString("date_time");
                        bid = jsonObject.getInt("b_id");

                        Bookmark_Items bookmarks = new Bookmark_Items(author,headlines,news_url,image,date_time,bid);
                        bk_array_list.add(bookmarks);
                    }
                    bk_adapter = new Bookmark_Adapter(getContext(),bk_array_list);
                    bk_recycler.setAdapter(bk_adapter);
                    bk_adapter.setOnItemClickListener(Bookmarks.this);
                    if(bk_array_list.isEmpty())
                    {
                        bk_recycler.setVisibility(View.GONE);
                        no_bookmark.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        bk_recycler.setVisibility(View.VISIBLE);
                        no_bookmark.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                   // e.printStackTrace();
                    Log.e(TAG, "BookmarkError", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userid",username);
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    public void removeItem(int position){
        remove_bookmark(position);
        bk_array_list.remove(position);
        bk_adapter.notifyItemRemoved(position);
    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(getContext(),Bookmark_complete_Article.class);
        Bookmark_Items clickedItem = bk_array_list.get(position);
        i.putExtra("author",clickedItem.getAuthor());
        i.putExtra("headlines",clickedItem.getHeadlines());
        i.putExtra("news_url",clickedItem.getNews_url());
        i.putExtra("image",clickedItem.getImage_url());
        i.putExtra("date",clickedItem.getDate_time());
        i.putExtra("bid",clickedItem.getBk_id());
        startActivity(i);
    }

    @Override
    public void onDeleteClick(int position) {
        removeItem(position);
    }

    public void remove_bookmark(int position){
        final Bookmark_Items clickedItem = bk_array_list.get(position);
        final String username=SharedPreferenceManager.getInstance(getContext()).getUserName();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.remove_bookmark, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                   // e.printStackTrace();
                    Log.e(TAG, "RemoveBookmarkResponseError", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(),error.toString(),Toast.LENGTH_SHORT).show();
                Log.e(TAG, "RemoveBookmarkError", error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userid",username);
                params.put("b_id",String.valueOf(clickedItem.getBk_id()));
                return params;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    public void no_bookmark(){
        if(bk_array_list.isEmpty() && !(SharedPreferenceManager.getInstance(getContext()).getUserName().equals("Guest"))){
            no_bookmark.setVisibility(View.VISIBLE);
        }
        else {
            no_bookmark.setVisibility(View.GONE);
        }
    }

}
