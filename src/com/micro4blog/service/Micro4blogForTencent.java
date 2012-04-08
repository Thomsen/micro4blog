package com.micro4blog.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.micro4blog.dialog.DialogError;
import com.micro4blog.dialog.Micro4blogDialog;
import com.micro4blog.dialog.Micro4blogDialogListener;
import com.micro4blog.http.AccessTokenHeader;
import com.micro4blog.http.HttpHeaderFactory;
import com.micro4blog.http.Micro4blogParameters;
import com.micro4blog.http.RequestTokenHeader;
import com.micro4blog.http.Utility;
import com.micro4blog.oauth.Micro4blog;
import com.micro4blog.oauth.OauthToken;
import com.micro4blog.oauth.RequestToken;
import com.micro4blog.utils.Micro4blogException;

public class Micro4blogForTencent extends Micro4blog {
	
	private static Micro4blogForTencent m4bTencent;
	
	public Micro4blogForTencent() {
		super();
	}
	
	public synchronized static Micro4blogForTencent getInstance() {
		if (m4bTencent == null) {
			m4bTencent = new Micro4blogForTencent();
		}
		
		return m4bTencent;
	}

	@Override
	protected void initConfig() {

		setAppKey("801111016");
		setAppSecret("77f11f15151a8b85b15044bca6c2d2ed");
		
		// 要设置callback url，并在manifest中配置
		setRedirectUrl("micro4blog://TimelineActivity");

		// 为了在dialog显示， 原来是https 换成了http 记得要改post为get，反之亦是
		// 针对dialog的callback
		setUrlRequestToken("http://open.t.qq.com/cgi-bin/request_token");
		setUrlAccessToken("http://open.t.qq.com/cgi-bin/access_token");
		setUrlAccessAuthorize("http://open.t.qq.com/cgi-bin/authorize");

	}

	@Override
	protected void authorize(Activity activity, String[] permissions,
			int activityCode, Micro4blogDialogListener listener) {

		// 中间出现的一切关于token错误，都是因为这里的头设置错误
		// Utility.setAuthorization(new AccessTokenHeader());

		// 通过Utility中的setHeader可以设置头部，即得到parameters
		// generateSignatureList的使用
		// setHeader中调用getMicro4blogAuthHeader，
		// generateMicro4blogAuthHeader中会使用到generateSignatureList
		// 那么setHeader在哪里调用了呢？
		// openUrl通信中
		
		Utility.setAuthorization(new RequestTokenHeader());
		
		mAuthDialogListener = listener;

		startDialogAuth(activity, permissions);
	}

	@Override
	protected void startDialogAuth(Activity activity, String[] permissions) {

		Micro4blogParameters params = new Micro4blogParameters();
		
		CookieSyncManager.createInstance(activity);

		dialog(activity, params, new Micro4blogDialogListener() {

			@Override
			public void onComplete(Bundle values) {

				CookieSyncManager.getInstance().sync();

				if (null == accessToken) {
					accessToken = new OauthToken();
				}

//				accessToken.setTokenOauthOrAccess(values.getString(TOKEN));
				accessToken.setOauthToken(values.getString("oauth_token"));

				if (isSessionValid()) {
					mAuthDialogListener.onComplete(values);
				} else {
					mAuthDialogListener
							.onMicro4blogException(new Micro4blogException(
									"Failed to receive access token."));
				}

			}

			@Override
			public void onError(DialogError error) {

			}

			@Override
			public void onCancel() {

			}

			@Override
			public void onMicro4blogException(Micro4blogException e) {

			}

		});

	}

	@Override
	protected void dialog(Context context, Micro4blogParameters parameters,
			Micro4blogDialogListener listener) {
		
		// listener该如何使用
		// listener.onComplete，可以使用在调用该方法时必须实现的接口方法

		// parameters.add("aouth_callback", null);
		// parameters.add("oauth_consumer_key", getAppKey());

		// String url = getUrlRequestToken() + "?" +
		// Utility.encodeUrl(parameters);

		// 这里不该初始化
		// HttpHeaderFactory hhp = new AccessTokenHeader();
		
		
		HttpHeaderFactory hhp = new RequestTokenHeader();
		String result = "";
		
		try {
			
			hhp.getMicro4blogAuthHeader(this, Utility.HTTPMETHOD_GET, getUrlRequestToken(), parameters, getAppKey(), getAppSecret(), accessToken);
		
			Micro4blogParameters params = hhp.getAuthParams();
			
			result = request(context, getUrlRequestToken(), params, Utility.HTTPMETHOD_GET, accessToken);
		} catch (Micro4blogException e) {
			e.printStackTrace();
		}
		
		RequestToken requestToken = new RequestToken(result);
				

		// 1、通过request url和请求参数得到request token
		// 2、通过request token和oauthorize url得到回调url，从中得到verifier code
		// 第二步，是通过url中提供的用户登录授权后得到verifier code
		// 3、通过request token、verifier code加上之前参数通过access url得到access token
		// 中间传递的token都是以oauth_token传递
		
		// 通信的过程都在Utility的openUrl中
		
		// 是通过这个方法来得到正确的url请求
		// 通过generateAccessToken只是设置parameters， 返回的结果用于测试
		// 到了这里全局主要的问题就是得到该有的parameters
		
		
		if (requestToken.getOauthToken() != null) {
			parameters.add("oauth_token", requestToken.getOauthToken());
		}
		
		Utility.setAuthorization(new AccessTokenHeader());
		
	
		String url = getUrlAccessAuthorize() + "?" + Utility.encodeUrl(parameters);
		
		Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
		
		new Micro4blogDialog(this, context, url, listener).show();
		

	}

	@Override
	protected void authorizeCallBack(int requestCode, int resultCode,
			Intent data) {

	}

}
