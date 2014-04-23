package com.koolbao.zuanapp;

import java.util.List;
import java.util.Map;

import com.koolbao.adapter.OrderAdapter;
import com.koolbao.zuanapp.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class OrderActivity extends Activity {
	public static List<Map<String, String>> orderdata = null;
	private Button back_btn;
	private ListView bb_lv;
	private TextView creat_time_tv;
	private TextView pay_time_tv;
	private TextView payment_tv;
	private TextView trade_num_tv;
	
	private String creat_time;
	private String pay_time;
	private String payment;
	private String trade_num;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);

		initCustom();
		initListence();
		initData();
		initTarget();
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

	private void initTarget() {
		creat_time_tv.setText(creat_time);
		pay_time_tv.setText(pay_time);
		payment_tv.setText(payment);
		trade_num_tv.setText(trade_num);
	}

	private void initData() {
		if (orderdata == null) {
			return;
		}
		Intent intent = getIntent();
		creat_time = intent.getStringExtra("creat_time");
		pay_time = intent.getStringExtra("pay_time");
		payment = intent.getStringExtra("payment");
		trade_num = intent.getStringExtra("trade_num");
		OrderAdapter adapter = new OrderAdapter(OrderActivity.this, orderdata);
		bb_lv.setAdapter(adapter);
	}

	private void initListence() {
		// 返回按钮
		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OrderActivity.this.finish();
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		});
	}

	private void initCustom() {
		creat_time_tv = (TextView) findViewById(R.id.creat_time_tv);
		pay_time_tv = (TextView) findViewById(R.id.pay_time_tv);
		payment_tv = (TextView) findViewById(R.id.payment_tv);
		trade_num_tv = (TextView) findViewById(R.id.trade_num_tv);
		back_btn = (Button) findViewById(R.id.back_btn);
		bb_lv = (ListView) findViewById(R.id.bb_lv);
		bb_lv.setDivider(null);
	}
}
