package com.koolbao.zuanapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.koolbao.utils.CustomProgressDialog;
import com.koolbao.utils.HttpTaskUtils;
import com.koolbao.utils.MD5Utils;
import com.koolbao.utils.UserInfoUtils;
import com.koolbao.zuanapp.R;
import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private Button submit_btn;
	private TextView findpsd_tv;
	private TextView newuser_tv;
	private TextView qrcode_btn;
	private AutoCompleteTextView user_nick_et;
	private EditText psd_et;
	private ArrayAdapter<String> adapter;
	
	private Map<String, String> completeData;
	private List<String> completeKey;
	
	private SharedPreferences sharePrefer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		initCostom();
		initListence();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//处理扫描结果（在界面上显示）
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			String[] spScanResult = scanResult.split("\n");
			
			//清空输入框
			user_nick_et.setText("");
			psd_et.setText("");
			if (spScanResult.length < 2) {
				Toast.makeText(this, "您扫描的不是酷宝的登录二维码", Toast.LENGTH_SHORT).show();
				return;
			}
			String user_nick = spScanResult[0];
			String psd = spScanResult[1];
			
			if (user_nick.isEmpty() || psd.isEmpty() || psd.length() != 32) {
				Toast.makeText(this, "您扫描的不是酷宝的登录二维码", Toast.LENGTH_SHORT).show();
				return;
			}
			//填充输入框
			user_nick_et.setText(user_nick);
			psd_et.setText(psd);
			
			Map<String, String> pq = new HashMap<String, String>();
			pq.put("user_nick", user_nick);
			pq.put("password", psd);
			new AuthCheckInAsyncTask().execute(pq);
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

	private void initListence() {
		//登录
		submit_btn.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				String user_nick = user_nick_et.getText().toString();
				String psd = psd_et.getText().toString();
				if (user_nick.isEmpty() || psd.isEmpty()) {
					// 用户名不能为空
					showCompleteTips();
					return;
				}
				Map<String, String> pq = new HashMap<String, String>();
				pq.put("user_nick", user_nick);
				if (psd.length() == 32) {
					//填充的密码为32位
					pq.put("password", psd);
				} else {
					//用户输入的密码需要进行加密
					pq.put("password", getAuthCode(psd));
				}
				new AuthCheckInAsyncTask().execute(pq);
			}
		});
		//忘记密码
		findpsd_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startBrowerView("http://container.api.taobao.com/container?appkey=12463114");
			}
		});
		//新用户订购
		newuser_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startBrowerView("http://fuwu.m.taobao.com/ser/detail.htm?service_code=ts-20181&tracelog=app");
			}
		});
		//二维码扫描登录
		qrcode_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//启动二维码扫描
				startActivityForResult(new Intent(LoginActivity.this, CaptureActivity.class), 0);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		});
		
		//补全选项选中
		user_nick_et.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				String key = adapter.getItem(position);
				if (completeData.containsKey(key)) {
					psd_et.setText(completeData.get(key));
				}
			}
		});
		
		//点击输入框时弹出补全选项
		user_nick_et.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				user_nick_et.showDropDown();
			}
		});
	}
	
	//输入完整提示
	private void showCompleteTips() {
		final AlertDialog tips = new AlertDialog.Builder(LoginActivity.this).create();
		tips.show();
		Window window = tips.getWindow();
		window.setContentView(R.layout.complete_tips);
		//确定
		RelativeLayout sure_rl = (RelativeLayout) window.findViewById(R.id.sure_rl);
		sure_rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tips.cancel();
			}
		});
	}
	
	//错误提示
	private void showTips() {
		final AlertDialog tips = new AlertDialog.Builder(LoginActivity.this).create();
		tips.show();
		Window window = tips.getWindow();
		window.setContentView(R.layout.alert_tips);
		//取消
		RelativeLayout cancal_btn = (RelativeLayout) window.findViewById(R.id.cancal_rl);
		cancal_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tips.cancel();
			}
		});
		//去授权
		RelativeLayout auth_go_btn = (RelativeLayout) window.findViewById(R.id.auth_go_rl);
		auth_go_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startBrowerView("http://container.api.taobao.com/container?appkey=12463114");
			}
		});
	}
	
	//打开浏览器
	private void startBrowerView(String url) {
		Intent intent = new Intent();        
		intent.setAction("android.intent.action.VIEW");    
		Uri content_url = Uri.parse(url);   
		intent.setData(content_url);  
		startActivity(intent);
	}

	//密码加密
	private String getAuthCode(String word) {
		String reverseWord = new StringBuffer(word).reverse().toString();
		return MD5Utils.create32Md5(word + reverseWord);
	}

	private void initCostom() {
		submit_btn = (Button) findViewById(R.id.submit_btn);
		findpsd_tv = (TextView) findViewById(R.id.findpsd);
		newuser_tv = (TextView) findViewById(R.id.newuser);
		user_nick_et = (AutoCompleteTextView) findViewById(R.id.user_nick_et);
		psd_et = (EditText) findViewById(R.id.psd_et);
		qrcode_btn = (TextView) findViewById(R.id.qrcode_btn);
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		
		completeData = UserInfoUtils.getCompleteData(sharePrefer);
		completeKey = new ArrayList<String>();
		for (String key : completeData.keySet()) {
			completeKey.add(key);
		}
		
		adapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, completeKey);
		user_nick_et.setAdapter(adapter);
		user_nick_et.setThreshold(1);
	}
	
	class AuthCheckInAsyncTask extends AsyncTask<Map<String, String>, Void, Map<String, Object>> {
		private Map<String, String> param;
		
		@Override
		protected void onPreExecute() {
			submit_btn.setClickable(false);
			CustomProgressDialog.createDialog(LoginActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(Map<String, String>... params) {
			param = params[0];
			try {
				Thread.sleep(500);
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.CHECK_IN, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			submit_btn.setClickable(true);
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(LoginActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String state = result.get("state").toString();
			if (state.equals(HttpTaskUtils.Requst_Fail)) {
				//错误提示
				showTips();
				return;
			}
			Map<String, Object> user_info = HttpTaskUtils.parseTableDataToMap(result.get("tabledata1").toString());
			UserInfoUtils.saveToShared(LoginActivity.this.getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE), user_info);
			Map<String, Object> user_sub = HttpTaskUtils.parseTableDataToMap(result.get("tabledata2").toString());
			UserInfoUtils.saveToShared(LoginActivity.this.getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE), user_sub);
			
			UserInfoUtils.setLoginFlag(getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE), true);
			
			//输入补全框增加补全用户
			completeData.put(param.get("user_nick"), param.get("password"));
			completeKey.add(param.get("user_nick"));
			UserInfoUtils.setCompleteData(sharePrefer, completeData);
			
			Intent intent = new Intent(LoginActivity.this, ContentActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			finish();
		}
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
