package com.micro4blog.oauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Micro4blogForNetease extends Micro4blog {

	@Override
	protected void initConfig() {
		
		setAppKey("5V20v8ORzD8ie78k");
		setAppSecret("O3iJyOQM5WQZD7tJjew7bpbHpQYt8VKy");
		
//		setRedirectUrl("micro4blog://TimelineActivity");
		setRedirectUrl("http://github.com/thomsen/Micro4blog");

		setUrlRequestToken("http://api.t.163.com/oauth/request_token");
		setUrlAccessToken("http://api.t.163.com/oauth/access_token");
		setUrlAccessAuthorize("http://api.t.163.com/oauth/authenticate");
		
		
		
	}

	@Override
	protected void authorize(Activity activity, String[] permissions,
			int activityCode, Micro4blogDialogListener listener) {

		Utility.setAuthorization(new RequestTokenHeader());
		
		mAuthDialogListener = listener;
		
		startDialogAuth(activity, permissions);
		
	}

	@Override
	protected void startDialogAuth(Activity activity, String[] permissions) {
		
		Micro4blogParameters params = new Micro4blogParameters();
		
		dialog(activity, params, new Micro4blogDialogListener() {

			@Override
			public void onComplete(Bundle values) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(DialogError error) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMicro4blogException(Micro4blogException e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}

	@Override
	protected void dialog(Context context, Micro4blogParameters parameters,
			Micro4blogDialogListener listener) {

		HttpHeaderFactory hhp = new RequestTokenHeader();
		String result = "";
		
		try {
			
			hhp.getMicro4blogAuthHeader(this, "GET", getUrlRequestToken(), parameters, getAppKey(), getAppSecret(), accessToken);
		
			Micro4blogParameters params = hhp.getAuthParams();
			
			result = request(context, getUrlRequestToken(), params, "GET", accessToken);
		} catch (Micro4blogException e) {
			e.printStackTrace();
		}
		
		OauthToken requestToken = new OauthToken(result);
		
		if (requestToken.getTokenOauthOrAccess() != null) {
			parameters.add("oauth_token", requestToken.getTokenOauthOrAccess());
		}
		
		Utility.setAuthorization(new AccessTokenHeader());
		
		String url = getUrlAccessAuthorize() + "?" + Utility.encodeUrl(parameters);
		
		new Micro4blogDialog(this, context, url, listener).show();
	}

	@Override
	protected void authorizeCallBack(int requestCode, int resultCode,
			Intent data) {
		// TODO Auto-generated method stub
		
	}

}
