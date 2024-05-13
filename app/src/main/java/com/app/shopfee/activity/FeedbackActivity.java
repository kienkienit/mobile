package com.app.shopfee.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.model.Feedback;
import com.app.shopfee.prefs.DataStoreManager;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.StringUtil;

public class FeedbackActivity extends BaseActivity {

    private EditText edtName, edtPhone, edtEmail, edtComment;
    private TextView tvSendFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initToolbar();
        initUi();
        initData();
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText(getString(R.string.feedback));
    }

    private void initUi() {
        edtName = findViewById(R.id.edt_name);
        edtPhone = findViewById(R.id.edt_phone);
        edtEmail = findViewById(R.id.edt_email);
        edtComment = findViewById(R.id.edt_comment);
        tvSendFeedback = findViewById(R.id.tv_send_feedback);
    }

    private void initData() {
        edtEmail.setText(DataStoreManager.getUser().getEmail());
        tvSendFeedback.setOnClickListener(v -> onClickSendFeedback());
    }

    private void onClickSendFeedback() {
        String strName = edtName.getText().toString();
        String strPhone = edtPhone.getText().toString();
        String strEmail = edtEmail.getText().toString();
        String strComment = edtComment.getText().toString();

        if (StringUtil.isEmpty(strName)) {
            GlobalFunction.showToastMessage(this, getString(R.string.name_require));
        } else if (StringUtil.isEmpty(strComment)) {
            GlobalFunction.showToastMessage(this, getString(R.string.comment_require));
        } else {
            showProgressDialog(true);
            Feedback feedback = new Feedback(strName, strPhone, strEmail, strComment);
            MyApplication.get(this).getFeedbackDatabaseReference()
                    .child(String.valueOf(System.currentTimeMillis()))
                    .setValue(feedback, (databaseError, databaseReference) -> {
                        showProgressDialog(false);
                        sendFeedbackSuccess();
                    });
        }
    }

    public void sendFeedbackSuccess() {
        GlobalFunction.hideSoftKeyboard(this);
        GlobalFunction.showToastMessage(this, getString(R.string.msg_send_feedback_success));
        edtName.setText("");
        edtPhone.setText("");
        edtComment.setText("");
    }
}
