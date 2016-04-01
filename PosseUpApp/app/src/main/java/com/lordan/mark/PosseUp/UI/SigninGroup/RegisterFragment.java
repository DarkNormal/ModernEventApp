package com.lordan.mark.PosseUp.UI.SigninGroup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.ImageSelectorDialog;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark on 9/30/2015
 */
public class RegisterFragment extends Fragment implements View.OnClickListener{

    private ProgressDialog mProgressDialog; //dialog used for all Volley requests
    private RequestQueue queue;             //Volley queue
    private View view;
    private EditText email;
    private ImageSelectorDialog dialog;
    private static final String TAG ="RegisterFrag";
    private ImageView profiler;
    private Bitmap profileImage;
    private AzureService az;
    private static final int CAMERA_OPTION = 11, GALLERY_OPTION = 12;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.register_layout, container, false);
        profiler = (ImageView) view.findViewById(R.id.profiler);
        if (savedInstanceState != null) {
            profileImage = savedInstanceState.getParcelable("profileImage");
            if(profileImage != null) {
                profiler.setImageBitmap(profileImage);
            }
        }

        email = (EditText) view.findViewById(R.id.email_signup);
        Drawable[] leftDrawable = email.getCompoundDrawables();
        leftDrawable[0].setAlpha(128);
        Button signup = (Button) view.findViewById(R.id.signup_button);
        queue = Volley.newRequestQueue(getContext());       //instantiate Volley queue
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) view.findViewById(R.id.username_register);

                EditText password = (EditText) view.findViewById(R.id.password_signup);

                if (validateDetails(username, email, password)) {
                    registerUser(username, email, password);     //begin registration process
                }

            }
        });
        TextView backToSignIn = (TextView) view.findViewById(R.id.signin_text);
        backToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        FloatingActionButton addImage = (FloatingActionButton) view.findViewById(R.id.addImage_Button);
        addImage.setOnClickListener(this);

        return view;
    }



    private void registerUser(final EditText username, final EditText email, EditText password) {

        //TODO
        //can now convert to JSONObject easier via Gson
        final User newUser = new User(email.getText().toString(), password.getText().toString(), username.getText().toString());
        mProgressDialog = new ProgressDialog(getContext(), R.style.CustomAlertDialogStyle);
        mProgressDialog.setTitle("Registering");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Email", newUser.getEmail());
            jsonBody.put("Password", newUser.getPassword());
            jsonBody.put("Username", newUser.getUsername());
            if(profileImage != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                profileImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                jsonBody.put("ProfileImage", encoded);
                byteArray = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = Constants.baseUrl + "api/Account/Register";
        JsonObjectRequest jrequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                System.out.println("user registered");
                az = new AzureService();
                try {
                    az.saveProfileImage(getContext(), response.getString("profileImage"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                login(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                if (error.networkResponse != null) {
                    System.out.println(error.networkResponse.statusCode);
                }
                String json;

                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    switch (response.statusCode) {
                        case 400:
                            json = new String(response.data);
                            JSONArray jsonarray = trimMessage(json, "Errors");
                            if (jsonarray != null) setErrors(jsonarray, username, email);
                            break;
                        default:
                            Toast.makeText(getContext(), "An unknown error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
                System.out.println("User not registered");
            }
        });
        jrequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jrequest);

    }

    private boolean validateDetails(EditText username, EditText email, EditText password) {
        boolean proceedRegister1, proceedRegister2, proceedRegister3;
        if (!((SigninActivity)getActivity()).isValidUsername(username.getText().toString())) {
            username.setError("Invalid username, cannot be empty and cannot contain @");
            proceedRegister1 = false;
        }
        else proceedRegister1 = true;
        if (!((SigninActivity)getActivity()).isValidEmail(email.getText().toString())) {
            email.setError("Invalid Email");
            proceedRegister2 = false;
        }
        else proceedRegister2 = true;
        if (!((SigninActivity)getActivity()).isValidPassword(password.getText().toString())) {
            password.setError("Mixed case letters and at least 1 digit is required. Length must be > 6");
            proceedRegister3 = false;
        }
        else proceedRegister3 = true;

        return proceedRegister1 && proceedRegister2 && proceedRegister3;
    }

    private void login(final JSONObject jsonBody) {
        mProgressDialog.setTitle("Logging in");
        mProgressDialog.show();
        String url = Constants.baseUrl + "Token";
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject json = new JSONObject();
                System.out.println("Token recieved");
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    AzureService az = new AzureService();
                    az.saveUserData(getContext(), json.getString("access_token"), json.getString("userName"), json.getString("email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mProgressDialog.dismiss();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("No Token recieved");
                mProgressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "password");
                try {
                    params.put("username", jsonBody.getString("Email"));
                    params.put("password", jsonBody.getString("Password"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String httpPostBody = null;
                try {
                    httpPostBody = "grant_type=password&username=" + jsonBody.getString("Username") + "&password=" + jsonBody.getString("Password");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return httpPostBody.getBytes();
            }
        };
        queue.add(req);

    }

    private JSONArray trimMessage(String json, String key) {
        JSONArray jarray;

        try {
            JSONObject obj = new JSONObject(json);
            jarray = obj.getJSONArray(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jarray;
    }

    private void setErrors(JSONArray toastString, EditText username, EditText email) {
        for (int i = 0; i < toastString.length(); i++) {
            try {
                if (toastString.getString(i).startsWith("Name")) {
                    username.setError("Username is taken");
                } else if (toastString.getString(i).startsWith("Email")) {
                    email.setError("Email is taken");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.addImage_Button:
                getProfileImage();
        }
    }

    private void getProfileImage(){
        FragmentManager fm = getFragmentManager();
        dialog = new ImageSelectorDialog();
        dialog.setTargetFragment(this, 0);
        dialog.show(fm, "image_selector");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 0:
                dialog.dismiss();
                if (resultCode == CAMERA_OPTION) {
                    Log.i(TAG, "got the result from the dialog");

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 1);//zero can be replaced with any action code
                } else if (resultCode == GALLERY_OPTION){
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 2);//one can be replaced with any action code
                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    Bundle extras = data.getExtras();
                    profileImage = (Bitmap) extras.get("data");
                    if(profiler != null && profileImage != null) {
                        profiler.setImageBitmap(profileImage);
                    }
                }
                break;

            case 2:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        profileImage = createScaledBitmapKeepingAspectRatio(bitmap,960);
                        bitmap = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(profiler != null && profileImage != null) {
                        profiler.setImageBitmap(profileImage);

                    }
                }
                break;

        }
    }
    private Bitmap createScaledBitmapKeepingAspectRatio(Bitmap bitmap, int maxSide){
        Bitmap scaledBitmap;
        final int maxSize = 960;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        scaledBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
        return scaledBitmap;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        if(profileImage != null) {
            savedInstanceState.putParcelable("profileImage", profileImage);
        }
    }

}


