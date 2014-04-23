package com.koolbao.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpTaskUtils {
	public static final String BASE_URL = "http://zuan.koolbao.com/ajax_wifi/";
	public static final String CHECK_IN = "check_user_nick";
	public static final String DAILY_DATA = "get_wifi_zonglan_table";
	public static final String DAILY_FLASH = "get_wifi_zonglan_flash";
	public static final String BAOBEI_DATA = "get_wifi_baobei_table";
	public static final String BAOBEI_DATA_TOTAL = "get_wifi_baobei_table_total";
	public static final String TRADE_DATA = "get_wifi_order_table";
	public static final String TRADE_DATA_TOTAL = "get_wifi_order_table_total";
	
	public static final String Requst_Fail = "500"; 
	public static final String Requst_Success = "200"; 
	
	/**
	 * HttpPost请求
	 * @param path
	 * 		访问地址
	 * @param param
	 * 		参数
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> requestByHttpPost(String path, Map<String, String> param) throws Exception {
		// 新建HttpPost对象
		HttpPost httpPost = new HttpPost(BASE_URL + path);
		// Post参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String key : param.keySet()) {
			params.add(new BasicNameValuePair(key, param.get(key)));
		}
		// 设置字符集
		HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		// 设置参数实体
		httpPost.setEntity(entity);
		// 获取HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();
		 // 请求超时
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        // 读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		int try_times = 0;
		while (try_times < 3) {
			// 获取HttpResponse实例
			HttpResponse httpResp = httpClient.execute(httpPost);
			// 判断是够请求成功
			if (httpResp.getStatusLine().getStatusCode() == 200) {
				// 获取返回的数据
				HttpEntity respEntity = httpResp.getEntity();
				String resp = EntityUtils.toString(respEntity, "UTF-8");
				JSONObject json = new JSONObject(resp);
				Iterator<String> keys = json.keys();
				Map<String, Object> result = new HashMap<String, Object>();
				while (keys.hasNext()) {
					String key = keys.next();
					result.put(key, json.getString(key));
				}
				return result;
			} else {
				Log.i("test", "HttpPost方式请求失败" + httpResp.getStatusLine().getStatusCode());
				try_times ++;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseTableDataToMap(String json) {
		if (json.equals("[]")) {
			return new HashMap<String, Object>();
		}
		while (json.startsWith("[")) {
			json = json.substring(1, json.length() - 1);
		}
		try {
			JSONObject jsonobj = new JSONObject(json);
			Iterator<String> keys = jsonobj.keys();
			Map<String, Object> result = new HashMap<String, Object>();
			while (keys.hasNext()) {
				String key = keys.next();
				result.put(key, jsonobj.getString(key));
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i("test", "JSON转换MAP失败");
		}
		
		return new HashMap<String, Object>();
	}
	
	public static Map<String, JSONArray> parseListDataToJSONArray(String json) {
		try {
			JSONArray keyArray = new JSONArray();
			JSONArray valueArray = new JSONArray();
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				String item = jsonArray.get(i).toString();
				JSONObject itemobj = new JSONObject(item);
				keyArray.put(itemobj.get("key"));
				valueArray.put(itemobj.get("value"));
			}
			Map<String, JSONArray> result = new HashMap<String, JSONArray>();
			result.put("date", keyArray);
			result.put("value", valueArray);
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i("test", "JSON转换Array失败");
		}
		return new HashMap<String, JSONArray>();
	}
	
	public static JSONArray parseDataToJS(JSONArray array) {
		try {
			JSONArray result = new JSONArray();
			for (int i = 0; i < array.length(); i++) {
				String value = array.getString(i);
				if (value.startsWith("\"")) {
					value = value.substring(1, value.length() - 1);
				}
				result.put(Double.valueOf(value));
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return array;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> parseDataToListdata(String json) {
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		if (json.equals("[]") || !json.startsWith("[")) {
			return result;
		}
		try {
			JSONArray arraylist = new JSONArray(json);
			for (int i = 0; i < arraylist.length(); i++) {
				String itemJson = arraylist.getString(i);
				JSONObject itemObj = new JSONObject(itemJson);
				Iterator<String> keys = itemObj.keys();
				Map<String, String> itemMap = new HashMap<String, String>();
				while (keys.hasNext()) {
					String key = keys.next();
					itemMap.put(key, itemObj.getString(key));
				}
				result.add(itemMap);
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
}
