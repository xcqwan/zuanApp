package com.koolbao.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserInfoUtils {
	public static final String FileName = "userinfo";
	public static final String CompleteName = "complete";
	public static String shop_id = null;
	public static String user_id = null;
	public static String user_nick = null;
	public static String password = null;
	public static String start_date = null;
	public static String end_date = null;
	public static String item_code = null;
	public static String shop_pic = null;
	public static String store_title = null;
	
	public static void init(SharedPreferences sharedPerfeencesr) {
		shop_id = sharedPerfeencesr.getString("store_id", "63335554");
		user_nick = sharedPerfeencesr.getString("visitor_nick", "演示帐号");
		end_date = sharedPerfeencesr.getString("end_date", "2014-02-28");
		shop_pic = sharedPerfeencesr.getString("pic_path", "/a3/6b/T1jH9eXbJnXXaCwpjX");
		store_title = sharedPerfeencesr.getString("store_title", "酷宝数据测试店铺");
	}
	
	public static void saveToShared(SharedPreferences sharedPerfeencesr, Map<String, Object> data) {
		Editor edit = sharedPerfeencesr.edit();
		for (String key : data.keySet()) {
			edit.putString(key, data.get(key).toString());
		}
		edit.commit();
	}
	
	public static boolean getVisitFlag(SharedPreferences sharedPerfeencesr) {
		return sharedPerfeencesr.getBoolean("first_visit_flag", true);
	}
	
	public static void setVisitFlag(SharedPreferences sharedPerfeencesr, boolean flag) {
		Editor edit = sharedPerfeencesr.edit();
		edit.putBoolean("first_visit_flag", flag);
		edit.commit();
	}

	public static boolean getLoginFlag(SharedPreferences sharedPreferences) {
		return sharedPreferences.getBoolean("login_flag", false);
	}
	
	public static void setLoginFlag(SharedPreferences sharedPerfeencesr, boolean flag) {
		Editor edit = sharedPerfeencesr.edit();
		edit.putBoolean("login_flag", flag);
		edit.commit();
	}
	
	public static void setCompleteData(SharedPreferences sharedPerfeencesr, Map<String, String> completeData) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (String key : completeData.keySet()) {
			if (!isFirst) {
				sb.append(",");
			}
			isFirst = false;
			sb.append(key + "\n" + completeData.get(key));
		}
		Editor edit = sharedPerfeencesr.edit();
		edit.putString("complete_data", sb.toString());
		edit.commit();
	}
	
	public static Map<String, String> getCompleteData(SharedPreferences sharedPerfeencesr) {
		Map<String, String> result = new HashMap<String, String>();
		String dataStr = sharedPerfeencesr.getString("complete_data", "");
		if (dataStr.isEmpty()) {
			return result;
		}
		String[] splitData = dataStr.split(",");
		for (String user : splitData) {
			String[] spilts = user.split("\n");
			result.put(spilts[0], spilts[1]);
		}
		return result;
	}
}
