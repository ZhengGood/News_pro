package com.example.ts.news.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ts.news.Activity.CoverActivity;

import com.example.ts.news.Activity.CustomDialog;

import com.example.ts.news.R;
import com.example.ts.news.Utils.ApplicationUtil;
import com.example.ts.news.Utils.FileCacheUtils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by ts on 18-8-20.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Context context;
    private TextView exit_app, about_app, check_version, welcome_app, clear_cache, yejian, zhengchang;
    ProgressDialog progressDialog;
    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        initView();

        progressDialog = new ProgressDialog(context);
        welcome_app.setOnClickListener(this);

        check_version.setOnClickListener(this);

        about_app.setOnClickListener(this);

        clear_cache.setOnClickListener(this);

        exit_app.setOnClickListener(this);

        exit_app.setOnClickListener(this);
        yejian.setOnClickListener(this);
        zhengchang.setOnClickListener(this);
        return view;
    }

    private void clearCache() {
        try {
            String cacheSize = FileCacheUtils.getCacheSize(context.getCacheDir());
            FileCacheUtils.cleanInternalCache(context);
            Toast.makeText(context, "本次清理" + cacheSize + "缓存", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showDialog(View view) {
        final CustomDialog dialog = new CustomDialog(getContext(), R.style.customDialog, R.layout.dialog_exit);
        dialog.show();

        TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
        dialog_title.setText("再看一会吧~");
        TextView tvCancel = (TextView) dialog.findViewById(R.id.cancel);
        tvCancel.setText("取消");
        TextView tvOk = (TextView) dialog.findViewById(R.id.ok);
        tvOk.setText("确定");
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ApplicationUtil.getInstance().exit();
            }
        });
    }

    public void upDate(View view) {
        final CustomDialog dialog = new CustomDialog(getContext(), R.style.customDialog, R.layout.dialog_exit);
        dialog.show();
        TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
        dialog_title.setText("立即更新吗？");
        TextView tvCancel = (TextView) dialog.findViewById(R.id.cancel);
        tvCancel.setText("取消");
        TextView tvOk = (TextView) dialog.findViewById(R.id.ok);
        tvOk.setText("确定");
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());

                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("升级App");
                progressDialog.setMessage("新功能");
                progressDialog.setProgress(0);
                progressDialog.show();
//                update_version();
                new Thread() {
                    public void run() {
                        HttpClient client = new DefaultHttpClient();
                        HttpGet get = new HttpGet("http://39.107.95.141:8080/update/app-release.apk");

                        HttpResponse response;
                        try {


                            response = client.execute(get);
                            HttpEntity entity = response.getEntity();
                            int length = (int) entity.getContentLength();   //获取文件大小
                            System.out.println(length + "大小");
                            progressDialog.setMax(length);                            //设置进度条的总长度
                            InputStream is = entity.getContent();
                            FileOutputStream fileOutputStream = null;
                            if (is != null) {
                                File file = new File(
                                        Environment.getExternalStorageDirectory(),
                                        "new.apk");
                                fileOutputStream = new FileOutputStream(file);
                                //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                                //看不出progressbar的效果。
                                byte[] buf = new byte[10000];
                                int ch = -1;
                                int process = 0;
                                while ((ch = is.read(buf)) != -1) {
                                    fileOutputStream.write(buf, 0, ch);
                                    process += ch;
                                    progressDialog.setProgress(process);       //这里就是关键的实时更新进度了！
                                }

                            }
                            fileOutputStream.flush();
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                            update();

                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }


          public   void update() {

                File apkfile = new File(Environment.getExternalStorageDirectory(), "new.apk");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (!apkfile.exists()) {
                    return;
                }
//判断是否是AndroidN以及更高的版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        String ss = "com.example.ts.news" + ".fileprovider";
                        Uri contentUri = FileProvider.getUriForFile(context.getApplicationContext(), ss, apkfile);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

    }
public   void  update_version() {

    new Thread() {
        public void run() {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://39.107.95.141:8080/update/app-release.apk");
            HttpResponse response;
            try {

                response = client.execute(get);
                HttpEntity entity = response.getEntity();
                int length = (int) entity.getContentLength();   //获取文件大小
                System.out.println(length + "大小");
                progressDialog.setMax(length);                            //设置进度条的总长度
                InputStream is = entity.getContent();
                FileOutputStream fileOutputStream = null;
                if (is != null) {
                    File file = new File(
                            Environment.getExternalStorageDirectory(),
                            "new.apk");
                    fileOutputStream = new FileOutputStream(file);
                    //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                    //看不出progressbar的效果。
                    byte[] buf = new byte[10000];
                    int ch = -1;
                    int process = 0;
                    while ((ch = is.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, ch);
                        process += ch;
                        progressDialog.setProgress(process);       //这里就是关键的实时更新进度了！
                    }

                }
                fileOutputStream.flush();
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                update();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }.start();
}
    public   void update(){

        File apkfile = new File(Environment.getExternalStorageDirectory(), "new.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (!apkfile.exists()) {
            return;
        }
//判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                String ss = "com.example.ts.news" + ".fileprovider";
                Uri contentUri = FileProvider.getUriForFile(context.getApplicationContext(), ss, apkfile);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void initView(){
        welcome_app = view.findViewById(R.id.welcome_app);
        check_version = view.findViewById(R.id.check_version);
        about_app = view.findViewById(R.id.about_app);
        clear_cache = view.findViewById(R.id.clear_cache);
        exit_app = view.findViewById(R.id.exit_app);

        yejian = view.findViewById(R.id.yejian);
        zhengchang = view.findViewById(R.id.zhengchang);

        context = getContext();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.yejian: {
                Intent intent = new Intent(context, getActivity().getClass());
                int current = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                if (current == Configuration.UI_MODE_NIGHT_YES) {
                    Toast.makeText(getContext(), "当前已为夜间模式", Toast.LENGTH_SHORT).show();

                } else {
                    ((AppCompatActivity) getActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.anim_scale, R.anim.anim_alpha);
                    getActivity().finish();
                    activity.overridePendingTransition(R.anim.anim_scale, R.anim.anim_alpha);
                    startActivity(intent);
                }
            }
            break;

            case R.id.zhengchang: {
                Intent intent = new Intent(context, getActivity().getClass());
                int current = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                if (current == Configuration.UI_MODE_NIGHT_YES) {
                    ((AppCompatActivity) getActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.anim_scale, R.anim.anim_alpha);
                    getActivity().finish();
                    activity.overridePendingTransition(R.anim.anim_scale, R.anim.anim_alpha);
                    startActivity(intent);

                } else {
                    Toast.makeText(getContext(), "当前已为正常模式", Toast.LENGTH_SHORT).show();
                }
            }

            break;

            case R.id.welcome_app: {
                Intent intent = new Intent(getContext(), CoverActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.check_version: {
                upDate(view);
                Toast.makeText(getContext(), "检查中", Toast.LENGTH_SHORT).show();

            }

            break;
            case R.id.about_app: {
                Toast.makeText(getContext(), "@News news.com version 1.0", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.clear_cache: {
                clearCache();
            }
            break;
            case R.id.exit_app: {
                showDialog(view);

            }
            break;
            default:
                break;

        }
    }

}
