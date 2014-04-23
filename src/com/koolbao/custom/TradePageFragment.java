package com.koolbao.custom;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.koolbao.adapter.TradeAdapter;
import com.koolbao.loadmore.LoadMoreListView;
import com.koolbao.loadmore.LoadMoreListView.LMListViewListener;
import com.koolbao.utils.HttpTaskUtils;
import com.koolbao.utils.UserInfoUtils;
import com.koolbao.zuanapp.R;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

public class TradePageFragment extends BaseFragment implements LMListViewListener{
	// 页面布局
	private View view;
	private RelativeLayout date_content;
	private TextView date_tv;
	// 页面指标
	private TextView pat;
	private TextView achieve_user;
	private TextView achieve_trade;
	private TextView achieve;
	private LoadMoreListView trade_lv;

	private String before_change;
	int page_count;
	int page_current;
	private TradeAdapter adapter;
	private List<Map<String, String>> trade_data_list;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.trade_page, null);
		initCostom();
		initTradeList();
		initListener();
		changeDate();
		
		return view;
	}
	
	private void initListener() {
		date_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String dateStr = date_tv.getText().toString();
				final WheelDatePicker wheel = new WheelDatePicker(getActivity(), dateStr, date_tv);
				wheel.showAtLocation(getActivity().findViewById(R.id.root), Gravity.BOTTOM, 0, 0);
				wheel.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						if (wheel.handle_type == 1) {
							changeDate();
						}
					}
				});
			}
		});
		
		trade_lv.setLMListViewListener(this);
	}

	private void initTradeList() {
		trade_data_list = new ArrayList<Map<String,String>>();
		if (reqest_msg == null) {
			trade_lv.setPullLoadEnable(false);
			page_count = 1;
			page_current = 1;
		} else {
			trade_data_list = HttpTaskUtils.parseDataToListdata(reqest_msg.get("tabledata").toString());
			page_count = Integer.valueOf(reqest_msg.get("page_count").toString());
			page_current = Integer.valueOf(reqest_msg.get("page_current").toString());
			if (page_count > page_current) {
				trade_lv.setPullLoadEnable(true);
			} else {
				trade_lv.setPullLoadEnable(false);
			}
		}
		adapter = new TradeAdapter(getActivity(), trade_data_list);
		trade_lv.setAdapter(adapter);
	}

	private void initCostom() {
		date_content = (RelativeLayout) view.findViewById(R.id.date_content);
		date_tv = (TextView) view.findViewById(R.id.start_date);
		date_tv.setText(DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd"));
		trade_lv = (LoadMoreListView) view.findViewById(R.id.trade_lv);
		trade_lv.setDivider(null);
		
		pat = (TextView) view.findViewById(R.id.pat);
		achieve_user = (TextView) view.findViewById(R.id.achieve_user);
		achieve_trade = (TextView) view.findViewById(R.id.achieve_trade);
		achieve = (TextView) view.findViewById(R.id.achieve);
	}

	public void changeDate() {
		String afterChange = date_tv.getText().toString();
		if (before_change != null && before_change.equals(afterChange)) {
			return;
		}
		Map<String, String> pq = new HashMap<String, String>();
		pq.put("shop_id", UserInfoUtils.shop_id);
		pq.put("add_time", date_tv.getText().toString());
		//初始化
		syncOperate(HttpTaskUtils.TRADE_DATA_TOTAL, view, new Runnable() {
			
			@Override
			public void run() {
				if (reqest_msg_total != null) {
					Map<String, Object> tabledata = HttpTaskUtils.parseTableDataToMap(reqest_msg_total.get("tabledata").toString());
					if (tabledata.isEmpty()) {
						Toast.makeText(getActivity(), "这一天暂无指标数据", Toast.LENGTH_SHORT).show();
					} else {
						showData(tabledata);
					}
				} else {
					Toast.makeText(getActivity(), "网络连接失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}, pq);
		//初始化订单列表
		pq.put("page", "1");
		syncOperate(HttpTaskUtils.TRADE_DATA, view, new Runnable() {
			
			@Override
			public void run() {
				if (reqest_msg != null) {
					Map<String, Object> tabledata = HttpTaskUtils.parseTableDataToMap(reqest_msg.get("tabledata").toString());
					if (tabledata.isEmpty()) {
						Toast.makeText(getActivity(), "这一天暂无订单数据", Toast.LENGTH_SHORT).show();
					} else {
						initTradeList();
					}
				} else {
					Toast.makeText(getActivity(), "网络连接失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}, pq);
	}

	protected void showData(Map<String, Object> data) {
		pat.setText(data.get("pat_nums").toString());
		achieve_user.setText(data.get("achieve_users").toString());
		achieve_trade.setText(data.get("achieve_trades").toString());
		achieve.setText(data.get("trade_sales").toString());
	}
	
	@Override
	public void onLoadMore() {
		final Map<String, String> pq = new HashMap<String, String>();
		pq.put("shop_id", UserInfoUtils.shop_id);
		pq.put("add_time", date_tv.getText().toString());
		pq.put("page", Integer.toString(page_current + 1));
		//加载数据
		syncOperate(HttpTaskUtils.TRADE_DATA, view, new Runnable() {
			
			@Override
			public void run() {
				if (reqest_msg != null) {
					List<Map<String, String>> data_list = HttpTaskUtils.parseDataToListdata(reqest_msg.get("tabledata").toString());
					for (Map<String, String> item : data_list) {
						trade_data_list.add(item);
					}
					adapter.notifyDataSetChanged();
					onLoad();
					page_count = Integer.valueOf(reqest_msg.get("page_count").toString());
					page_current = Integer.valueOf(reqest_msg.get("page_current").toString());
					if (page_count > page_current) {
						trade_lv.setPullLoadEnable(true);
					} else {
						trade_lv.setPullLoadEnable(false);
					}
				} else {
					Toast.makeText(getActivity(), "网络连接失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}, pq);
	}
	
	private void onLoad() {
		trade_lv.stopLoadMore();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
