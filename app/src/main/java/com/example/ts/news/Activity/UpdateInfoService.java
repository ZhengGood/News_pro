package com.example.ts.news.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.example.ts.news.Bean.UpdateInfo;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class UpdateInfoService {
    ProgressDialog progressDialog;
    Handler handler;
    Context context;
    UpdateInfo updateInfo;


    private static final String TAG = "data";

    public UpdateInfoService(Context context) {
        this.context = context;
    }

    public UpdateInfo getUpDateInfo() throws Exception {
        String path = "http://39.107.95.141:8080/update/update.json";
        String re = requestHttp(path);
        JSONObject jsonObject = new JSONObject(re);

        Log.d(TAG, jsonObject.toString());

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

    public static String requestHttp(String url) {
        String responseData = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            responseData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData;
    }

    public boolean isNeedUpdate() {
        String new_version = updateInfo.getVersionName(); // 最新版本的版本号
        //获取当前版本号
        String now_version = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            now_version = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (new_version.equals(now_version)) {
            return false;
        } else {
            return true;
        }
    }


    public void downLoadFile(final String url, final ProgressDialog pDialog, Handler h) {
        handler = h;

        progressDialog = pDialog;
        handler = h;
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    int length = (int) entity.getContentLength();   //获取文件大小
                    progressDialog.setMax(length);                            //设置进度条的总长度
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                "Test.apk");
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
                    down();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    void down() {
        handler.post(new Runnable() {
            public void run() {
                progressDialog.cancel();
                update();
            }
        });
    }

    void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "Test.apk")),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}
