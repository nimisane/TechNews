package com.example.nimish.technews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class User_News_Adapter extends RecyclerView.Adapter<User_News_Adapter.User_News_ViewHolder> {

    String news_img_url;
    String news_headlines;
    String news_date;
    String userid;
    int news_id;
    int count;
    private Context user_news_context;
    private ArrayList<User_News_Items> user_news_item_list;
    private OnItemClickListener user_news_listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        user_news_listener = listener;
    }

    public User_News_Adapter(Context context,ArrayList<User_News_Items> user_news_items_list){
        this.user_news_context=context;
        this.user_news_item_list=user_news_items_list;
    }

    @NonNull
    @Override
    public User_News_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View user_news_view = LayoutInflater.from(user_news_context).inflate(R.layout.user_news_cardlayout,parent,false);
        return new User_News_ViewHolder(user_news_view);
    }

    @Override
    public void onBindViewHolder(@NonNull final User_News_ViewHolder holder, int position) {
        User_News_Items current_user_news = user_news_item_list.get(position);
        news_img_url = current_user_news.getUser_imageurl();
        news_headlines = current_user_news.getUser_news_headlines();
        news_date = current_user_news.getUser_news_time();
        userid = current_user_news.getUserid();
        news_id = current_user_news.getNews_id();
        count = current_user_news.getReport_news();

        Glide.with(user_news_context).load(news_img_url).into(holder.user_image);
        holder.user_headlines.setText(news_headlines);
        holder.news_userid.setText(userid);
        holder.news_date.setText(news_date);
        if(count==0){
            holder.report_news_count.setText("");
        }
        else if(count==1){
            holder.report_news_count.setText(count+" user reported this article as incorrect");
        }
        else {
        holder.report_news_count.setText(count+" users reported this article as incorrect");
        }

    }

    @Override
    public int getItemCount() {
        return user_news_item_list.size();
    }

    public class User_News_ViewHolder extends RecyclerView.ViewHolder{

        public ImageView user_image;
        public TextView user_headlines;
        public TextView report_news_count;
        public TextView news_userid;
        public TextView news_date;
        public User_News_ViewHolder(View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.user_news_image);
            user_headlines = itemView.findViewById(R.id.user_news_headlines);
            news_userid = itemView.findViewById(R.id.user_news_card_author);
            news_date= itemView.findViewById(R.id.user_news_card_time);
            report_news_count = itemView.findViewById(R.id.report_count);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(user_news_listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            user_news_listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
