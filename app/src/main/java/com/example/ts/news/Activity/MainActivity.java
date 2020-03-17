package com.example.ts.news.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ts.news.Bean.News;
import com.example.ts.news.Bean.UpdateInfo;
import com.example.ts.news.Fragment.MainFragment;
import com.example.ts.news.Fragment.MineFragment;
import com.example.ts.news.Fragment.SettingFragment;
import com.example.ts.news.R;
import com.example.ts.news.Utils.ApplicationUtil;



import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private UpdateInfo info;
    private ProgressDialog progressDialog;
    UpdateInfoService updateInfoService;


    private LinearLayout ll_main, ll_setting, ll_mine;

    private MainFragment mainFragment;
    private SettingFragment settingFragment;
    private MineFragment mineFragment;

    private List<Fragment> fragmentList = new ArrayList<>();

    private ImageView img_main, img_seting, img_mine;
    private TextView text_main, text_setting, text_mine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
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

        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();

        ll_main.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_mine.setOnClickListener(this);
        ApplicationUtil.getInstance().addActivity(this);


        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

    }


    private void initView() {

        ll_main = (LinearLayout) findViewById(R.id.layout_main);
        ll_setting = (LinearLayout) findViewById(R.id.layout_setting);
        ll_mine = (LinearLayout) findViewById(R.id.layout_mine);

        text_main = (TextView) findViewById(R.id.text_main);
        text_setting = (TextView) findViewById(R.id.text_setting);
        text_mine = (TextView) findViewById(R.id.text_mine);

        img_main = (ImageView) findViewById(R.id.img_main);
        img_seting = (ImageView) findViewById(R.id.img_setting);
        img_mine = (ImageView) findViewById(R.id.img_mine);
        img_main.setImageResource(R.drawable.main_selected);

    }

    private void initFragment() {
        mainFragment = new MainFragment();

        addFragment(mainFragment);
        showFragment(mainFragment);

    }

    /*添加fragment*/
    private void addFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(R.id.main_content, fragment).commit();
            fragmentList.add(fragment);

        }
    }

    /*显示fragment*/
    private void showFragment(Fragment fragment) {
        for (Fragment frag : fragmentList) {
            if (frag != fragment) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(frag).commit();
            }
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.show(fragment).commit();
    }


    @Override
    public void onClick(View view) {
        int current = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;


        switch (view.getId()) {
            case R.id.layout_main: {
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                }
                addFragment(mainFragment);

                showFragment(mainFragment);
                if (current  == Configuration.UI_MODE_NIGHT_YES) {
                    text_main.setTextColor(Color.parseColor("#B15C28"));
                    text_setting.setTextColor(Color.WHITE);
                    text_mine.setTextColor(Color.WHITE);
                } else {
                    text_main.setTextColor(Color.parseColor("#13227a"));
                    text_setting.setTextColor(Color.BLACK);
                    text_mine.setTextColor(Color.BLACK);
                }

                img_main.setImageResource(R.drawable.main_selected);
                img_seting.setImageResource(R.drawable.setting);
                img_mine.setImageResource(R.drawable.mine);

            }
            break;
            case R.id.layout_setting: {
                if (settingFragment == null) {
                    settingFragment = new SettingFragment();
                }
                addFragment(settingFragment);

                showFragment(settingFragment);

                if (current  == Configuration.UI_MODE_NIGHT_YES) {
                    text_setting.setTextColor(Color.parseColor("#B15C28"));
                    text_main.setTextColor(Color.WHITE);
                    text_mine.setTextColor(Color.WHITE);
                }else{
                    text_setting.setTextColor(Color.parseColor("#13227a"));
                    text_main.setTextColor(Color.BLACK);
                    text_mine.setTextColor(Color.BLACK);
                }


                img_main.setImageResource(R.drawable.main);
                img_seting.setImageResource(R.drawable.setting_selected);
                img_mine.setImageResource(R.drawable.mine);

            }
            break;
            case R.id.layout_mine: {
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                }
                addFragment(mineFragment);
                showFragment(mineFragment);

                if (current == Configuration.UI_MODE_NIGHT_YES) {
                    text_mine.setTextColor(Color.parseColor("#B15C28"));
                    text_main.setTextColor(Color.WHITE);
                    text_setting.setTextColor(Color.WHITE);
                }else{
                    text_mine.setTextColor(Color.parseColor("#13227a"));
                    text_main.setTextColor(Color.BLACK);
                    text_setting.setTextColor(Color.BLACK);

                }

                img_main.setImageResource(R.drawable.main);
                img_seting.setImageResource(R.drawable.setting);
                img_mine.setImageResource(R.drawable.mine_selected);
            }

            default:
                break;
        }
    }

}
