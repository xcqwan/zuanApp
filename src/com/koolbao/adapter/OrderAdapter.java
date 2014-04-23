package com.koolbao.adapter;

import java.util.List;
import java.util.Map;

import com.koolbao.utils.DownImage;
import com.koolbao.zuanapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderAdapter extends BaseAdapter {
	private List<Map<String, String>> data;
	private Context mContext;
	
	public OrderAdapter(Context context, List<Map<String, String>> data) {
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
            convertView = inflater.inflate(R.layout.order_item, null);
        }
		Map<String, String> order = data.get(position);
		if (order != null && !order.isEmpty()) {
			String bb_num_str = order.get("num") + "件";
    		String bb_img = order.get("baobei_img");
    		String bb_title_str = order.get("baobei_title");
    		
    		TextView bb_num = (TextView) convertView.findViewById(R.id.bb_num);
    		TextView baobei_title = (TextView) convertView.findViewById(R.id.bb_title);
    		ImageView baobei_pic = (ImageView) convertView.findViewById(R.id.bb_pic);
    		
    		bb_num.setText(bb_num_str);
    		baobei_title.setText(bb_title_str);
    		new DownImage(baobei_pic).execute(bb_img);
		}
		
		return convertView;
	}
}
