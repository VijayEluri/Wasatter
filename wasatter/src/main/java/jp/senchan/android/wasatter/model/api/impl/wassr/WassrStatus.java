package jp.senchan.android.wasatter.model.api.impl.wassr;

import java.util.ArrayList;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.model.api.WasatterUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WassrStatus implements WasatterStatus {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String KEY_RID = "rid";
	public static final String KEY_LOGIN_ID = "user_login_id";
	public static final String KEY_USER = "user";
	public static final String KEY_EPOCH = "epoch";
	public static final String KEY_HTML = "html";
	public static final String KEY_FAVORITES = "favorites";
	
	private String mRid;
	private WassrUser mUser;
	private long mEpoch;
	private String mBody;
	private ArrayList<String> mFavorites;
	
	public WassrStatus(JSONObject json) throws JSONException {
		mRid = json.getString(KEY_RID);
		mUser = new WassrUser(json.getJSONObject(KEY_USER), json.getString(KEY_LOGIN_ID));
		mEpoch = json.getLong(KEY_EPOCH);
		mBody = json.getString(KEY_HTML);
		mFavorites = new ArrayList<String>();
		JSONArray favorites = json.getJSONArray(KEY_FAVORITES);
		if (favorites != null) {
			int length = favorites.length();
			for (int i = 0; i < length; i++) {
				mFavorites.add(favorites.getString(i));
			}
		}
	}

 	@Override
	public String getServiceName() {
		return Wasatter.SERVICE_WASSR;
	}

	@Override
	public String getBody() {
		return mBody;
	}

	@Override
	public String getStatusId() {
		return mRid;
	}

	@Override
	public WasatterUser getUser() {
		return mUser;
	}

	@Override
	public long getTime() {
		return mEpoch * 1000;
	}

	@Override
	public boolean isRetweet() {
		return false;
	}

	@Override
	public WasatterUser getRetweetUser() {
		return null;
	}
}
