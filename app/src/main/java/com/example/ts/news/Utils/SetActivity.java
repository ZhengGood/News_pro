//package com.example.ts.news.Utils;
//
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.ts.news.Activity.UpdateInfoService;
//import com.example.ts.news.Bean.UpdateInfo;
//import com.example.ts.news.R;
//
///**
//
// */
//public class SetActivity extends AppCompatActivity {
//
//
//	private UpdateInfo info;
//	private ProgressDialog progressDialog;
//	UpdateInfoService updateInfoService;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.fragment_main);
//
//		button.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				checkUpdate();
//			}
//		});
//	}
//
//	private void checkUpdate(){
//		Toast.makeText(SetActivity.this, "检查中", Toast.LENGTH_SHORT).show();
//
//		new Thread() {
//			public void run() {
//				try {
//					updateInfoService = new UpdateInfoService(SetActivity.this);
//					info = updateInfoService.getUpDateInfo();
//					handler1.sendEmptyMessage(0);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			};
//		}.start();
//	}
//
//	@SuppressLint("HandlerLeak")
//	private Handler handler1 = new Handler() {
//		public void handleMessage(Message msg) {
//
//			if (updateInfoService.isNeedUpdate()) {
//				showUpdateDialog();
//			}
//		};
//	};
//
//
//	private void showUpdateDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setIcon(android.R.drawable.ic_dialog_info);
//		builder.setTitle("确认升级到版本\n" + info.getVersionCode());
//		builder.setMessage(info.getContent());
//		builder.setCancelable(false);
//		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (Environment.getExternalStorageState().equals(
//						Environment.MEDIA_MOUNTED)) {
//					downFile(info.getUrl());
//				} else {
//					Toast.makeText(SetActivity.this, "SDk未装载",
//							Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
//		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//			}
//		});
//		builder.create().show();
//	}
//
//	void downFile(final String url) {
//		progressDialog = new ProgressDialog(SetActivity.this);
//		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		progressDialog.setTitle("升级App");
//		progressDialog.setMessage("新功能");
//		progressDialog.setProgress(0);
//		progressDialog.show();
//		updateInfoService.downLoadFile(url, progressDialog,handler1);
//	}
//
//}