package com.app.shopfee.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.model.User;
import com.app.shopfee.adapter.UserAdapter;
import com.app.shopfee.utils.GlobalFunction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerManagementActivity extends BaseActivity {

    private RecyclerView rcvUser;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private EditText edtSearchEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_management);

        initUi();
        initToolbar();
        loadUsersFromFirebase();
        setupSearchListener();
    }

    private void initUi() {
        rcvUser = findViewById(R.id.rcv_users);
        edtSearchEmail = findViewById(R.id.edt_search_email);
        userAdapter = new UserAdapter(userList);
        rcvUser.setLayoutManager(new LinearLayoutManager(this));
        rcvUser.setAdapter(userAdapter);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.user_title));
    }

    private void loadUsersFromFirebase() {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                userList.add(user);
                            }
                        }
                        userAdapter = new UserAdapter(userList);  // Tạo lại adapter để cập nhật dữ liệu
                        rcvUser.setAdapter(userAdapter);  // Đặt lại adapter cho RecyclerView
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        GlobalFunction.showToastMessage(CustomerManagementActivity.this, getString(R.string.msg_load_user_error));
                    }
                });
    }

    private void setupSearchListener() {
        edtSearchEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().toLowerCase().trim();
                Log.d("CustomerManagement", "onTextChanged: " + searchText);
                userAdapter.filter(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
