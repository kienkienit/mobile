package com.app.shopfee.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.adapter.VoucherAdapter;
import com.app.shopfee.event.VoucherSelectedEvent;
import com.app.shopfee.model.Voucher;
import com.app.shopfee.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class VoucherActivity extends BaseActivity {

    private List<Voucher> listVoucher;
    private VoucherAdapter voucherAdapter;
    private int amount;
    private int voucherSelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        getDataIntent();
        initToolbar();
        initUi();
        getListVoucherFromFirebase();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        voucherSelectedId = bundle.getInt(Constant.VOUCHER_ID, 0);
        amount = bundle.getInt(Constant.AMOUNT_VALUE, 0);
    }

    private void initUi() {
        RecyclerView rcvVoucher = findViewById(R.id.rcv_voucher);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvVoucher.setLayoutManager(linearLayoutManager);
        listVoucher = new ArrayList<>();
        voucherAdapter = new VoucherAdapter(this, listVoucher, amount, this::handleClickVoucher);
        rcvVoucher.setAdapter(voucherAdapter);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.title_voucher));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListVoucherFromFirebase() {
        showProgressDialog(true);
        MyApplication.get(this).getVoucherDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showProgressDialog(false);

                        resetListVoucher();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Voucher voucher = dataSnapshot.getValue(Voucher.class);
                            if (voucher != null) {
                                listVoucher.add(0, voucher);
                            }
                        }

                        if (voucherSelectedId > 0 && listVoucher != null && !listVoucher.isEmpty()) {
                            for (Voucher voucher : listVoucher) {
                                if (voucher.getId() == voucherSelectedId) {
                                    voucher.setSelected(true);
                                    break;
                                }
                            }
                        }
                        if (voucherAdapter != null) voucherAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showProgressDialog(false);
                        showToastMessage(getString(R.string.msg_get_date_error));
                    }
                });
    }

    private void resetListVoucher() {
        if (listVoucher != null) {
            listVoucher.clear();
        } else {
            listVoucher = new ArrayList<>();
        }
    }

    private void handleClickVoucher(Voucher voucher) {
        EventBus.getDefault().post(new VoucherSelectedEvent(voucher));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (voucherAdapter != null) voucherAdapter.release();
    }
}
