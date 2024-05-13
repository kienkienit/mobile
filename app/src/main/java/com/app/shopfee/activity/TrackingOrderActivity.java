package com.app.shopfee.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.adapter.DrinkOrderAdapter;
import com.app.shopfee.database.DrinkDatabase;
import com.app.shopfee.event.DisplayCartEvent;
import com.app.shopfee.event.OrderSuccessEvent;
import com.app.shopfee.model.Order;
import com.app.shopfee.model.RatingReview;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.DateTimeUtils;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.NotificationHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class TrackingOrderActivity extends BaseActivity {

    private RecyclerView rcvDrinks;
    private LinearLayout layoutReceiptOrder;
    private View dividerStep1, dividerStep2;
    private ImageView imgStep1, imgStep2, imgStep3;
    private TextView tvTakeOrder, tvTakeOrderMessage;

    private long orderId;
    private Order mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);

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
        tvToolbarTitle.setText(getString(R.string.label_tracking_order));
    }

    private void initUi() {
        rcvDrinks = findViewById(R.id.rcv_drinks);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvDrinks.setLayoutManager(linearLayoutManager);
        layoutReceiptOrder = findViewById(R.id.layout_receipt_order);
        dividerStep1 = findViewById(R.id.divider_step_1);
        dividerStep2 = findViewById(R.id.divider_step_2);
        imgStep1 = findViewById(R.id.img_step_1);
        imgStep2 = findViewById(R.id.img_step_2);
        imgStep3 = findViewById(R.id.img_step_3);
        tvTakeOrder = findViewById(R.id.tv_take_order);
        tvTakeOrderMessage = findViewById(R.id.tv_take_order_message);
    }

    private void initListener() {
        layoutReceiptOrder.setOnClickListener(view -> {
            if (mOrder == null) return;
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.ORDER_ID, mOrder.getId());
            GlobalFunction.startActivity(TrackingOrderActivity.this,
                    ReceiptOrderActivity.class, bundle);
            finish();
        });

        imgStep1.setOnClickListener(view -> updateStatusOrder(Order.STATUS_NEW));
        imgStep2.setOnClickListener(view -> updateStatusOrder(Order.STATUS_DOING));
        imgStep3.setOnClickListener(view -> updateStatusOrder(Order.STATUS_ARRIVED));
        tvTakeOrder.setOnClickListener(view -> updateStatusOrder(Order.STATUS_COMPLETE));
    }

    private void getOrderDetailFromFirebase() {
        showProgressDialog(true);
        MyApplication.get(this).getOrderDetailDatabaseReference(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showProgressDialog(false);
//                        mOrder = snapshot.getValue(Order.class);
                        Order updatedOrder = snapshot.getValue(Order.class);
                        if (updatedOrder == null) return;
//                        initData();
                        if (mOrder == null || mOrder.getStatus() != updatedOrder.getStatus()) {
                            mOrder = updatedOrder;
                            initData();
                            sendNotificationBasedOnStatus(updatedOrder.getStatus());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showProgressDialog(false);
                        showToastMessage(getString(R.string.msg_get_date_error));
                    }
                });
    }

    private void sendNotificationBasedOnStatus(int status) {
        switch (status) {
            case Order.STATUS_NEW:
                NotificationHelper.sendNotification(this, "Đơn hàng mới", "Đơn hàng của bạn đã được tiếp nhận.");
                break;
            case Order.STATUS_DOING:
                NotificationHelper.sendNotification(this, "Đơn hàng đang xử lý", "Chúng tôi đang chuẩn bị món của bạn.");
                break;
            case Order.STATUS_ARRIVED:
                NotificationHelper.sendNotification(this, "Đơn hàng đã sẵn sàng", "Đơn hàng của bạn đã sẵn sàng để nhận.");
                break;
            case Order.STATUS_COMPLETE:
                NotificationHelper.sendNotification(this, "Đơn hàng hoàn thành", "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.");
                handleOrderComplete();
                break;
        }
    }

    private void initData() {
        DrinkOrderAdapter adapter = new DrinkOrderAdapter(mOrder.getDrinks());
        rcvDrinks.setAdapter(adapter);

        switch (mOrder.getStatus()) {
            case Order.STATUS_NEW:
                imgStep1.setImageResource(R.drawable.ic_step_enable);
                dividerStep1.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep2.setImageResource(R.drawable.ic_step_disable);
                dividerStep2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                imgStep3.setImageResource(R.drawable.ic_step_disable);

                tvTakeOrder.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                tvTakeOrderMessage.setVisibility(View.GONE);
                break;

            case Order.STATUS_DOING:
                imgStep1.setImageResource(R.drawable.ic_step_enable);
                dividerStep1.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep2.setImageResource(R.drawable.ic_step_enable);
                dividerStep2.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep3.setImageResource(R.drawable.ic_step_disable);

                tvTakeOrder.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                tvTakeOrderMessage.setVisibility(View.GONE);
                break;

            case Order.STATUS_ARRIVED:
                imgStep1.setImageResource(R.drawable.ic_step_enable);
                dividerStep1.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep2.setImageResource(R.drawable.ic_step_enable);
                dividerStep2.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep3.setImageResource(R.drawable.ic_step_enable);

                tvTakeOrder.setBackgroundResource(R.drawable.bg_button_enable_corner_16);
                tvTakeOrderMessage.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateStatusOrder(int status) {
        if (mOrder == null) return;
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);

        MyApplication.get(this).getOrderDatabaseReference()
                .child(String.valueOf(mOrder.getId()))
                .updateChildren(map, (error, ref) -> {
                    if (error != null){
                        showToastMessage("Cập nhật trạng thái thất bại: " + error.getMessage());
                        return;
                    }
                    mOrder.setStatus(status);
                    initData();
                    if (status == Order.STATUS_COMPLETE) {
                        handleOrderComplete();
                    }
        });
    }
    private void handleOrderComplete() {
        Bundle bundle = new Bundle();
        RatingReview ratingReview = new RatingReview(RatingReview.TYPE_RATING_REVIEW_ORDER, String.valueOf(mOrder.getId()));
        bundle.putSerializable(Constant.RATING_REVIEW_OBJECT, ratingReview);
        GlobalFunction.startActivity(TrackingOrderActivity.this, RatingReviewActivity.class, bundle);
        finish();
    }
}
