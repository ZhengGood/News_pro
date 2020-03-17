package com.example.ts.news.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ts.news.R;
import com.example.ts.news.Utils.MyDatabaseHelper;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewCallbackClient;
import com.tencent.smtt.sdk.WebViewClient;

import static java.security.AccessController.getContext;


public class ShowNewsActivity extends AppCompatActivity {


    private com.tencent.smtt.sdk.WebView mWebView;
    private ImageView collect_news;

    // 添加用户等待显示控件
    private ProgressDialog mDialog;

    private MyDatabaseHelper helper;
    private TextView news_title_top;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }


        getWindow().setFormat(PixelFormat.TRANSLUCENT);//视频
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_news);


        initView();
        initX5();

        helper = new MyDatabaseHelper(this, "UserDB.db", null, 1);

        mDialog = new ProgressDialog(ShowNewsActivity.this);

        mDialog.setMessage("玩命加载ing");


        mWebView.setWebViewClient(new WebViewClient() {

            //网络请求部分
            @Override
            public com.tencent.smtt.export.external.interfaces.WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                Log.i("-->", "地址：" + url);
                return null;

            }


        });


        Intent intent = getIntent();
        final String news_url = intent.getStringExtra("url");
        final String news_title = intent.getStringExtra("title");
        final String news_date = intent.getStringExtra("date");
        final String news_author = intent.getStringExtra("author");
        final String news_picurl = intent.getStringExtra("pic_url");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setUserAgentString("Android");
        webSettings.setJavaScriptEnabled(true);//支持Js
        int current = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (current == Configuration.UI_MODE_NIGHT_YES) {
            mWebView.setDayOrNight(false);
        }

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                news_title_top.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
//                mWebView.loadUrl("JavaScript:function mFunc(){document.querySelector('#logo').querySelector('img').src=\"https://photocdn.sohu.com/20150906/mp30746702_1441530698601_2_th.jpg\";}mFunc();");
//                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('[class=\"topbar\"]').style.display=\"none\";}setTop();");//顶部导航


                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('header').style.display=\"none\";}setTop();");//顶部导航


                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('header').style.display=\"none\";}setTop();");//顶部导航
                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('article').style=\"overflow: hidden; max-height: none;\";}setTop();");//自动高度
                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('[class=\"m_article list-item list-article  clearfix\"]').style.display=\"none\";}setTop();");//<!-- 相关推荐 -->
                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('[class=\"footer\"]').style.display=\"none\";}setTop();");    // 打开APP
                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('[class=\"comment_list comment-list\"]').style.display=\"none\";}setTop();");    // 打开APP
                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('[class=\"comment_info js-replylink\"]').style.display=\"none\";}setTop();");    // 打开APP
                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('[class=\"copyright\"]').style.display=\"none\";}setTop();");    // 底部公司
                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('[class=\"a_adtemp a_topad js-topad\"]').style.display=\"none\";}setTop();");    // 打开APP
                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('[class=\"comment_title\"]').style.display=\"none\";}setTop();");    // 打开APP
                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('[class=\"open-modal\"]').style.display=\"none\";}setTop();");    // 打开APP
                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('hot_news hot-news-F796926G000380D0\"]').style.display=\"none\";}setTop();");    //加载
                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('relative-doc-list').style.display=\"none\";}setTop();");    //加载
                mWebView.loadUrl("JavaScript:function setTop(){document.querySelector('[class=\"list-more\"]').style.display=\"none\";}setTop();");    //加载


            }
        });


        mWebView.loadUrl(news_url);


        collect_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                collect_news.setImageResource(R.drawable.favorite_selected);

                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues values = new ContentValues();
                //组装数据
                values.put("news_url", news_url);
                values.put("news_title", news_title);
                values.put("news_date", news_date);
                values.put("news_author", news_author);
                values.put("news_picurl", news_picurl);

                db.insert("Collection_News", null, values);
                db.close();


                Toast.makeText(ShowNewsActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        mWebView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.show_news);
        collect_news = (ImageView) findViewById(R.id.collect_news);
        news_title_top = (TextView) findViewById(R.id.news_title_top);

    }

    private void initX5() {
        QbSdk.setDownloadWithoutWifi(true);
        //x5內核初始化回调
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("vv", " onViewInitFinished is " + arg0);

            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);

    }

}
