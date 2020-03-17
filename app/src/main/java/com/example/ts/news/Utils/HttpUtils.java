package com.example.ts.news.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ts.news.Activity.CustomDialog;
import com.example.ts.news.Activity.MainActivity;
import com.example.ts.news.Fragment.TechFragment;
import com.example.ts.news.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ts on 18-8-29.
 * 关于网络请求的工具类
 * 提供4个静态方法：判断是否有网络连接、若没有弹出网络设置、网络请求（OKHttp）和根据图片的url路径获得Bitmap对象
 */


public class HttpUtils {
    //判断dialog是否存在的标志
    private static Dialog dialog = null;
    public Context context ;

    /**
     * 判断网络情况
     *
     * @param context 上下文
     * @return false 表示没有网络 true 表示有网络
     */
    public static boolean isNetworkAvalible(Context context) {
        // 获得网络状态管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 建立网络数组
            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();

            if (net_info != null) {
                for (int i = 0; i < net_info.length; i++) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 如果没有网络，则弹出网络设置对话框
    public static void checkNetwork(final Activity activity) {
        if (!HttpUtils.isNetworkAvalible(activity)) {

            final CustomDialog dialog=new CustomDialog(activity,R.style.customDialog,R.layout.dialog_exit);
            dialog.show();
            TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
            dialog_title.setText("哇哇~网络找不到了");
            TextView tvCancel = (TextView) dialog.findViewById(R.id.cancel);
            tvCancel.setText("取消");
            TextView tvOk = (TextView) dialog.findViewById(R.id.ok);
            tvOk.setText("设置");
            tvOk.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    activity.startActivityForResult(new Intent(
                                                    Settings.ACTION_DATA_ROAMING_SETTINGS),
                                            0);

                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
        return;
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

    /**
     * 根据图片的url路径获得Bitmap对象
     */
    public static Bitmap decodeUriAsBitmapFromNet(String imgUrl) {
        if(TextUtils.isEmpty(imgUrl)){
            return null;
        }
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(imgUrl);

        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
