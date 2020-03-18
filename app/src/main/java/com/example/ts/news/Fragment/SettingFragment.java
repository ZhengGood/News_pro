package com.example.ts.news.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ts.news.Activity.CoverActivity;

import com.example.ts.news.Activity.CustomDialog;

import com.example.ts.news.Activity.CustomProgressDialog;
import com.example.ts.news.Bean.UpdateInfo;
import com.example.ts.news.R;
import com.example.ts.news.Utils.ApplicationUtil;
import com.example.ts.news.Utils.FileCacheUtils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.ts.news.Utils.HttpUtils.requestHttp;
import static com.tencent.smtt.sdk.TbsReaderView.TAG;


/**
 * Created by ts on 18-8-20.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Context context;
    private TextView exit_app, about_app, check_version, welcome_app, clear_cache, yejian, zhengchang;
    ProgressDialog progressDialog;
    Handler handler;
    UpdateInfo updateInfo;
    private UpdateInfo info;

    private long curTime = 0;


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

        TextView dialog_mess = (TextView) dialog.findViewById(R.id.dialog_mess);
        dialog_mess.setText("提示");
        TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
        dialog_title.setText("再看一会吧~");
        TextView tvCancel = (TextView) dialog.findViewById(R.id.cancel);
        tvCancel.setText("取消");
        TextView tvOk = (TextView) dialog.findViewById(R.id.ok);
        tvOk.setText("确认");
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
        TextView dialog_mess = (TextView) dialog.findViewById(R.id.dialog_mess);
        dialog_mess.setText("提示");
        TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
        dialog_title.setText("检查更新吗？");
        TextView tvCancel = (TextView) dialog.findViewById(R.id.cancel);
        tvCancel.setText("取消");
        TextView tvOk = (TextView) dialog.findViewById(R.id.ok);
        tvOk.setText("确定");
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                checkUpdate();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

    }

    private void checkUpdate() {
        new Thread() {
            public void run() {
                try {
                    info = getUpDateInfo();
                    handler1.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            if (isNeedUpdate()) {
                showUpdateDialog();
            }
        }
    };

    private void showUpdateDialog() {
        final CustomDialog builder = new CustomDialog(getContext(), R.style.customDialog, R.layout.dialog_exit);
        builder.show();
        TextView dialog_mess = (TextView) builder.findViewById(R.id.dialog_mess);
        dialog_mess.setText("新版本 " + info.getVersionCode() + ".0.0 版本来了");
        TextView dialog_title = (TextView) builder.findViewById(R.id.dialog_title);
        dialog_title.setText(Html.fromHtml("<font color='black' size='20'>" + info.getContent() + "</font>"));
        TextView tvCancel = (TextView) builder.findViewById(R.id.cancel);
        tvCancel.setText("取消");
        TextView tvOk = (TextView) builder.findViewById(R.id.ok);
        tvOk.setText("确定");


        builder.setCancelable(false);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    builder.dismiss();

                    update_version(info.getUrl());
                } else {
                    Toast.makeText(getActivity(), "SDk未装载",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();

            }
        });
    }

    /**
     * 判断是否为新版本
     * 是返回true
     * 否返回false
     */
    public boolean isNeedUpdate() {
        String new_version = updateInfo.getVersionName(); // 最新版本的版本号
        //获取当前版本号
        String now_version = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            now_version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (new_version.equals(now_version)) {
            Toast.makeText(getContext(), "当前版本是" + now_version + "已经是最新版了哦！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(getContext(), "当前版本是" + now_version, Toast.LENGTH_LONG).show();

            return true;
        }
    }

    public UpdateInfo getUpDateInfo() throws Exception {
        String path = "http://39.107.95.141:8080/update/update.json";
        String re = requestHttp(path);
        JSONObject jsonObject = new JSONObject(re);
        Log.d("请求", jsonObject.toString());
        UpdateInfo updateInfo = new UpdateInfo();

        String versionName = jsonObject.getString("versionName");
        updateInfo.setVersionName(versionName);
        int versionCode = jsonObject.getInt("versionCode");
        updateInfo.setVersionCode(versionCode);
        String content = jsonObject.getString("content");
        updateInfo.setContent(content);
        String url = jsonObject.getString("url");
        updateInfo.setUrl(url);

        System.out.println(url);
        this.updateInfo = updateInfo;
        return updateInfo;
    }


    public void update_version(final String url) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("升级App中");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(Html.fromHtml("<font color='black' size='20'>" + info.getContent() + "</font>"));
        progressDialog.setProgress(0);
        progressDialog.setIcon(R.drawable.version);
        progressDialog.setCancelable(true);
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getContext(), "耐心等待一下~正在后台下载", Toast.LENGTH_LONG).show();
                    }
                });
        progressDialog.show();


        new Thread() {

            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);

                HttpResponse response;
                try {
                    System.out.println(url + "地址2");
                    response = client.execute(get);

                    HttpEntity entity = response.getEntity();
                    int length = (int) entity.getContentLength();   //获取文件大小
                    System.out.println(length + "大小");

                    progressDialog.setMax(length);                    //设置进度条的总长度
                    final InputStream is = entity.getContent();

                    FileOutputStream fileOutputStream = null;

                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                "new.apk");
                        fileOutputStream = new FileOutputStream(file);
                        //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                        //看不出progressbar的效果。

                        byte[] buf = new byte[10];
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
                    progressDialog.dismiss();
                    update();

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void update() {
        File apkfile = new File(Environment.getExternalStorageDirectory(), "new.apk");
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Android7.0及以上版本
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //Uri contentUri = FileProvider.getUriForFile(mContext, "应用包名" + ".fileProvider", file);//参数二:应用包名+".fileProvider"(和步骤一中的Manifest文件中的provider节点下的authorities对应) 
                Uri contentUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".fileprovider", apkfile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

            } else {
                // Android7.0以下版本
                intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            getActivity().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            //progressDismiss();
        }

    }


    private void initView() {
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
/**
 * 夜间模式
 */
            case R.id.yejian: {
                Intent intent = new Intent(context, getActivity().getClass());
                int current = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                if (current == Configuration.UI_MODE_NIGHT_YES) {
                    Toast.makeText(getContext(), "当前已为夜间模式", Toast.LENGTH_SHORT).show();

                } else {
//                    int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                    Activity activity = (Activity) context;
                    ((AppCompatActivity) getActivity()).getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    activity.overridePendingTransition(R.anim.anim_scale, R.anim.anim_alpha);
                    getActivity().finish();
                    activity.overridePendingTransition(R.anim.anim_scale, R.anim.anim_alpha);
                    startActivity(intent);

                }
            }
            break;
/**
 * 正常模式
 */
            case R.id.zhengchang: {
                Intent intent = new Intent(context, getActivity().getClass());
                int current = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                if (current == Configuration.UI_MODE_NIGHT_YES) {
                    ((AppCompatActivity) getActivity()).getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

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
/**
 * 欢迎界面
 */
            case R.id.welcome_app: {
                Intent intent = new Intent(getContext(), CoverActivity.class);
                startActivity(intent);
            }
            break;
/**
 * 检查更新
 */
            case R.id.check_version: {
                upDate(view);
                Toast.makeText(getContext(), "检查中", Toast.LENGTH_LONG).show();


            }
            break;
/**
 * 关于
 */
            case R.id.about_app: {

                final CustomDialog builder2 = new CustomDialog(getContext(), R.style.customDialog, R.layout.dialog_exit);
                builder2.show();
                TextView dialog_mess = (TextView) builder2.findViewById(R.id.dialog_mess);
                dialog_mess.setText("欢迎使用 新闻 -News");
                TextView dialog_title = (TextView) builder2.findViewById(R.id.dialog_title);
                dialog_title.setText(Html.fromHtml("<font color='black' size='20'>" + "集新闻浏览、收藏、<br>为一体的高效<br>新闻App "+ "</font>"));
                TextView tvCancel = (TextView) builder2.findViewById(R.id.cancel);
                tvCancel.setText("取消");
                TextView tvOk = (TextView) builder2.findViewById(R.id.ok);
                tvOk.setText("我的主页");
                tvOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder2.dismiss();
                        Uri uri = Uri.parse("https://zhenggood.github.io./");    //设置跳转的网站
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);

                    }
                });


                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder2.dismiss();

                    }
                });

            }
            break;
/**
 * 清理缓存
 */
            case R.id.clear_cache: {
                clearCache();
            }
            break;
/**
 * 退出功能
 */
            case R.id.exit_app: {
                showDialog(view);

            }
            break;
            default:
                break;

        }
    }

}
