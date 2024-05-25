package com.app.shopfee.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class OrderDetailActivity extends BaseActivity {

    private TextView tvOrderId, tvUserEmail, tvDateTime, tvStatus, tvPaymentMethod, tvTotalPrice, tvAddress;
    private RecyclerView rcvDrinkList;
    private Button btnUpdateStatus;
    private Order mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        getDataIntent();
        initToolbar();
        initUi();
        getOrderDetailFromFirebase();
        initListener();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        mOrder = (Order) bundle.getSerializable(Constant.ORDER_OBJECT);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText(getString(R.string.order_detail_title));
    }

    private void initUi() {
        tvOrderId = findViewById(R.id.tv_order_id);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvDateTime = findViewById(R.id.tv_date_time);
        tvStatus = findViewById(R.id.tv_status);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvAddress = findViewById(R.id.tv_address);
        rcvDrinkList = findViewById(R.id.rcv_drink_list);
        btnUpdateStatus = findViewById(R.id.btn_update_status);

        rcvDrinkList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initListener() {
        btnUpdateStatus.setOnClickListener(view -> {
            if (mOrder == null) return;
            showUpdateStatusDialog();
        });
    }

    private void getOrderDetailFromFirebase() {
        if (mOrder == null) return;

        DatabaseReference orderRef = MyApplication.get(this).getOrderDetailDatabaseReference(mOrder.getId());
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mOrder = snapshot.getValue(Order.class);
                if (mOrder != null) {
                    displayOrderDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(OrderDetailActivity.this, getString(R.string.msg_get_date_error));
            }
        });
    }

    private void displayOrderDetails() {
        tvOrderId.setText("Mã đơn hàng: " + mOrder.getId());
        tvUserEmail.setText("Email người dùng: " + mOrder.getUserEmail());
        tvDateTime.setText("Ngày giờ đặt hàng: " + DateTimeUtils.convertTimeStampToDate(Long.parseLong(mOrder.getDateTime())));
        tvStatus.setText("Trạng thái đơn hàng: " + getOrderStatus(mOrder.getStatus()));
        tvPaymentMethod.setText("Phương thức thanh toán: " + mOrder.getPaymentMethod());
        tvTotalPrice.setText("Tổng giá trị đơn hàng: " + mOrder.getTotal() + "000 VND");
        tvAddress.setText("Địa chỉ giao hàng: " + mOrder.getAddress().getAddress());

        DrinkOrderAdapter adapter = new DrinkOrderAdapter(mOrder.getDrinks());
        rcvDrinkList.setAdapter(adapter);
    }

    private String getOrderStatus(int status) {
        switch (status) {
            case Order.STATUS_NEW:
                return "Mới";
            case Order.STATUS_DOING:
                return "Đang xử lý";
            case Order.STATUS_ARRIVED:
                return "Đã giao";
            case Order.STATUS_COMPLETE:
                return "Hoàn thành";
            default:
                return "Không xác định";
        }
    }

    private void showUpdateStatusDialog() {
        final String[] statuses = {"Mới", "Đang xử lý", "Đã giao", "Hoàn thành"};
        int currentStatusIndex = getStatusIndex(mOrder.getStatus());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cập nhật trạng thái đơn hàng");
        builder.setSingleChoiceItems(statuses, currentStatusIndex, (dialog, which) -> {
            mOrder.setStatus(getStatusFromIndex(which));
        });
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            updateOrderStatus();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void updateOrderStatus() {
        DatabaseReference orderRef = MyApplication.get(this).getOrderDetailDatabaseReference(mOrder.getId());
        orderRef.child("status").setValue(mOrder.getStatus(), (error, ref) -> {
            if (error == null) {
                GlobalFunction.showToastMessage(OrderDetailActivity.this, "Cập nhật trạng thái thành công");
                displayOrderDetails(); // Refresh order details
            } else {
                GlobalFunction.showToastMessage(OrderDetailActivity.this, "Cập nhật trạng thái thất bại");
            }
        });
    }

    private int getStatusIndex(int status) {
        switch (status) {
            case Order.STATUS_NEW:
                return 0;
            case Order.STATUS_DOING:
                return 1;
            case Order.STATUS_ARRIVED:
                return 2;
            case Order.STATUS_COMPLETE:
                return 3;
            default:
                return 0;
        }
    }

    private int getStatusFromIndex(int index) {
        switch (index) {
            case 0:
                return Order.STATUS_NEW;
            case 1:
                return Order.STATUS_DOING;
            case 2:
                return Order.STATUS_ARRIVED;
            case 3:
                return Order.STATUS_COMPLETE;
            default:
                return Order.STATUS_NEW;
        }
    }
}
