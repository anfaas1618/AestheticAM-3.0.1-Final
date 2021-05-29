package com.mibtech.aesthetic_am.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import com.mibtech.aesthetic_am.R;
import com.mibtech.aesthetic_am.helper.ApiConfig;
import com.mibtech.aesthetic_am.helper.Constant;
import com.mibtech.aesthetic_am.helper.Session;

public class SplashActivity extends Activity {

    Session session;
    Activity activity;
    String res = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApiConfig.transparentStatusAndNavigation(this);
        activity = SplashActivity.this;
        session = new Session(activity);
        session.setIsUpdateSkipped("update_skip", false);

        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {

            switch (data.getPath().split("/")[1]) {
                case "itemdetail": // Handle the item detail deep link
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", data.getPath().split("/")[2]);
                    intent.putExtra(Constant.FROM, "share");
                    intent.putExtra("vpos", 0);
                    startActivity(intent);
                    finish();
                    break;

                case "refer": // Handle the refer deep link
                    if(!session.isUserLoggedIn()){
                        Constant.FRND_CODE = data.getPath().split("/")[2];
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", Constant.FRND_CODE);
                        assert clipboard != null;
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(SplashActivity.this, R.string.refer_code_copied, Toast.LENGTH_LONG).show();

                        Intent referIntent = new Intent(this, LoginActivity.class);
                        referIntent.putExtra(Constant.FROM, "refer");
                        startActivity(referIntent);
                        finish();
                    }
                    else{
                        GetHomeData();
                        Toast.makeText(activity, "You can not use refer code, You have already logged in.", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        } else {

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_splash);

            int SPLASH_TIME_OUT = 500;

            if (!session.getIsFirstTime("is_first_time")) {
                new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this, WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)), SPLASH_TIME_OUT);
            } else {
                GetHomeData();
            }
        }
    }


    public void GetHomeData() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.FROM, "");
        if (ApiConfig.isConnected(this)) {
            Map<String, String> params = new HashMap<>();
            if (session.isUserLoggedIn()) {
                params.put(Constant.USER_ID, session.getData(Constant.ID));
            }
            ApiConfig.RequestToVolley((result, response) -> {
                if (result) {
                    res = response;
                }
                intent.putExtra("json", res);
                startActivity(intent);
                finish();
            }, this, Constant.GET_ALL_DATA_URL, params, false);
        }
    }
}
