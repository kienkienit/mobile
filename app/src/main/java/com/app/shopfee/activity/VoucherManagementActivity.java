package com.app.shopfee.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.adapter.VoucherManagementAdapter;
import com.app.shopfee.model.Voucher;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VoucherManagementActivity extends BaseActivity implements VoucherManagementAdapter.VoucherActionListener {

    private EditText edtSearchVoucher;
    private RecyclerView rcvVouchers;
    private VoucherManagementAdapter voucherAdapter;
    private List<Voucher> voucherList = new ArrayList<>();
    private TextView tvStatistics;
    private Button btnAddVoucher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_management);

        initToolbar();
        initUi();
        initListener();
        loadVouchersFromFirebase();
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.title_voucher));
    }

    private void initUi() {
        edtSearchVoucher = findViewById(R.id.edt_search_voucher);
        rcvVouchers = findViewById(R.id.rcv_vouchers);
        tvStatistics = findViewById(R.id.tv_statistics);
        btnAddVoucher = findViewById(R.id.btn_add_voucher);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvVouchers.setLayoutManager(linearLayoutManager);
        voucherAdapter = new VoucherManagementAdapter(voucherList, this, this);
        rcvVouchers.setAdapter(voucherAdapter);
    }

    private void initListener() {
        edtSearchVoucher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterVouchers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnAddVoucher.setOnClickListener(view -> showAddVoucherDialog());
    }

    private void loadVouchersFromFirebase() {
        MyApplication.get(this).getVoucherDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        voucherList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Voucher voucher = dataSnapshot.getValue(Voucher.class);
                            if (voucher != null) {
                                voucherList.add(voucher);
                            }
                        }
                        // Sắp xếp voucherList theo giảm dần phần trăm giảm giá
                        Collections.sort(voucherList, (v1, v2) -> Integer.compare(v2.getDiscount(), v1.getDiscount()));
                        voucherAdapter.updateList(voucherList);
                        updateStatistics();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void filterVouchers(String query) {
        List<Voucher> filteredList = new ArrayList<>();
        for (Voucher voucher : voucherList) {
            if (voucher.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(voucher);
            }
        }
        voucherAdapter.updateList(filteredList);
    }

    private void updateStatistics() {
        int totalVouchers = voucherList.size();
        String statistics = "Tổng số voucher: " + totalVouchers;
        tvStatistics.setText(statistics);
    }

    private void showAddVoucherDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_add_voucher, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        EditText edtDiscount = bottomSheetView.findViewById(R.id.edt_discount);
        EditText edtMinimum = bottomSheetView.findViewById(R.id.edt_minimum);
        TextView tvCancel = bottomSheetView.findViewById(R.id.tv_cancel);
        TextView tvAdd = bottomSheetView.findViewById(R.id.tv_add);

        tvCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());

        tvAdd.setOnClickListener(view -> {
            String discountStr = edtDiscount.getText().toString().trim();
            String minimumStr = edtMinimum.getText().toString().trim();

            if (discountStr.isEmpty() || minimumStr.isEmpty()) {
                return;
            }

            int discount = Integer.parseInt(discountStr);
            int minimum = Integer.parseInt(minimumStr);

            // Lấy ID cuối cùng từ Firebase
            MyApplication.get(this).getVoucherDatabaseReference()
                    .orderByKey()
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int lastId = 0;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                lastId = Integer.parseInt(dataSnapshot.getKey());
                            }
                            int newId = lastId + 1;

                            Voucher newVoucher = new Voucher();
                            newVoucher.setId(newId);
                            newVoucher.setDiscount(discount);
                            newVoucher.setMinimum(minimum);

                            MyApplication.get(VoucherManagementActivity.this).getVoucherDatabaseReference()
                                    .child(String.valueOf(newId))
                                    .setValue(newVoucher);

                            bottomSheetDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle possible errors.
                        }
                    });
        });
        bottomSheetDialog.show();
    }

    private void showUpdateVoucherDialog(Voucher voucher) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_add_voucher, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        EditText edtDiscount = bottomSheetView.findViewById(R.id.edt_discount);
        EditText edtMinimum = bottomSheetView.findViewById(R.id.edt_minimum);
        TextView tvCancel = bottomSheetView.findViewById(R.id.tv_cancel);
        TextView tvAdd = bottomSheetView.findViewById(R.id.tv_add);

        edtDiscount.setText(String.valueOf(voucher.getDiscount()));
        edtMinimum.setText(String.valueOf(voucher.getMinimum()));
        tvAdd.setText("Cập nhật");

        tvCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());

        tvAdd.setOnClickListener(view -> {
            String discountStr = edtDiscount.getText().toString().trim();
            String minimumStr = edtMinimum.getText().toString().trim();

            if (discountStr.isEmpty() || minimumStr.isEmpty()) {
                return;
            }

            int discount = Integer.parseInt(discountStr);
            int minimum = Integer.parseInt(minimumStr);

            voucher.setDiscount(discount);
            voucher.setMinimum(minimum);

            MyApplication.get(this).getVoucherDatabaseReference()
                    .child(String.valueOf(voucher.getId()))
                    .setValue(voucher)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            loadVouchersFromFirebase();
                            bottomSheetDialog.dismiss();
                        }
                    });
        });
        bottomSheetDialog.show();
    }

    @Override
    public void onUpdateVoucher(Voucher voucher) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn cập nhật voucher này không?")
                .setPositiveButton("Cập nhật", (dialog, which) -> showUpdateVoucherDialog(voucher))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDeleteVoucher(Voucher voucher) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn xóa voucher này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    MyApplication.get(this).getVoucherDatabaseReference()
                            .child(String.valueOf(voucher.getId()))
                            .removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    loadVouchersFromFirebase();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
