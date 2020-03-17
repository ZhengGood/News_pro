package com.example.ts.news.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ts.news.Bean.News;
import com.example.ts.news.R;

import java.util.List;

/**
 * Created by ts on 18-8-21.
 */

public class NewsAdapter extends ArrayAdapter<News> implements View.OnClickListener {
    private int resourceId;
    private CallBack mCallBack;
    private int a;

    public NewsAdapter(@NonNull Context context, @NonNull int textViewResourceId, @NonNull List<News> objects, CallBack callBack) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        mCallBack = callBack;
    }


    public interface CallBack {
        public void click(View view);
    }


    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        int p = position % 2;
        if (p == 0) {
            return a = 0;
        } else {
            return a = 1;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        News news = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.newsImg = view.findViewById(R.id.news_item_img);
            viewHolder.newsTitle = view.findViewById(R.id.news_item_title);
            viewHolder.newsDelete = view.findViewById(R.id.delete_item);
            viewHolder.newsAuthor = view.findViewById(R.id.news_item_author);
            viewHolder.newsDate = view.findViewById(R.id.news_item_date);
            Glide.with(getContext())
                    .load(news.getNews_picurl()).error(R.mipmap.ic_launcher)
                    .into(viewHolder.newsImg);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
            Glide.with(getContext())
                    .load(news.getNews_picurl()).error(R.mipmap.ic_launcher)
                    .into(viewHolder.newsImg);
            view.setTag(viewHolder);
        }

        // 加载图片

//        viewHolder.newsImg.setImageBitmap(news.getNews_img());
        viewHolder.newsTitle.setText(news.getNews_title());
        viewHolder.newsAuthor.setText(news.getAuthor_name());
        viewHolder.newsDate.setText(news.getDate());

        viewHolder.newsDelete.setTag(position);
        viewHolder.newsDelete.setOnClickListener(this);

        return view;
    }


    static class ViewHolder {
        ImageView newsImg;
        TextView newsTitle;
        TextView newsAuthor;
        TextView newsDate;
        ImageView newsDelete;
    }

    @Override
    public void onClick(View view) {
        mCallBack.click(view);
    }
}
