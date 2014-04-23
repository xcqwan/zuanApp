package com.koolbao.zuanapp;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.time.DateUtils;

import com.koolbao.adapter.ContentAdapter;
import com.koolbao.custom.BaoBeiPageFragment;
import com.koolbao.custom.HomePageFragment;
import com.koolbao.custom.TradePageFragment;
import com.koolbao.custom.MorePageFragment;
import com.koolbao.utils.DownImage;
import com.koolbao.utils.UserInfoUtils;
import com.koolbao.zuanapp.R;
import com.umeng.analytics.MobclickAgent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ContentActivity  extends FragmentActivity{
	private Button homtBtn;
	private Button baobeiBtn;
	private Button tradeBtn;
	private Button moreBtn;
	TextView msg_tv;
	private TextView title;
	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();
	private ContentAdapter contentAdapter;
	private ViewPager contentPager;
	private int pager_position = 0;
	private SharedPreferences sharePrefer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		initCostom();
		initListener();
		changePosition();
		checkSub();
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

	private void checkSub() {
		String end_date = UserInfoUtils.end_date;
		try {
			Date endDate = DateUtils.parseDate(end_date, "yyyy-MM-dd");
			Date checkDate = DateUtils.addDays(endDate, -7);
			Date today = new Date();
			if (today.after(checkDate)) {
				msg_tv.setVisibility(TextView.VISIBLE);
			} else {
				msg_tv.setVisibility(TextView.GONE);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void initListener() {
		HomePageFragment homePage = new HomePageFragment();
		pagerItemList.add(homePage);
		BaoBeiPageFragment brandPage = new BaoBeiPageFragment();
		pagerItemList.add(brandPage);
		TradePageFragment defindPage = new TradePageFragment();
		pagerItemList.add(defindPage);
		MorePageFragment morePage = new MorePageFragment();
		pagerItemList.add(morePage);
		contentAdapter = new ContentAdapter(getSupportFragmentManager(), pagerItemList);
		contentPager.setAdapter(contentAdapter);
		contentPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				pager_position = position;
				changePosition();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int position) {
			}
		});
		//主页
		homtBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contentPager.setCurrentItem(0);
				pager_position = 0;
				changePosition();
			}
		});
		//宝贝
		baobeiBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contentPager.setCurrentItem(1);
				pager_position = 1;
				changePosition();
			}
		});
		//订单
		tradeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contentPager.setCurrentItem(2);
				pager_position = 2;
				changePosition();
			}
		});
		//更多
		moreBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contentPager.setCurrentItem(3);
				pager_position = 3;
				changePosition();
			}
		});
	}
	
	private void changePosition() {
		homtBtn.setBackgroundResource(R.drawable.normal);
		baobeiBtn.setBackgroundResource(R.drawable.normal);
		tradeBtn.setBackgroundResource(R.drawable.normal);
		moreBtn.setBackgroundResource(R.drawable.normal);
		switch (pager_position) {
		case 0:
			title.setText("钻展透视");
			homtBtn.setBackgroundResource(R.drawable.btn_press);
			break;
		case 1:
			title.setText("宝贝分析");
			baobeiBtn.setBackgroundResource(R.drawable.btn_press);
			break;
		case 2:
			title.setText("订单详情");
			tradeBtn.setBackgroundResource(R.drawable.btn_press);
			break;
		case 3:
			title.setText("更多");
			moreBtn.setBackgroundResource(R.drawable.btn_press);
			break;
		}
	}

	private void initCostom() {
		contentPager = (ViewPager) findViewById(R.id.content_layout);
		homtBtn = (Button) findViewById(R.id.home_btn);
		baobeiBtn = (Button) findViewById(R.id.baobei_btn);
		tradeBtn = (Button) findViewById(R.id.trade_btn);
		moreBtn = (Button) findViewById(R.id.more_btn);
		title = (TextView) findViewById(R.id.title);
		msg_tv = (TextView) findViewById(R.id.msg_tv);
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
		DownImage.initCache(ContentActivity.this);
	}
	
	private static Boolean isExit = false;
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//监听系统的返回按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//调用双击退出函数
			exitBy2Click();
		}
		return false;
	}
	
	private void exitBy2Click() {  
	    Timer tExit = null;  
	    if (isExit == false) {  
	        isExit = true; // 准备退出  
	        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
	        tExit = new Timer();  
	        tExit.schedule(new TimerTask() {  
	            @Override  
	            public void run() {  
	                isExit = false; // 取消退出  
	            }  
	        }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  
	  
	    } else {  
	        finish();
	        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	    }  
	}
}
