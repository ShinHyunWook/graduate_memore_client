package com.mglab.memore;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 신현욱 on 2017-03-19.
 */

public class SharedPreferencesManager {

    private Context mContext;
    private SharedPreferences pref;

    public SharedPreferencesManager(Context context) {
        this.mContext = context;
    }

    public void setLogin(String email) {
        pref = mContext.getSharedPreferences("LOGIN", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", email);
        //editor.putString("id"----->키값, memberId----->value);
        editor.commit();
    }

    public String getLogin() {
        SharedPreferences pref = mContext.getSharedPreferences("LOGIN", MODE_PRIVATE);
        String result = pref.getString("email", "");

        return result;
    }

    public void Logout(){
        pref = mContext.getSharedPreferences("LOGIN", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("email");
        editor.commit();
    }
}
