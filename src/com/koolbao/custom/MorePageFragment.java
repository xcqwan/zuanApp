package com.koolbao.custom;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.koolbao.utils.DownImage;
import com.koolbao.utils.UserInfoUtils;
import com.koolbao.zuanapp.LoginActivity;
import com.koolbao.zuanapp.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MorePageFragment extends Fragment {
	private View view;
	private RelativeLayout call;
	private Button login_btn;
	private TextView shop_title_tv;
	private TextView end_time_tv;
	private ImageView expired_iv;
	private ImageView shop_pic_iv;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.more_page, null);
		initCustom();
		initListence();
		initShopInfo();
		return view;
	}

	private void initShopInfo() {
		String shop_title = UserInfoUtils.store_title;
		String end_time = "到期时间：" + UserInfoUtils.end_date;
		shop_title_tv.setText(shop_title);
		end_time_tv.setText(end_time);
		
		String end_date = UserInfoUtils.end_date;
		try {
			Date endDate = DateUtils.parseDate(end_date, "yyyy-MM-dd");
			Date today = new Date();
			if (today.after(endDate)) {
				expired_iv.setVisibility(ImageView.VISIBLE);
			} else {
				expired_iv.setVisibility(ImageView.GONE);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String pic_path = UserInfoUtils.shop_pic;
		if (pic_path != null && !pic_path.isEmpty()) {
			new DownImage(shop_pic_iv).execute("http://logo.taobao.com/shop-logo" + pic_path);
		}
	}

	private void initListence() {
		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 传入服务， parse（）解析号码
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:0571-87853991"));
				// 通知activtity处理传入的call服务
				startActivity(intent);
			}
		});
		//注销登录
		login_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UserInfoUtils.setLoginFlag(getActivity().getSharedPreferences(UserInfoUtils.FileName, Activity.MODE_PRIVATE), false);
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				getActivity().finish();
			}
		});
	}

	private void initCustom() {
		call = (RelativeLayout) view.findViewById(R.id.call);
		login_btn = (Button) view.findViewById(R.id.login_btn);
		shop_title_tv = (TextView) view.findViewById(R.id.shop_title);
		end_time_tv = (TextView) view.findViewById(R.id.end_time_tv);
		shop_pic_iv = (ImageView) view.findViewById(R.id.shop_img);
		expired_iv = (ImageView) view.findViewById(R.id.expired_iv);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
