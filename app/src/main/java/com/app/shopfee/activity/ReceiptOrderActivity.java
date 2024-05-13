package com.app.shopfee.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.adapter.DrinkOrderAdapter;
import com.app.shopfee.model.Order;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.DateTimeUtils;
import com.app.shopfee.utils.GlobalFunction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ReceiptOrderActivity extends BaseActivity {

    private TextView tvIdTransaction, tvDateTime;
    private RecyclerView rcvDrinks;
    private TextView tvPrice, tvVoucher, tvTotal, tvPaymentMethod;
    private TextView tvName, tvPhone, tvAddress;
    private TextView tvTrackingOrder;

    private long orderId;
    private Order mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_order);

        getDataIntent();
        initToolbar();
        initUi();
        initListener();
        getOrderDetailFromFirebase();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        orderId = bundle.getLong(Constant.ORDER_ID);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText(getString(R.string.label_receipt_order));
    }

    private void initUi() {
        tvIdTransaction = findViewById(R.id.tv_id_transaction);
        tvDateTime = findViewById(R.id.tv_date_time);
        rcvDrinks = findViewById(R.id.rcv_drinks);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvDrinks.setLayoutManager(linearLayoutManager);
        tvPrice = findViewById(R.id.tv_price);
        tvVoucher = findViewById(R.id.tv_voucher);
        tvTotal = findViewById(R.id.tv_total);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvTrackingOrder = findViewById(R.id.tv_tracking_order);
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
    }

    private void initListener() {
        tvTrackingOrder.setOnClickListener(view -> {
            if (mOrder == null) return;
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.ORDER_ID, mOrder.getId());
            GlobalFunction.startActivity(ReceiptOrderActivity.this,
                    TrackingOrderActivity.class, bundle);
            finish();
        });
    }

    private void getOrderDetailFromFirebase() {
        showProgressDialog(true);
        MyApplication.get(this).getOrderDetailDatabaseReference(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showProgressDialog(false);
                        mOrder = snapshot.getValue(Order.class);
                        if (mOrder == null) return;

                        initData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showProgressDialog(false);
                        showToastMessage(getString(R.string.msg_get_date_error));
                    }
                });
    }

    private void initData() {
        tvIdTransaction.setText(String.valueOf(mOrder.getId()));
        tvDateTime.setText(DateTimeUtils.convertTimeStampToDate(Long.parseLong(mOrder.getDateTime())));
        String strPrice = mOrder.getPrice() + Constant.CURRENCY;
        tvPrice.setText(strPrice);
        String strVoucher = "-" + mOrder.getVoucher() + Constant.CURRENCY;
        tvVoucher.setText(strVoucher);
        String strTotal = mOrder.getTotal() + Constant.CURRENCY;
        tvTotal.setText(strTotal);
        tvPaymentMethod.setText(mOrder.getPaymentMethod());
        tvName.setText(mOrder.getAddress().getName());
        tvPhone.setText(mOrder.getAddress().getPhone());
        tvAddress.setText(mOrder.getAddress().getAddress());
        DrinkOrderAdapter adapter = new DrinkOrderAdapter(mOrder.getDrinks());
        rcvDrinks.setAdapter(adapter);
        if (Order.STATUS_COMPLETE == mOrder.getStatus()) {
            tvTrackingOrder.setVisibility(View.GONE);
        } else {
            tvTrackingOrder.setVisibility(View.VISIBLE);
        }
    }
}
