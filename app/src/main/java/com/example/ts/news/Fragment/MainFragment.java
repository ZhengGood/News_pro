package com.example.ts.news.Fragment;

import android.content.ActivityNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ts.news.Activity.MyImageLoader;
import com.example.ts.news.Bean.banner;
import com.example.ts.news.day;

import com.example.ts.news.Activity.ShowNewsActivity;
import com.example.ts.news.Adapter.FragmentAdapter;
import com.example.ts.news.R;
import com.example.ts.news.Utils.HttpUtils;
import com.example.ts.news.Utils.MD5Encoder;
import com.example.ts.news.Utils.TimeCount;
import com.example.ts.news.day;
import com.google.gson.Gson;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by ts on 18-8-20.
 */

public class MainFragment extends Fragment {

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private List<String> titleList;
    private List<Fragment> fragmentList;

    private FragmentAdapter fragmentAdapter;

    private TechFragment tech_fragment;
    private EnteFragment ente_fragment;
    private SportFragment sport_fragment;
    private MiliFragment mili_fragment;
    private Banner banner;
    private String url = "http://api.tianapi.com/bulletin/index?key=81c0699ae798c4a8066c2df7a20e4e52&num=10&rand=1";
    //社会新闻
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        initBanner();
        initView();

        fragmentChange();
        TimeCount.getInstance().setTime(System.currentTimeMillis());
        return view;
    }

    private void initView() {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
    }


    private void fragmentChange() {
        fragmentList = new ArrayList<>();
        tech_fragment = new TechFragment();
        ente_fragment = new EnteFragment();
        sport_fragment = new SportFragment();
        mili_fragment = new MiliFragment();

        fragmentList.add(tech_fragment);
        fragmentList.add(ente_fragment);
        fragmentList.add(sport_fragment);
        fragmentList.add(mili_fragment);



        titleList = new ArrayList<>();
        titleList.add("每日头条");
        titleList.add("财经资讯");
        titleList.add("科技资讯");
        titleList.add("体育资讯");

        fragmentAdapter = new FragmentAdapter(getActivity().getSupportFragmentManager(), fragmentList, titleList);
        viewPager.setAdapter(fragmentAdapter);

        //将tabLayout与viewPager连起来
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initBanner() {
        banner = (Banner) view.findViewById(R.id.banner);
        //ok网络请求
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        //用OkHttp里面的Call对象打点调用 异步请求数据的抽象方法
        call.enqueue(new Callback() {
            //建个集合用来存放图片url的地址
            private List<String> picUrlList;
            private List<String> title;
            //此集合是bean解析过来的集合
            private List<banner.NewslistBean> list;

            @Override
            public void onFailure(Call call, IOException e) {
                //访问网络失败的方法(自动生成的)
            }

            //访问网络成功的方法(自动生成的)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //把数据流转换成json字符串
                String json = response.body().string();
                Log.e("++++++++", "这是访问到的数据：" + json);
                //开始用gson解析
                Gson gson = new Gson();
                banner ladyBean = gson.fromJson(json, banner.class);
                //拿到bean类里的集合
                list = ladyBean.getNewslist();
                Log.e("++++++++", "这是bean集合里的数据：" + list);

                //设全局此集合专门用来存放图片url地址的
                picUrlList = new ArrayList<String>();
                title = new ArrayList<String>();
                for (int i = 0; i < list.size(); i++) {
                    //循环把图片地址添加到string泛型的集合里
                    picUrlList.add(list.get(i).getImgsrc());
                    title.add(list.get(i).getTitle());
                }
                Log.e("++++++++", "这是专门存放图片url集合里的数据：" + picUrlList);
                Log.e("++++++++", "这是专门存放图片url集合里的数据：" + title);

                //更新住UI开启返回主线程的方法
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
                            banner.setImageLoader(new MyImageLoader());
                            banner.setImages(picUrlList);
                            banner.setBannerTitles(title);
                            //设置指示器位置（当banner模式中有指示器时）
                            banner.setIndicatorGravity(BannerConfig.LEFT);
                            banner.setDelayTime(3000);
                            banner.start();
                        } catch (ActivityNotFoundException gg) {
                            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
                            banner.setImageLoader(new MyImageLoader());
                            banner.setImages(picUrlList);
                            banner.setBannerTitles(title);
                            //设置指示器位置（当banner模式中有指示器时）
                            banner.setIndicatorGravity(BannerConfig.LEFT);
                            banner.setDelayTime(3000);
                            banner.start();
                        }
                    }
                });
            }
        });
    }

}


