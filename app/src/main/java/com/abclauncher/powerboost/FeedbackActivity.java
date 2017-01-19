package com.abclauncher.powerboost;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abclauncher.powerboost.view.MaterialRippleLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private EditText mFeedbackInformation;
    private EditText mFeedbackEmail;
    private MaterialRippleLayout mFeedbackSubmit;
    public static final int RESULT_GET_SCREENSHOT = 2;
    private String mPictureString;
    private ProgressDialog mLoadingProgressDialog;
    private String mImagename;
    private TelephonyManager mTelephonyManager;
    private PackageInfo mPackageInfo;
    private int localVersionCode;
    private String url;
    private TextView mSubmitText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity_layout);
        initView();
    }

    private void initView() {
        findViewById(R.id.back).setOnClickListener(this);
        mFeedbackInformation = (EditText) findViewById(R.id.feedback_info);
        mFeedbackEmail = (EditText) findViewById(R.id.feedback_email);
        mFeedbackSubmit = (MaterialRippleLayout) findViewById(R.id.feedback_submit);
        mSubmitText = (TextView) findViewById(R.id.submit_text);
        mFeedbackInformation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mFeedbackSubmit.setEnabled(true);
                mSubmitText.setTextColor(getResources().getColor(R.color.text_color_white));
                if (TextUtils.isEmpty(mFeedbackInformation.getText())) {
                    mFeedbackSubmit.setEnabled(false);
                    mSubmitText.setTextColor(getResources().getColor(R.color.text_color_light_gray));
                }
            }
        });
        mFeedbackSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mFeedbackInformation.getText())) {
                    return;
                }
                showLoadingProgressDialog();
                postRequest();
            }
        });


        mFeedbackSubmit.setEnabled(false);
        mSubmitText.setTextColor(getResources().getColor(R.color.text_color_light_gray));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case android.R.id.home:
                    finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void postRequest() {
        url = "http://update.abclauncher.com/feedback";
        JSONObject jsonObject = new JSONObject(getParams());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mLoadingProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.menu_feedback_submit_success), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mLoadingProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.menu_feedback_submit_fail), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }

    private Map<String, String> getParams() {
        String submitTime = getSubmitTime();
        Map<String, String> params = new HashMap<String, String>();
        params.put("feedbackInfo", mFeedbackInformation.getText().toString());
        params.put("email", mFeedbackEmail.getText().toString());
        params.put("image", mPictureString);
        params.put("imageName", mImagename);
        params.put("submitTime", submitTime);
        params.put("urlInfo", getUrl());
        params.put("source", "power_boost");
        return params;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == RESULT_GET_SCREENSHOT && resultCode == Activity.RESULT_OK) {
            try {
                if (data != null && data.getData() != null) {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(inputStream, null, options);
                    if (options.outWidth > 360) {
                        options.inSampleSize = Math.round(options.outWidth / 360);
                    }
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inDither = false;
                    options.inJustDecodeBounds = false;
                    inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                    String mPictureName = data.getData().toString();
                    mImagename = mPictureName.substring(mPictureName.length() - 4);
                    Log.d(getClass().getSimpleName(), "onActivityResult: mImagename-->" + mImagename);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] mPictureScale = baos.toByteArray();
                    mPictureString = Base64.encodeToString(mPictureScale, 5);
                } else {
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void showLoadingProgressDialog() {
        if (mLoadingProgressDialog == null) {
            mLoadingProgressDialog = new ProgressDialog(this);
            mLoadingProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            mLoadingProgressDialog.setMessage(getString(R.string.menu_feedback_submit_dialog));
        }
        mLoadingProgressDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedback_submit:
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    private String getSubmitTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String time = formatter.format(curDate);
        return time;
    }

    private String getUrl() {
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        }
        try {
            String imei = mTelephonyManager.getDeviceId();
            if (mPackageInfo == null) {
                mPackageInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            }
            String ver = mPackageInfo.versionName;
            localVersionCode = mPackageInfo.versionCode;
            String facturer = Build.MANUFACTURER;
            String brand = Build.BRAND;
            String lang = Locale.getDefault().getLanguage();
            String country = Locale.getDefault().getCountry();
            double random = Math.random();
            return url + "?imei=" + imei + "&ver=" + ver + "&os=" + localVersionCode +
                    "&facturer=" + facturer + "&brand=" + brand + "&alias=gp&lang=" +
                    lang + "&country=" + country + "&r=" + random;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
