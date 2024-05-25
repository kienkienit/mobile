package com.app.shopfee.activity;

<<<<<<< HEAD
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
=======
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

<<<<<<< HEAD
import androidx.annotation.NonNull;

=======
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be
import com.app.shopfee.R;
import com.app.shopfee.model.User;
import com.app.shopfee.prefs.DataStoreManager;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
<<<<<<< HEAD
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
=======
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be

public class LoginActivity extends BaseActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private LinearLayout layoutRegister;
    private TextView tvForgotPassword;
    private boolean isEnableButtonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initUi();
        initListener();
    }

    private void initUi() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        layoutRegister = findViewById(R.id.layout_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
    }

    private void initListener() {
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    edtEmail.setBackgroundResource(R.drawable.bg_white_corner_16_border_main);
                } else {
                    edtEmail.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray);
                }

                String strPassword = edtPassword.getText().toString().trim();
                if (!StringUtil.isEmpty(s.toString()) && !StringUtil.isEmpty(strPassword)) {
                    isEnableButtonLogin = true;
                    btnLogin.setBackgroundResource(R.drawable.bg_button_enable_corner_16);
                } else {
                    isEnableButtonLogin = false;
                    btnLogin.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                }
            }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    edtPassword.setBackgroundResource(R.drawable.bg_white_corner_16_border_main);
                } else {
                    edtPassword.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray);
                }

                String strEmail = edtEmail.getText().toString().trim();
                if (!StringUtil.isEmpty(s.toString()) && !StringUtil.isEmpty(strEmail)) {
                    isEnableButtonLogin = true;
                    btnLogin.setBackgroundResource(R.drawable.bg_button_enable_corner_16);
                } else {
                    isEnableButtonLogin = false;
                    btnLogin.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                }
            }
        });

        layoutRegister.setOnClickListener(
                v -> GlobalFunction.startActivity(this, RegisterActivity.class));

        btnLogin.setOnClickListener(v -> onClickValidateLogin());
        tvForgotPassword.setOnClickListener(
                v -> GlobalFunction.startActivity(this, ForgotPasswordActivity.class));
    }

    private void onClickValidateLogin() {
        if (!isEnableButtonLogin) return;

        String strEmail = edtEmail.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();
        if (StringUtil.isEmpty(strEmail)) {
            showToastMessage(getString(R.string.msg_email_require));
        } else if (StringUtil.isEmpty(strPassword)) {
            showToastMessage(getString(R.string.msg_password_require));
        } else if (!StringUtil.isValidEmail(strEmail)) {
            showToastMessage(getString(R.string.msg_email_invalid));
        } else {
            loginUserFirebase(strEmail, strPassword);
        }
    }

    private void loginUserFirebase(String email, String password) {
        showProgressDialog(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showProgressDialog(false);
                    if (task.isSuccessful()) {
<<<<<<< HEAD
//                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            User userObject = new User(user.getEmail(), password);
                            DataStoreManager.setUser(userObject);
//                            GlobalFunction.startActivity(LoginActivity.this, MainActivity.class);
//                            finishAffinity();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String role = dataSnapshot.child("role").getValue(String.class);
                                    if ("admin".equals(role)) {
                                        GlobalFunction.startActivity(LoginActivity.this, AdminDashboardActivity.class);
                                        finishAffinity();
                                    } else {
                                        GlobalFunction.startActivity(LoginActivity.this, MainActivity.class);
                                        finishAffinity();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    showToastMessage("Error accessing database: " + databaseError.getMessage());
                                    Log.e("FirebaseLogin", "Database error: " + databaseError.getMessage());
                                }
                            });
                        }
                    } else {
                        showToastMessage(getString(R.string.msg_login_error));
                        Log.e("FirebaseLogin", "Login failed: " + task.getException().getMessage());
=======
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            User userObject = new User(user.getEmail(), password);
                            DataStoreManager.setUser(userObject);
                            GlobalFunction.startActivity(LoginActivity.this, MainActivity.class);
                            finishAffinity();
                        }
                    } else {
                        showToastMessage(getString(R.string.msg_login_error));
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be
                    }
                });
    }
}