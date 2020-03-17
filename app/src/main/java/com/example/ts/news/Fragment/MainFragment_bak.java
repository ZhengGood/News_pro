//package com.example.ts.news.Fragment;
//
//import android.content.Context;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.ts.news.Activity.CustomDialog;
//import com.example.ts.news.Adapter.CategoryAdapter;
//import com.example.ts.news.Adapter.NewsAdapter;
//import com.example.ts.news.Bean.News;
//import com.example.ts.news.R;
//import com.example.ts.news.Utils.ApplicationUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by ts on 18-8-20.
// */
//
//public class MainFragment_bak extends Fragment implements NewsAdapter.CallBack {
//    private static final String TAG = "MainFragment_bak";
//
//    private View view;
//
//    private List<String> categoryList = new ArrayList<>();
//    private List<News> newsList = new ArrayList<>();
//
//    private RecyclerView recyclerView;
//    private ListView listView;
//    private CategoryAdapter categoryAdapter;
//
//    private NewsAdapter newsAdapter;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_main_bak, container, false);
////        initCategory();
//        initNews();
//        initView();
//
//        newsAdapter = new NewsAdapter(getContext(), R.layout.news_item, newsList, this);
//        listView = view.findViewById(R.id.list_view);
//        listView.setAdapter(newsAdapter);
//
//        return view;
//    }
//
//    private void initNews() {
//        for (int i = 0; i < 30; i++) {
////            News news = new News(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
////                    "这是新闻" + (i + 1) + "的标题","https://3g.163.com/money/20/0313/12/F7JM4JOB00259BNT.html",
////                    "http://cms-bucket.ws.126.net/2020/0313/de11a391j00q74u04003xc000s600e3c.jpg","ha","zx");
////            newsList.add(news);
//        }
//    }
//
//    private void initView() {
//        recyclerView = view.findViewById(R.id.recycler_view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setLayoutManager(layoutManager);
//        categoryAdapter = new CategoryAdapter(categoryList);
//        recyclerView.setAdapter(categoryAdapter);
//    }
//
//    @Override
//    public void click(View view) {
//        Toast.makeText(getContext(), "该新闻已hhh删除！", Toast.LENGTH_SHORT).show();
//        newsList.remove(Integer.parseInt(view.getTag().toString()));
//        newsAdapter.notifyDataSetChanged();
//    }
//
//
//}
