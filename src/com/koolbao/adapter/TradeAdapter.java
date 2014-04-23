package com.koolbao.adapter;

import java.util.List;
import java.util.Map;

import com.koolbao.utils.DownImage;
import com.koolbao.utils.HttpTaskUtils;
import com.koolbao.zuanapp.OrderActivity;
import com.koolbao.zuanapp.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TradeAdapter extends BaseAdapter  {
	private List<Map<String, String>> data;
	private Context mContext;
	
	public TradeAdapter(Context context, List<Map<String, String>> data) {
		mContext = context;
		this.data = data;
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {  
            LayoutInflater inflater = LayoutInflater.from(mContext);  
            convertView = inflater.inflate(R.layout.trade_item, null);
        }
		int trade_num = 0;
		TextView creat_time = (TextView) convertView.findViewById(R.id.pat_time);
        TextView pay_time = (TextView) convertView.findViewById(R.id.achieve_time);
        TextView payment = (TextView) convertView.findViewById(R.id.payment);
        TextView nums = (TextView) convertView.findViewById(R.id.num);
        RelativeLayout more = (RelativeLayout)convertView.findViewById(R.id.more);
        RelativeLayout two_bb = (RelativeLayout)convertView.findViewById(R.id.two_order);
        
        Map<String, String> itemData = data.get(position);
        if (!itemData.isEmpty()) {
        	final String creat_time_str = itemData.get("creat_time").substring(5);
        	String pay_time_temp = itemData.get("pay_time");
        	if (pay_time_temp.length() > 0) {
        		pay_time_temp = pay_time_temp.substring(5);
			} else {
				pay_time_temp = "    --    ";
			}
        	final String pay_time_str = pay_time_temp;
        	final String payment_str = itemData.get("payment");
        	creat_time.setText(creat_time_str);
        	pay_time.setText(pay_time_str);
        	payment.setText(payment_str);
        	
        	final List<Map<String, String>> orderlist = HttpTaskUtils.parseDataToListdata(itemData.get("order"));
        	for (Map<String, String> order : orderlist) {
				int order_num = Integer.valueOf(order.get("num"));
				trade_num += order_num;
			}
        	final String trade_num_str = Integer.toString(trade_num);
        	nums.setText(trade_num_str);
        	if (!orderlist.isEmpty()) {
        		Map<String, String> order = orderlist.get(0);
        		String bb_num_str = order.get("num") + "件";
        		String bb_img = order.get("baobei_img");
        		String bb_title_str = order.get("baobei_title");
        		
        		TextView bb_num = (TextView) convertView.findViewById(R.id.bb_num);
        		TextView baobei_title = (TextView) convertView.findViewById(R.id.bb_title);
        		ImageView baobei_pic = (ImageView) convertView.findViewById(R.id.bb_pic);
        		
        		bb_num.setText(bb_num_str);
        		baobei_title.setText(bb_title_str);
        		new DownImage(baobei_pic).execute(bb_img);
        		//子订单数大于1，默认展示2个子订单
        		if (orderlist.size() > 1) {
        			two_bb.setVisibility(RelativeLayout.VISIBLE);
        			Map<String, String> two_order = orderlist.get(1);
            		String two_bb_num_str = two_order.get("num") + "件";
            		String two_bb_img = two_order.get("baobei_img");
            		String two_bb_title_str = two_order.get("baobei_title");
            		
            		TextView two_bb_num = (TextView) convertView.findViewById(R.id.two_bb_num);
            		TextView two_baobei_title = (TextView) convertView.findViewById(R.id.two_bb_title);
            		ImageView two_baobei_pic = (ImageView) convertView.findViewById(R.id.two_bb_pic);
            		
            		two_bb_num.setText(two_bb_num_str);
            		two_baobei_title.setText(two_bb_title_str);
            		new DownImage(two_baobei_pic).execute(two_bb_img);
            		//子订单数大于2，显示更多
            		 if (orderlist.size() > 2) {
            			 more.setVisibility(RelativeLayout.VISIBLE);
            			 more.setOnClickListener(new OnClickListener() {
    						
    						@Override
    						public void onClick(View arg0) {
    							Intent intent = new Intent(mContext, OrderActivity.class);
    							intent.putExtra("creat_time", creat_time_str);
    							intent.putExtra("pay_time", pay_time_str);
    							intent.putExtra("payment", payment_str);
    							intent.putExtra("trade_num", trade_num_str);
    							OrderActivity.orderdata = orderlist;
    							mContext.startActivity(intent);
    						}
    					});
    				} else {
    					more.setVisibility(RelativeLayout.GONE);
    				}
				} else {
					two_bb.setVisibility(RelativeLayout.GONE);
					more.setVisibility(RelativeLayout.GONE);
				}
			}
		}
		return convertView;
	}

}
