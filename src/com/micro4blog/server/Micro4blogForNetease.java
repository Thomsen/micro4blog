package com.micro4blog.server;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.micro4blog.Micro4blog;
import com.micro4blog.activity.HomeTimelineActivity;
import com.micro4blog.data.Micro4blogInfo;
import com.micro4blog.data.UserInfo;
import com.micro4blog.dialog.DialogError;
import com.micro4blog.dialog.Micro4blogDialogListener;
import com.micro4blog.http.Micro4blogParameters;
import com.micro4blog.http.HttpUtility;
import com.micro4blog.utils.AsyncMicro4blogRunner.RequestListener;
import com.micro4blog.utils.Micro4blogException;

public class Micro4blogForNetease extends Micro4blog {
	
	private static final String TAG = "Micro4blogForNetease";
	
	private static Micro4blog m4bNetease = new Micro4blogForNetease();
	
	public Micro4blogForNetease() {
		super();
	}
	
	public synchronized static Micro4blog getInstance() {		
		return m4bNetease;
	}

	@Override
	protected void initConfig() {
		
		setAppKey("5V20v8ORzD8ie78k");
		setAppSecret("O3iJyOQM5WQZD7tJjew7bpbHpQYt8VKy");
		
		setRedirectUrl("http://github.com/thomsen/Micro4blog");

		setUrlRequestToken("http://api.t.163.com/oauth/request_token");
		setUrlAccessToken("http://api.t.163.com/oauth/access_token");
		setUrlAccessAuthorize("http://api.t.163.com/oauth/authenticate");

//		setUrlAccessAuthorize("https://api.t.163.com/oauth2/authorize");
//		setUrlAccessToken("https://api.t.163.com/oauth2/access_token");
		
		setServerUrl("http://api.t.163.com/");
		
	}

	@Override
	protected void authorize(Activity activity, String[] permissions,
			int activityCode, Micro4blogDialogListener listener) {
		
		mAuthDialogListener = listener;
		
		startDialogAuth(activity, permissions);
		
	}

