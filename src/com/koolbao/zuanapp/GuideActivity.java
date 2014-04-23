package com.koolbao.zuanapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.koolbao.utils.UserInfoUtils;
import com.koolbao.zuanapp.R;
import com.umeng.analytics.MobclickAgent;

public class GuideActivity extends Activity {
	private ViewPager viewPager;
	private ArrayList<View> pageViews;
	private ImageView imageView;
	private ImageView[] imageViews;
	private ViewGroup viewPics;
	private LinearLayout viewPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//隐藏底部导航栏，针对自带虚拟按钮的操作系统
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		
		LayoutInflater inflater = getLayoutInflater();
		pageViews = new ArrayList<View>();
		pageViews.add(inflater.inflate(R.layout.guide_page1, null));
		pageViews.add(inflater.inflate(R.layout.guide_page2, null));
		pageViews.add(inflater.inflate(R.layout.guide_page3, null));
		
		imageViews = new ImageView[pageViews.size()];
		viewPics = (ViewGroup) inflater.inflate(R.layout.activity_guide, null);

		// 实例化小圆点的linearLayout和viewpager
		viewPoints = (LinearLayout) viewPics.findViewById(R.id.viewGroup);
		viewPager = (ViewPager) viewPics.findViewById(R.id.guidePages);

		// 添加小圆点的图片
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(GuideActivity.this);
			LayoutParams layout = new LayoutParams(14, 14);
			layout.rightMargin = 15;
			layout.leftMargin = 15;
			// 设置小圆点imageview的参数
			imageView.setLayoutParams(layout);
			// 将小圆点layout添加到数组中
			imageViews[i] = imageView;
			// 默认选中的是第一张图片，此时第一个小圆点是选中状态，其他不是
			if (i == 0) {
				imageViews[i].setBackgroundResource(R.drawable.guide_select);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.guide_unselect);
			}
			// 将imageviews添加到小圆点视图组
			viewPoints.addView(imageViews[i]);
		}
		// 显示滑动图片的视图
		setContentView(viewPics);

		// 设置viewpager的适配器和监听事件
		viewPager.setAdapter(new GuidePageAdapter());
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
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

	private Button.OnClickListener Button_OnClickListener = new Button.OnClickListener() {
		public void onClick(View v) {
			// 设置已经引导
			UserInfoUtils.setVisitFlag(getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE), false);
			// 跳转
			Intent mIntent = new Intent();
			mIntent.setClass(GuideActivity.this, LoginActivity.class);
			GuideActivity.this.startActivity(mIntent);
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			GuideActivity.this.finish();
		}
	};

	class GuidePageAdapter extends PagerAdapter {
		
		// 销毁position位置的界面
		@Override
		public void destroyItem(View v, int position, Object arg2) {
			((ViewPager) v).removeView(pageViews.get(position));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		// 获取当前窗体界面数
		@Override
		public int getCount() {
			return pageViews.size();
		}

		// 初始化position位置的界面
		@Override
		public Object instantiateItem(View v, int position) {
			((ViewPager) v).addView(pageViews.get(position));
			// 最后一页的按钮事件
			if (position == pageViews.size() - 1) {
				Button btn = (Button) v.findViewById(R.id.btn_close_guide);
				btn.setOnClickListener(Button_OnClickListener);
			}
			return pageViews.get(position);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View v, Object arg1) {
			return v == arg1;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[position].setBackgroundResource(R.drawable.guide_select);
				// 不是当前选中的page，其小圆点设置为未选中的状态
				if (position != i) {
					imageViews[i].setBackgroundResource(R.drawable.guide_unselect);
				}
			}
		}
	}
}
