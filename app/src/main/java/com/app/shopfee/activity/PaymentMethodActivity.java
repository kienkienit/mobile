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
import com.app.shopfee.adapter.PaymentMethodAdapter;
import com.app.shopfee.event.PaymentMethodSelectedEvent;
import com.app.shopfee.model.PaymentMethod;
import com.app.shopfee.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodActivity extends BaseActivity {

    private List<PaymentMethod> listPaymentMethod;
    private PaymentMethodAdapter paymentMethodAdapter;
    private int paymentMethodSelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        getDataIntent();
        initToolbar();
        initUi();
        getListPaymentMethodFromFirebase();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        paymentMethodSelectedId = bundle.getInt(Constant.PAYMENT_METHOD_ID, 0);
    }

    private void initUi() {
        RecyclerView rcvPaymentMethod = findViewById(R.id.rcv_payment_method);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvPaymentMethod.setLayoutManager(linearLayoutManager);
        listPaymentMethod = new ArrayList<>();
        paymentMethodAdapter = new PaymentMethodAdapter(listPaymentMethod,
                this::handleClickPaymentMethod);
        rcvPaymentMethod.setAdapter(paymentMethodAdapter);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.title_payment_method));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListPaymentMethodFromFirebase() {
        showProgressDialog(true);
        MyApplication.get(this).getPaymentMethodDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showProgressDialog(false);

                        resetListPaymentMethod();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            PaymentMethod paymentMethod = dataSnapshot.getValue(PaymentMethod.class);
                            if (paymentMethod != null) {
                                listPaymentMethod.add(paymentMethod);
                            }
                        }

                        if (paymentMethodSelectedId > 0 && listPaymentMethod != null
                                && !listPaymentMethod.isEmpty()) {
                            for (PaymentMethod paymentMethod : listPaymentMethod) {
                                if (paymentMethod.getId() == paymentMethodSelectedId) {
                                    paymentMethod.setSelected(true);
                                    break;
                                }
                            }
                        }
                        if (paymentMethodAdapter != null) paymentMethodAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showProgressDialog(false);
                        showToastMessage(getString(R.string.msg_get_date_error));
                    }
                });
    }

    private void resetListPaymentMethod() {
        if (listPaymentMethod != null) {
            listPaymentMethod.clear();
        } else {
            listPaymentMethod = new ArrayList<>();
        }
    }

    private void handleClickPaymentMethod(PaymentMethod paymentMethod) {
        EventBus.getDefault().post(new PaymentMethodSelectedEvent(paymentMethod));
        finish();
    }
}
