package com.example.ts.news.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ts.news.Activity.CustomDialog;
import com.example.ts.news.Activity.ShowNewsActivity;
import com.example.ts.news.Adapter.NewsAdapter;
import com.example.ts.news.Bean.News;
import com.example.ts.news.LoadListView;
import com.example.ts.news.R;
import com.example.ts.news.Utils.ApplicationUtil;
import com.example.ts.news.Utils.HttpUtils;
import com.example.ts.news.Utils.MD5Encoder;
//import com.example.ts.news.Utils.MyBitmapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 第二Fragment    //财经资讯   234
 * Created by ts on zz  每日头条1   T1348647853363
 */

public class TechFragment extends Fragment implements LoadListView.ILoadListener,
        LoadListView.RLoadListener, NewsAdapter.CallBack {
    private static final String TAG = "TechFragment";

    final String url = "http://c.m.163.com/nc/article/headline/T1348647853363/0-40.html";
    private View view;
    private ListView listView;//

    private LoadListView mListView;

    private List<News> newsList = new ArrayList<News>();//改
    private NewsAdapter adapter;
//    private MyBitmapUtils myBitmapUtils;


    public TechFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.news, container, false);
        Log.d(TAG, "onCreateView");
//        myBitmapUtils = new MyBitmapUtils(getContext());
        setupViews();
        if (!HttpUtils.isNetworkAvalible(getContext())) {
            HttpUtils.checkNetwork(getActivity());
            view.setBackgroundResource(R.drawable.err);
            Toast.makeText(getContext(), "网络走丢了，请检查网络设置！", Toast.LENGTH_SHORT).show();
        } else {
            initNews();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), ShowNewsActivity.class);

                intent.putExtra("title",   newsList.get(i - mListView.getHeaderViewsCount()).getNews_title());
                intent.putExtra("url",     newsList.get(i - mListView.getHeaderViewsCount()).getNews_url());
                intent.putExtra("date",    newsList.get(i - mListView.getHeaderViewsCount()).getDate());
                intent.putExtra("author",  newsList.get(i - mListView.getHeaderViewsCount()).getAuthor_name());
                intent.putExtra("pic_url", newsList.get(i - mListView.getHeaderViewsCount()).getNews_picurl());
                startActivity(intent);
                //添加Activity过场动画
                getActivity().overridePendingTransition(R.anim.anim_scale, R.anim.anim_alpha);
            }

        });

        return view;


    }

    private void initNews() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonData = HttpUtils.requestHttp(url);
                parseJSONWithGSON(jsonData);
                System.out.println("啊哈1"+jsonData);
            }
        }).start();
    }


    private void parseJSONWithGSON(String jsonData) {

        try {

            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("T1348647853363");
            newsList.clear();
            for (int i = 0; i <jsonArray.length(); i++) {
                JSONObject json_news = jsonArray.getJSONObject(i);

                try {
                    json_news.getString("url");
                } catch (JSONException gg) {

                    continue;
                }
                String imgUrl = json_news.getString("imgsrc");

                /**
                 * 采取三级缓存策略加载图片
                 */

//                Bitmap bitmap = myBitmapUtils.getBitmap(imgUrl);
                /**
                 * 不采取缓存策略
                 */
//                Bitmap bitmap = HttpUtils.decodeUriAsBitmapFromNet(imgUrl);
                String title = json_news.getString("title");
                String date = json_news.getString("mtime");
                String author_name = json_news.getString("source");
                String url = json_news.getString("url");
                Log.d(TAG, "url:*-*-*-*-*-*-*" + imgUrl);
                News news = new News(title, url, imgUrl, date, author_name);
                newsList.add(news);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }

            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 刷新数据
     */
    private void parseJSONWithGSON_Refresh(String jsonData) {

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("T1348647853363");


            JSONObject json_news = jsonArray.getJSONObject(new Random().nextInt(30) + 1);
            String imgUrl = json_news.getString("imgsrc");
            Log.d(TAG, "url:*-*-*-*-*-*-*" + imgUrl);
//            Bitmap bitmap = HttpUtils.decodeUriAsBitmapFromNet(imgUrl);
            String title = json_news.getString("title");
            String date = json_news.getString("mtime");
            String author_name = json_news.getString("source");
            String url = json_news.getString("url");

            News news = new News(title, url, imgUrl, date, author_name);
            newsList.add(0, news);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 加载更多
     */

    private void parseJSONWithGSON_Load(String jsonData) {

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("T1348647853363");

            JSONObject json_news = jsonArray.getJSONObject(new Random().nextInt(30) + 1);
            String imgUrl = json_news.getString("imgsrc");
            Log.d(TAG, "url:*-*-*-*-*-*-*" + imgUrl);
            Bitmap bitmap = HttpUtils.decodeUriAsBitmapFromNet(imgUrl);
            String title = json_news.getString("title");
            String date = json_news.getString("mtime");
            String author_name = json_news.getString("source");
            String url = json_news.getString("url");

            News news = new News(title, url, imgUrl, date, author_name);
            newsList.add(news);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void initNewDatas() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonData = HttpUtils.requestHttp(url);
                parseJSONWithGSON_Load(jsonData);
                System.out.println("啊哈1"+jsonData);
            }
        }).start();


    }

    private void initRefreshDatas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonData = HttpUtils.requestHttp(url);
                parseJSONWithGSON_Refresh(jsonData);
            }
        }).start();

    }

    private void setupViews() {
        listView = view.findViewById(R.id.listview_news);//
        mListView = view.findViewById(R.id.lv_main);
//        上拉加载接口
        mListView.setInterface(this);
        mListView.setReflashInterface(this);
        newsList = new ArrayList<News>();
        adapter = new NewsAdapter(getContext(), R.layout.news_item, newsList, this);
        mListView.setAdapter(adapter);


    }

    //实现onLoad()方法。
    @Override
    public void onLoad() {
        //添加延时效果模拟数据加载
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initNewDatas();//得到新数据
                mListView.loadCompleted();
            }
        }, 2000);
    }

    //  实现的刷新方法
    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initRefreshDatas();//得到新数据
                mListView.reflashComplete();
            }
        }, 3000);

    }

    @Override
    public void click(View view) {
        Toast.makeText(getContext(), "该新闻已删除！", Toast.LENGTH_SHORT).show();
        newsList.remove(Integer.parseInt(view.getTag().toString()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }
}

