package com.app.shopfee.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.model.Order;
import com.app.shopfee.prefs.DataStoreManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminDashboardActivity extends BaseActivity{
    private TextView tvOrdersTodayValue, tvRevenueTodayValue, tvProfitTodayValue;
    private ImageView btnManageDrinks, btnManageCustomers, btnManageVouchers, btnViewFeedback, btnManageOrders, btnViewRevenue;
    private LineChart revenueChart;
    private Button btnLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_admin_dashboard);

        initUi();
        loadDashboardData();
        setupButtonListener();
    }

    private void initUi() {
        tvOrdersTodayValue = findViewById(R.id.tv_orders_today_value);
        tvRevenueTodayValue = findViewById(R.id.tv_revenue_today_value);
        tvProfitTodayValue = findViewById(R.id.tv_profit_today_value);

        btnManageDrinks = findViewById(R.id.btn_manage_drinks);
        btnManageCustomers = findViewById(R.id.btn_manage_customers);
        btnManageVouchers = findViewById(R.id.btn_manage_vouchers);
        btnViewFeedback = findViewById(R.id.btn_view_feedback);
        btnManageOrders = findViewById(R.id.btn_manage_orders);
        btnViewRevenue = findViewById(R.id.btn_view_revenue);
        btnLogout = findViewById(R.id.btn_logout);

        revenueChart = findViewById(R.id.revenue_chart);
    }

    private void loadDashboardData() {
        MyApplication.get(this).getOrderDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int ordersToday = 0;
                        int revenueToday = 0;
                        int profitToday = 0;

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        long startOfDay = calendar.getTimeInMillis();

                        // Lấy ngày đầu tiên của tháng hiện tại
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        long startOfMonth = calendar.getTimeInMillis();

                        Map<String, Integer> monthlyRevenue = new HashMap<>();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Order order = dataSnapshot.getValue(Order.class);
                            if (order != null) {
                                long orderTime = Long.parseLong(order.getDateTime());
                                if (orderTime >= startOfDay) {
                                    ordersToday++;
                                    revenueToday += order.getTotal() * 1000;
                                    profitToday = revenueToday / 5;
                                }

                                // Chỉ lấy các đơn hàng trong tháng hiện tại
                                if (orderTime >= startOfMonth) {
                                    String date = dateFormat.format(new Date(orderTime));
                                    if (monthlyRevenue.containsKey(date)) {
                                        monthlyRevenue.put(date, monthlyRevenue.get(date) + order.getTotal() * 1000);
                                    } else {
                                        monthlyRevenue.put(date, order.getTotal() * 1000);
                                    }
                                }
                            }
                        }

                        tvOrdersTodayValue.setText(String.valueOf(ordersToday));
                        tvRevenueTodayValue.setText(String.format("%d VND", revenueToday));
                        tvProfitTodayValue.setText(String.format("%d VND", profitToday));

                        displayRevenueChart(monthlyRevenue);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle possible errors.
                    }
                });
    }

    private void displayRevenueChart(Map<String, Integer> revenueData) {
        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        int index = 0;

        // Sort the revenueData by date keys
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(revenueData.entrySet());
        Collections.sort(sortedEntries, (e1, e2) -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                Date date1 = dateFormat.parse(e1.getKey());
                Date date2 = dateFormat.parse(e2.getKey());
                return date1.compareTo(date2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        });

        for (Map.Entry<String, Integer> entry : sortedEntries) {
            values.add(new Entry(index, entry.getValue()));
            dates.add(entry.getKey());
            index++;
        }

        LineDataSet lineDataSet = new LineDataSet(values, "Revenue");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(lineDataSet);
        revenueChart.setData(lineData);

        XAxis xAxis = revenueChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return dates.get((int) value);
            }
        });

        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(values.size() - 0.5f);

        YAxis leftAxis = revenueChart.getAxisLeft();
        leftAxis.setGranularity(1f);

        YAxis rightAxis = revenueChart.getAxisRight();
        rightAxis.setEnabled(false);

        revenueChart.setDragEnabled(true);
        revenueChart.setScaleEnabled(true);
        revenueChart.getDescription().setEnabled(false);
        revenueChart.setPinchZoom(true);
        revenueChart.invalidate(); // refresh
    }

    private void setupButtonListener() {
        btnManageDrinks.setOnClickListener(view -> openManageDrinks());
        btnManageCustomers.setOnClickListener(view -> openManageCustomers());
        btnManageVouchers.setOnClickListener(view -> openManageVouchers());
        btnViewFeedback.setOnClickListener(view -> openViewFeedback());
        btnManageOrders.setOnClickListener(view -> openManageOrders());
        btnViewRevenue.setOnClickListener(view -> openViewRevenue());
        btnLogout.setOnClickListener(view -> logout());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
//        DataStoreManager.setUser(null);
        DataStoreManager.removeUser();
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openViewRevenue() {
        Intent intent = new Intent(this, ViewRevenueActivity.class);
        startActivity(intent);
    }

    private void openManageOrders() {
        Intent intent = new Intent(this, OrderManagementActivity.class);
        startActivity(intent);
    }

    private void openViewFeedback() {
        Intent intent = new Intent(this, ViewFeedbackActivity.class);
        startActivity(intent);
    }

    private void openManageVouchers() {
        Intent intent = new Intent(this, VoucherManagementActivity.class);
        startActivity(intent);
    }

    private void openManageCustomers() {
        Intent intent = new Intent(this, CustomerManagementActivity.class);
        startActivity(intent);
    }

    private void openManageDrinks() {
        Intent intent = new Intent(this, DrinkManagementActivity.class);
        startActivity(intent);
    }
}
