package com.app.shopfee.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.adapter.OrderManagementAdapter;
import com.app.shopfee.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderManagementActivity extends BaseActivity {

    private EditText edtSearchOrder;
    private Spinner spinnerFilterStatus;
    private RecyclerView rcvOrders;
    private OrderManagementAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();
    private List<Order> filteredOrderList = new ArrayList<>();
    private TextView tvStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        initToolbar();
        initUi();
        initListener();
        loadOrdersFromFirebase();
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.order_title));
    }

    private void initUi() {
        edtSearchOrder = findViewById(R.id.edt_search_order);
        spinnerFilterStatus = findViewById(R.id.spinner_filter_status);
        rcvOrders = findViewById(R.id.rcv_orders);
        tvStatistics = findViewById(R.id.tv_statistics);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_status_array, R.layout.spinner_selected_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFilterStatus.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvOrders.setLayoutManager(linearLayoutManager);
        orderAdapter = new OrderManagementAdapter(filteredOrderList, this);
        rcvOrders.setAdapter(orderAdapter);
    }

    private void initListener() {
        edtSearchOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterOrders(charSequence.toString(), spinnerFilterStatus.getSelectedItemPosition());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        spinnerFilterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                filterOrders(edtSearchOrder.getText().toString(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void loadOrdersFromFirebase() {
        MyApplication.get(this).getOrderDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Order order = dataSnapshot.getValue(Order.class);
                            if (order != null) {
                                orderList.add(order);
                            }
                        }
                        filterOrders(edtSearchOrder.getText().toString(), spinnerFilterStatus.getSelectedItemPosition());
                        updateStatistics();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void filterOrders(String query, int statusPosition) {
        filteredOrderList.clear();
        for (Order order : orderList) {
            String userEmail = order.getUserEmail();
            String orderId = String.valueOf(order.getId());
            boolean matchesQuery = (userEmail != null && userEmail.toLowerCase().contains(query.toLowerCase())) ||
                    (orderId != null && orderId.contains(query));
            boolean matchesStatus = (statusPosition == 0) || (order.getStatus() == statusPosition);
            if (matchesQuery && matchesStatus) {
                filteredOrderList.add(order);
            }
        }
        orderAdapter.updateList(filteredOrderList);
    }

    private void updateStatistics() {
        int newOrders = 0, doingOrders = 0, arrivedOrders = 0, completedOrders = 0;
        for (Order order : orderList) {
            switch (order.getStatus()) {
                case Order.STATUS_NEW:
                    newOrders++;
                    break;
                case Order.STATUS_DOING:
                    doingOrders++;
                    break;
                case Order.STATUS_ARRIVED:
                    arrivedOrders++;
                    break;
                case Order.STATUS_COMPLETE:
                    completedOrders++;
                    break;
            }
        }
        String statistics = "Mới: " + newOrders + " | Đang xử lý: " + doingOrders + " | Đã giao: " + arrivedOrders + " | Hoàn thành: " + completedOrders;
        tvStatistics.setText(statistics);
    }
}
