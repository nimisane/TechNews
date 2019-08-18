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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Daily_News_Adapter extends RecyclerView.Adapter<Daily_News_Adapter.Daily_News_ViewHolder> {

    private Context daily_news_context;
    private ArrayList<Daily_News_Items> daily_news_item_list;
    private OnItemClickListener daily_news_listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener daily_news_list_listener)
    {
        daily_news_listener = daily_news_list_listener;
    }

    public Daily_News_Adapter(Context context, ArrayList<Daily_News_Items> daily_news_item_list) {
        this.daily_news_context = context;
        this.daily_news_item_list = daily_news_item_list;
    }

    @NonNull
    @Override
    public Daily_News_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View daily_news_view= LayoutInflater.from(daily_news_context).inflate(R.layout.daily_news_cardlayout,parent,false);
        return new Daily_News_ViewHolder(daily_news_view);
    }

    @Override
    public void onBindViewHolder(@NonNull Daily_News_ViewHolder holder, int position) {

        Daily_News_Items current_daily_news = daily_news_item_list.get(position);
        String daily_news_img_url=current_daily_news.getImageurl();
        String daily_news_headlines=current_daily_news.getNews_headlines();
        String daily_news_reporter=current_daily_news.getNews_reporter();
        String daily_news_time=current_daily_news.getNews_time();

       // Picasso.with(daily_news_context).load(daily_news_img_url).fit().centerInside().into(holder.news_image);
        Glide.with(daily_news_context).load(daily_news_img_url).into(holder.news_image);
        holder.news_headlines.setText(daily_news_headlines);
        holder.news_reporter.setText(daily_news_reporter);
        holder.news_time.setText(daily_news_time);

    }

    @Override
    public int getItemCount() {
        return daily_news_item_list.size();
    }

    public class Daily_News_ViewHolder extends RecyclerView.ViewHolder{

        public ImageView news_image;
        public TextView news_headlines;
        public TextView news_reporter;
        public TextView news_time;

        public Daily_News_ViewHolder(View itemView) {
            super(itemView);

            news_image=itemView.findViewById(R.id.daily_news_card_imageview);
            news_headlines=itemView.findViewById(R.id.daily_news_card_headlines);
            news_reporter=itemView.findViewById(R.id.daily_news_card_author);
            news_time=itemView.findViewById(R.id.daily_news_card_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(daily_news_listener != null)
                    {
                        int position=getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            daily_news_listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
