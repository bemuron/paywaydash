package com.payway.dashboard.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.HashMap;

/*
*This class maintains session data across the app using shared prefs.
* We store a boolean flag isLoggedIn in shared prefs to check the login status
*
 */
public class SessionManager {
	// LogCat tag
	private static String TAG = SessionManager.class.getSimpleName();

	// Shared Preferences
	SharedPreferences pref;

	Editor editor;
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Shared preferences file name
	private static final String PREF_NAME = "dashPref";

	private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static final String TAG_TOKEN = "tagtoken";
    private static final String USER_TOKEN = "user_token";

    private static final String CUSTOM_TOKEN = "custom_token";

	public SessionManager(Context context) {
		this._context = context;
        this.pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.editor = pref.edit();
	}

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        return  pref.getString(TAG_TOKEN, null);
    }

    //get the custom token used for firebase auth
    public String getCustomToken(){
        return  pref.getString(CUSTOM_TOKEN, null);
    }

    //save the user authentication token
    public boolean saveUserToken(String token){
        editor.putString(USER_TOKEN, token);
        editor.apply();
        return true;
    }
}