	@Override
	protected void startDialogAuth(Activity activity, String[] permissions) {
		
		// 针对permissions，进行对参数设置
		Micro4blogParameters params = new Micro4blogParameters();
		
		dialog(activity, params, new Micro4blogDialogListener() {

			@Override
			public void onComplete(Bundle values) {
				
				// ensure any cookies set by the dialog are saved
                CookieSyncManager.getInstance().sync();
                
                // oauth第三步，换取access token				
				getUserAccessToken(values);			
                
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
		
		// oauth第一步，获取request token
		getAppRequestToken(context, parameters);
		
		parameters.add("client_type", "mobile");
		
		// oauth第二步，进行用户的授权认证
		getAuthorization(context, parameters, listener);
	
	}

	
	@Override
	protected void authorizeCallBack(int requestCode, int resultCode,
			Intent data) {
			
	}
	
	
	
	private void setMicro4blogList(String message,
			ArrayList<Micro4blogInfo> m4bInfoList) {
		try {
//			JSONObject jsonObject = new JSONObject(message);
//			JSONArray m4bArray = jsonObject.getJSONArray("statuses");
			JSONArray m4bArray = new JSONArray(message);
			JSONObject m4bObject;
			Micro4blogInfo m4bInfo;
			UserInfo userInfo;
			for (int i=0; i<m4bArray.length(); i++) {
				m4bObject = (JSONObject) m4bArray.get(i);

				userInfo = new UserInfo();
				m4bInfo = new Micro4blogInfo();
				
				setMicro4blogInfo(m4bObject, m4bInfo);		
				
				setRetweetMicro4blogInfo(m4bObject, m4bInfo);
				
				setUserInfo(m4bObject, m4bInfo, userInfo);
			
				m4bInfoList.add(m4bInfo);	
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void setMicro4blogInfo(JSONObject m4bObject, Micro4blogInfo m4bInfo) {
		
		try {
			m4bInfo.setM4bCreateAt(m4bObject.getString("created_at"));
//			m4bInfo.setM4bId(m4bObject.getInt("id"));
			m4bInfo.setM4bStrId(m4bObject.getString("id"));
			m4bInfo.setM4bText(m4bObject.getString("text"));
			m4bInfo.setM4bSource(m4bObject.getString("source"));
			m4bInfo.setM4bFovorited(m4bObject.getBoolean("favorited"));
			m4bInfo.setM4bTruncated(m4bObject.getBoolean("truncated"));
//			m4bInfo.setM4bInReplyToStatusId(m4bObject.getInt("in_replay_to_status_id"));
//			m4bInfo.setM4bInReplyToUserId(m4bObject.getInt("in_replay_to_user_id"));
//			m4bInfo.setM4bInReplyToScreenName(m4bObject.getString("in_reply_to_screen_name"));
//			m4bInfo.setM4bMid(m4bObject.getInt("mid"));
//			m4bInfo.setM4bMiddlePicture(m4bObject.getString("bmiddle_pic"));
//			m4bInfo.setM4bOriginPicture(m4bObject.getString("original_pic"));
//			m4bInfo.setM4bThumbnailPic(m4bObject.getString("thumbnail_pic"));
//		
//			m4bInfo.setM4bForwardingCount(m4bObject.getInt("reposts_count"));
			m4bInfo.setM4bRetweetCount(m4bObject.getInt("retweet_count"));
			
			m4bInfo.setM4bCommentCount(m4bObject.getInt("comments_count"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private JSONObject setUserInfo(JSONObject m4bObject,
			Micro4blogInfo m4bInfo, UserInfo userInfo) throws JSONException {
		JSONObject userObject;
		userObject = m4bObject.getJSONObject("user");

		userInfo.setUserId(userObject.getLong("id"));
		userInfo.setUserName(userObject.getString("name"));
		userInfo.setUserName(userObject.getString("screen_name"));
//		userInfo.setProvince(userObject.getInt("province"));
//		userInfo.setCity(userObject.getInt("city"));
		userInfo.setLocation(userObject.getString("location"));
		userInfo.setDescription(userObject.getString("description"));
		userInfo.setBlogUrl(userObject.getString("url"));
		userInfo.setProfileImageUrl(userObject.getString("profile_image_url"));
//		userInfo.setDomain(userObject.getString("domain"));
		userInfo.setGender(userObject.getString("gender"));
		userInfo.setFollowersCount(userObject.getInt("followers_count"));
		userInfo.setFriendsCount(userObject.getInt("friends_count"));
		userInfo.setM4bCount(userObject.getInt("statuses_count"));
		userInfo.setFavouritesCount(userObject.getInt("favourites_count"));
		userInfo.setCreateAt(userObject.getString("created_at"));
//		userInfo.setFollowing(userObject.getBoolean("following"));
//		userInfo.setAllowAllActMsg(userObject.getBoolean("allow_all_act_msg"));
		userInfo.setGeoEnabled(userObject.getBoolean("geo_enable"));
		userInfo.setVerified(userObject.getBoolean("verified"));
//		userInfo.setAllowAllComment(userObject.getBoolean("allow_all_comment"));
//		userInfo.setAvatarLarge(userObject.getString("avatar_large"));
//		userInfo.setVerifiedReason(userObject.getString("verified_reason"));
//		userInfo.setFollowMe(userObject.getBoolean("follow_me"));
//		userInfo.setOnlineStatus(userObject.getInt("online_status"));
//		userInfo.setBiFollowersCount(userObject.getInt("bi_followers_count"));
						
		m4bInfo.setUserInfo(userInfo);
		return userObject;
	}

	@Override
	public String getHomeTimeline(Context context) {
		
		apiUrl = getServerUrl() + "statuses/home_timeline.json";
		
		// 解决带参数的未授权问题
		apiParameters.add("count", "16");

		apiRunner.request(apiHeader, apiUrl, apiParameters, HttpUtility.HTTPMETHOD_GET, new RequestListener() {

			@Override
			public void onComplete(String response) {
				
				apiResult = response;
				
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(Micro4blogException e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		return apiResult;
	}

	@Override
	public ArrayList<Micro4blogInfo> parseHomeTimeline(String message) {
		ArrayList<Micro4blogInfo> m4bInfoList = new ArrayList<Micro4blogInfo>();
		if(message == null) {
			return m4bInfoList;
		}
		
		setMicro4blogList(message, m4bInfoList);
				
		return m4bInfoList;
	}

	@Override
	public String update(String status, String lon, String lat) {
		
		apiUrl = getServerUrl() + "statuses/update.json";
		
		apiParameters.add("status", status);
				
		apiRunner.request(apiHeader, apiUrl, apiParameters, HttpUtility.HTTPMETHOD_POST, new RequestListener() {

			@Override
			public void onComplete(String response) {
				Toast.makeText(mContext, "send success", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(mContext, HomeTimelineActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mContext.startActivity(intent);
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(Micro4blogException e) {
				// TODO Auto-generated method stub
				
			}
			
		});

		return apiResult;
	}

	@Override
	public boolean destroy(String strId) {
		
		apiUrl = getServerUrl() + "statuses/destroy/" + strId + ".json";
		
		apiRunner.request(apiHeader, apiUrl, apiParameters, HttpUtility.HTTPMETHOD_DELETE, null);
		
		return false;
	}
	


}
