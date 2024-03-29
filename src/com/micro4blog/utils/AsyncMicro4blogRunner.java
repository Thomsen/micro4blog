package com.micro4blog.utils;

import java.io.IOException;

import android.content.Context;

import com.micro4blog.Micro4blog;
import com.micro4blog.http.HttpHeaderFactory;
import com.micro4blog.http.Micro4blogParameters;

/**
 * 
 * 封装Api请求时需要的授权参数
 *
 */
public class AsyncMicro4blogRunner {
	
	private Micro4blog micro4blog;
	
	public AsyncMicro4blogRunner(Micro4blog micro4blog){
		this.micro4blog = micro4blog;
	}
	
	public void request(final Context context, 
			final String url, 
			final Micro4blogParameters params, 
			final String httpMethod, 
			final RequestListener listener){
		new Thread(){
			@Override public void run() {
                String resp = micro4blog.request(context, url, params, httpMethod, micro4blog.getAccessToken());
				
                if (listener != null) {
                	listener.onComplete(resp);
                }
            }
		}.run();
		
	}
	
	public void request(final HttpHeaderFactory header,
			final String url, 
			final Micro4blogParameters params, 
			final String httpMethod, 
			final RequestListener listener){
		new Thread(){
			@Override public void run() {
                String resp = micro4blog.request(header, url, params, httpMethod, micro4blog.getAccessToken());
				
                if (listener != null) {
                	listener.onComplete(resp);
                }
            }
		}.run();
		
	}
	
	
    public static interface RequestListener {

        public void onComplete(String response);

        public void onIOException(IOException e);

        public void onError(Micro4blogException e);

    }

	
}
