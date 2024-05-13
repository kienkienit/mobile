package com.app.shopfee.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.shopfee.R;

public abstract class BaseActivity extends AppCompatActivity {
    protected MaterialDialog progressDialog, alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createProgressDialog();
        createAlertDialog();
    }

    public void createProgressDialog() {
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.msg_waiting_message)
                .progress(true, 0)
                .build();
    }

    public void showProgressDialog(boolean value) {
        if (value) {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setCancelable(false);
            }
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public void createAlertDialog() {
        alertDialog = new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .positiveText(R.string.action_ok)
                .cancelable(false)
                .build();
    }

    public void showAlertDialog(String errorMessage) {
        alertDialog.setContent(errorMessage);
        alertDialog.show();
    }

    public void showAlertDialog(@StringRes int resourceId) {
        alertDialog.setContent(resourceId);
        alertDialog.show();
    }

    public void setCancelProgress(boolean isCancel) {
        if (progressDialog != null) {
            progressDialog.setCancelable(isCancel);
        }
    }

    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}
