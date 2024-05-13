package com.app.shopfee.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;

import com.app.shopfee.R;
import com.app.shopfee.prefs.DataStoreManager;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.StringUtil;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(this::goToActivity, 2000);
    }

    private void goToActivity() {
        if (DataStoreManager.getUser() != null
                && !StringUtil.isEmpty(DataStoreManager.getUser().getEmail())) {
            GlobalFunction.startActivity(this, MainActivity.class);
        } else {
            GlobalFunction.startActivity(this, LoginActivity.class);
        }
        finish();
    }
}
