package com.micro4blog.activity;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.micro4blog.R;
import com.micro4blog.http.Micro4blogParameters;
import com.micro4blog.http.Utility;
import com.micro4blog.oauth.Micro4blog;
import com.micro4blog.utils.AsyncMicro4blogRunner;
import com.micro4blog.utils.Micro4blogException;

public class HomeTimelineActivity extends TimelineActivity implements AsyncMicro4blogRunner.RequestListener {
	
	private Activity mThis;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		mThis = this;
		
		
		
		Micro4blog m4b = Micro4blog.getInstance(Micro4blog.getCurrentServer());
		Micro4blogParameters m4bParams = new Micro4blogParameters();
		
//				
//		String result = "";
//		try {
//			result = m4b.request(this, "https://api.weibo.com/2/statuses/home_timeline.json", m4bParams, Utility.HTTPMETHOD_GET, null);
//		} catch (Micro4blogException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		String url = m4b.getServerUrl() + "statuses/home_timeline.json";
		
		AsyncMicro4blogRunner Micro4blogRunner = new AsyncMicro4blogRunner(m4b);
		Micro4blogRunner.request(mThis, url, m4bParams, Utility.HTTPMETHOD_POST, this);
		
//		Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onComplete(String response) {
		runOnUiThread(new Runnable() {

            public void run() {
                Toast.makeText(mThis, R.string.send_sucess, Toast.LENGTH_LONG).show();
            }
        });

        this.finish();
		
	}

	@Override
	public void onIOException(IOException e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Micro4blogException e) {
		// TODO Auto-generated method stub
		
	}

}