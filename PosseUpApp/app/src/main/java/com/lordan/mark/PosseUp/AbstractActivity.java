package com.lordan.mark.PosseUp;

import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mark on 7/18/2015.
 * Abstract activity to set orientation of all Activities to Portrait
 * Every activity is a child of this class, or should be anyway
 */

public abstract class AbstractActivity extends AppCompatActivity {

    public boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    public boolean isValidUsername(String target) {
        return !TextUtils.isEmpty(target) && !target.contains("@") && !target.contains(" ");
    }

    public boolean isValidPassword(String target) {
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(target);

        return matcher.matches();
    }

    protected void signOutOfAccount(){       //logout user completely, remove all login information
        SharedPreferences settings = getSharedPreferences("PosseUpData", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("access_token");
        editor.remove("email");
        editor.remove("username");
        editor.remove("profileImageURL");
        editor.apply();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null){
            LoginManager.getInstance().logOut();
        }
    }
    protected String getCurrentEmail(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("PosseUpData", MODE_PRIVATE);
        if(prefs != null){
            return prefs.getString("email", null);
        }
        return null;
    }
    protected String getCurrentUsername(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("PosseUpData", MODE_PRIVATE);
        if(prefs != null){
            return prefs.getString("username", null);
        }
        return null;
    }

    protected JsonObjectRequest getUserDetails(final VolleyCallback callback) {
        String url = Constants.baseUrl + "api/Account/UserInfo/" + new AzureService().getCurrentUsername(this).replace(" ", "%20");
        return new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
                Log.i("profilefragment", "got response");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("profilefragment", "got error");
                callback.onError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "bearer " + new AzureService().getToken(getApplicationContext()));

                return params;
            }
        };
    }
}
