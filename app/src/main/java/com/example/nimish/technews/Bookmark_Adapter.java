package com.example.nimish.technews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Bookmark_Adapter extends RecyclerView.Adapter<Bookmark_Adapter.Bookmark_Viewholder> {

    private Context bk_context;
    private ArrayList<Bookmark_Items> bk_list;
    private OnItemClickListener bk_listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        bk_listener = listener;
    }

    public Bookmark_Adapter(Context context,ArrayList<Bookmark_Items> book_list){
        bk_context = context;
        bk_list = book_list;
    }

    @NonNull
    @Override
    public Bookmark_Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =LayoutInflater.from(bk_context).inflate(R.layout.bookmark_cardlayout,parent,false);
        return new Bookmark_Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Bookmark_Viewholder holder, int position) {
        Bookmark_Items currentItem = bk_list.get(position);
        String news_img_url=currentItem.getImage_url();
        String news_headlines=currentItem.getHeadlines();
        String news_reporter=currentItem.getAuthor();
        String news_time=currentItem.getDate_time();

        Glide.with(bk_context).load(news_img_url).into(holder.bk_image);
        holder.bk_headlines.setText(news_headlines);
        holder.bk_author.setText(news_reporter);
        holder.bk_time.setText(news_time);
    }

    @Override
    public int getItemCount() {
        return bk_list.size();
    }

    public class Bookmark_Viewholder extends  RecyclerView.ViewHolder{

        public ImageView bk_image;
        public TextView bk_headlines;
        public TextView bk_author;
        public TextView bk_time;
        public ImageView remove;

        public Bookmark_Viewholder(View itemView) {
            super(itemView);
            bk_image=itemView.findViewById(R.id.image);
            bk_headlines=itemView.findViewById(R.id.bookmark_headlines);
            bk_author=itemView.findViewById(R.id.bk_author);
            bk_time=itemView.findViewById(R.id.bk_time);
            remove=itemView.findViewById(R.id.bk_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bk_listener != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            bk_listener.onItemClick(position);
                        }
                    }
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bk_listener != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            bk_listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
}
