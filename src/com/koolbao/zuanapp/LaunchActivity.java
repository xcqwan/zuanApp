package com.koolbao.zuanapp;

import com.koolbao.utils.UserInfoUtils;
import com.koolbao.zuanapp.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class LaunchActivity extends Activity {
	private final int IsFirstVist = 0;
	private final int Login = 1;
	private final int UnLogin = 2;
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Intent intent;
			switch (msg.what) {
			case IsFirstVist:
				intent = new Intent(LaunchActivity.this, GuideActivity.class);
				break;
			case Login:
				intent = new Intent(LaunchActivity.this, ContentActivity.class);
				break; 
			case UnLogin:
				intent = new Intent(LaunchActivity.this, LoginActivity.class);
				break;
			default:
				intent = new Intent(LaunchActivity.this, LoginActivity.class);
				break;
			}
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			finish();
			super.handleMessage(msg);
		}
	};
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		//隐藏底部导航栏，针对自带虚拟按钮的操作系统
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		
		SharedPreferences sharedPreferences = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		//初次访问标记
		boolean isFirstVist = UserInfoUtils.getVisitFlag(sharedPreferences);
		boolean isLogin = UserInfoUtils.getLoginFlag(sharedPreferences);
		if (isFirstVist) {
			//第一次登录
			mHandler.sendEmptyMessageDelayed(IsFirstVist, 500);
		} else if (isLogin) {
			//已登录
			mHandler.sendEmptyMessageDelayed(Login, 500);
		} else {
			//未登录
			mHandler.sendEmptyMessageDelayed(UnLogin, 500);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
