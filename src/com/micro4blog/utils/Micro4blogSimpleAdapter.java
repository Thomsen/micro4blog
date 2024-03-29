package com.micro4blog.utils;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.micro4blog.R;
import com.micro4blog.data.Micro4blogInfo;

public class Micro4blogSimpleAdapter extends SimpleAdapter {
	
	private Context mContext;
	private List<? extends Map<String, ?>> mListData;
	

	public Micro4blogSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		
		mContext = context;
		mListData = data;
	}

	@Override
	public int getCount() {

		return super.getCount();
	}

	@Override
	public Object getItem(int position) {

		return super.getItem(position);
	}

	@Override
	public long getItemId(int position) {

		return super.getItemId(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// 优化
		Micro4blogHodler hodler;
		
		if (convertView == null) {
			hodler = new Micro4blogHodler();
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.timeline_list, null);
			
			hodler.userTextView = (TextView) convertView.findViewById(R.id.username_textview);
			hodler.userImageView = (ImageView) convertView.findViewById(R.id.userimage_imageview);
			
			hodler.retweetLinearLayout = (LinearLayout) convertView.findViewById(R.id.timeline_retweet);
		
			convertView.setTag(hodler);
		} else {
			hodler = (Micro4blogHodler) convertView.getTag();
		}
		
		Micro4blogInfo m4bInfo = (Micro4blogInfo) mListData.get(position);
		
		if (m4bInfo.isHasRetweet()) {
			
		} else {
			hodler.retweetLinearLayout.setVisibility(View.GONE);
		}
		
		hodler.userTextView.setText(mListData.get(position).get("username").toString());
		
		// TODO 图片的异步加载
		
		return convertView;
		
//		return super.getView(position, convertView, parent);
		
	}
	
	
	
	
	@Override
	public ViewBinder getViewBinder() {
//		return super.getViewBinder();
		
		return new Micro4blogBinder();
	}


	class Micro4blogHodler {
		TextView userTextView;
		ImageView userImageView;
		
		TextView contentView;
		TextView forwardContentView;
		
		LinearLayout retweetLinearLayout;
	}


	class Micro4blogBinder implements ViewBinder {

		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			
			if (view.getId() == R.id.userimage_imageview) {
				view.setFocusable(false);
				
				view.setBackgroundResource(R.drawable.ic_launcher);
			}
			
//			if (view instanceof WebView) {
//				view.setFocusable(false);
//			}
			
			return false;
		}
		
	}
	
	

}
