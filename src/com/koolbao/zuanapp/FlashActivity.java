package com.koolbao.zuanapp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;

import com.koolbao.utils.HttpTaskUtils;
import com.koolbao.utils.UserInfoUtils;
import com.koolbao.zuanapp.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FlashActivity extends Activity {
	private WebView flash_web;
	private TextView title_tv;
	private RelativeLayout loading_rl;
	private Button back_btn;
	private String date;
	private String column;
	private String title;
	Map<String, Object> data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flash);
		initData();
		initCostom();
		initListence();
		initFlash();
		showFlash();
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
	
	/**
	 * 加载flash页面
	 */
	@SuppressWarnings("unchecked")
	private void showFlash() {
		try {
			String datepartten = "yyyy-MM-dd";
			Date end_date_7 = DateUtils.parseDate(date, datepartten);
			Date start_date_7 = DateUtils.addDays(end_date_7, -6);
			Date end_date_tongqi = DateUtils.addDays(start_date_7, -1);
			Date start_date_tongqi = DateUtils.addDays(end_date_tongqi, -6);
			
			Map<String, String> pq = new  HashMap<String, String>();
			pq.put("shop_id", UserInfoUtils.shop_id);
			pq.put("column", column);
			pq.put("start_date_7", DateFormatUtils.format(start_date_7, datepartten));
			pq.put("end_date_7", DateFormatUtils.format(end_date_7, datepartten));
			pq.put("end_date_tongqi", DateFormatUtils.format(end_date_tongqi, datepartten));
			pq.put("start_date_tongqi", DateFormatUtils.format(start_date_tongqi, datepartten));
			new QuestFlashDataAsyncTask().execute(pq);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initFlash() {
		flash_web.getSettings().setJavaScriptEnabled(true);
		//设置是否启用缩放
//		flash_web.getSettings().setBuiltInZoomControls(true);
		flash_web.loadUrl("file:///android_asset/index.html");
		
		//初始化演示数据
		JSONArray yesterWeek = new JSONArray();
		for (int i = 1; i <= 7; i++) {
			yesterWeek.put(i);
		}
		JSONArray tongqiWeek = new JSONArray();
		for (int i = 1; i <= 7; i++) {
			tongqiWeek.put(i*10);
		}
		JSONArray yesterWeekDate = new JSONArray();
		for (int i = 1; i <= 7; i++) {
			yesterWeekDate.put("11-0" + i);
		}
		data = new HashMap<String, Object>();
		data.put("yesterWeek", yesterWeek);
		data.put("yesterWeekDate", yesterWeekDate);
		data.put("tongqiWeek", tongqiWeek);
		data.put("tongqiWeekDate", yesterWeekDate);
		data.put("demo", "true");
	}

	private void initListence() {
		//返回按钮
		back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FlashActivity.this.finish();
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		});
	}

	private void initData() {
		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		date = intent.getStringExtra("date");
		column = intent.getStringExtra("column");
	}

	private void initCostom() {
		title_tv = (TextView) findViewById(R.id.title_tv);
		title_tv.setText(title);
		
		flash_web = (WebView) findViewById(R.id.webView1);
		back_btn = (Button) findViewById(R.id.back_btn);
		loading_rl = (RelativeLayout) findViewById(R.id.loading_rl);
	}
	
	class QuestFlashDataAsyncTask extends AsyncTask<Map<String, String>, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			loading_rl.setVisibility(ProgressBar.VISIBLE);
		}
		
		@Override
		protected Map<String, Object> doInBackground(Map<String, String>... params) {
			try {
				Thread.sleep(1000);
				return HttpTaskUtils.requestByHttpPost(HttpTaskUtils.DAILY_FLASH, params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			if (result == null) {
				Toast.makeText(FlashActivity.this, "网络链接失败！", Toast.LENGTH_SHORT).show();
			} else {
				Map<String,	JSONArray> table1 = HttpTaskUtils.parseListDataToJSONArray(result.get("tabledata1").toString());
				Map<String, JSONArray> table2 = HttpTaskUtils.parseListDataToJSONArray(result.get("tabledata2").toString());
				data.put("yesterWeek", HttpTaskUtils.parseDataToJS(table1.get("value")));
				data.put("yesterWeekDate", table1.get("date"));
				data.put("tongqiWeek", HttpTaskUtils.parseDataToJS(table2.get("value")));
				data.put("tongqiWeekDate", table2.get("date"));
				data.put("demo", "false");
			}
			JSONArray jspq = new JSONArray();
			jspq.put(data.get("yesterWeek"));
			jspq.put(data.get("tongqiWeek"));
			jspq.put(data.get("yesterWeekDate"));
			jspq.put(data.get("tongqiWeekDate"));
			jspq.put("");
			
			//flash适配屏幕尺寸
			int width = dip2px(flash_web.getWidth()) - 20;
			int height = dip2px(flash_web.getHeight()) - 20;
			flash_web.loadUrl("javascript:updateData('"+ jspq + "','" + width + "','" + height + "','120','16')");
			loading_rl.setVisibility(ProgressBar.GONE);
		}
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public int dip2px(float dpValue) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (dpValue / scale + 0.5f);
	}
}
