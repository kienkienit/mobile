package com.app.shopfee.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.adapter.RevenueAdapter;
import com.app.shopfee.model.Order;
import com.app.shopfee.utils.DateTimeUtils;
import com.app.shopfee.utils.ExcelUtils;
import com.app.shopfee.utils.FileUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewRevenueActivity extends BaseActivity {

    private Spinner spinnerFilterTime;
    private RecyclerView rcvRevenueDetails;
    private RevenueAdapter revenueAdapter;
    private List<Order> orderList = new ArrayList<>();
    private List<Order> filteredList = new ArrayList<>();
    private EditText edtStartDate, edtEndDate;
    private LinearLayout layoutDatePicker;
    private Button btnApplyFilter;
    private TextView tvRevenueResult;
    private TextView btnExportPDF;
    private TextView btnExportExcel;
    private TextView btnExportWord;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_revenue);

        checkPermissions();
        initUi();
        initToolbar();
        initListener();
        loadOrdersFromFirebase();
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.revenue_title));
    }

    private void initUi() {
        spinnerFilterTime = findViewById(R.id.spinner_filter_time);
        rcvRevenueDetails = findViewById(R.id.rcv_revenue_details);
        edtStartDate = findViewById(R.id.edt_start_date);
        edtEndDate = findViewById(R.id.edt_end_date);
        layoutDatePicker = findViewById(R.id.layout_date_picker);
        btnApplyFilter = findViewById(R.id.btn_apply_filter);
        tvRevenueResult = findViewById(R.id.tv_revenue_result);
        btnExportPDF = findViewById(R.id.btn_export_pdf);
        btnExportExcel = findViewById(R.id.btn_export_excel);
        btnExportWord = findViewById(R.id.btn_export_word);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.revenue_filter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterTime.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvRevenueDetails.setLayoutManager(linearLayoutManager);
        revenueAdapter = new RevenueAdapter(orderList);
        rcvRevenueDetails.setAdapter(revenueAdapter);
    }

    private void initListener() {
        spinnerFilterTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isCustomRange = position == 6;
                layoutDatePicker.setVisibility(isCustomRange ? View.VISIBLE : View.GONE);
                btnApplyFilter.setVisibility(isCustomRange ? View.VISIBLE : View.GONE);

                if (!isCustomRange) {
                    filterOrders(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        edtStartDate.setOnClickListener(v -> showDatePickerDialog((datePicker, year, month, day) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            edtStartDate.setText(dateFormat.format(calendar.getTime()));
        }));

        edtEndDate.setOnClickListener(v -> showDatePickerDialog((datePicker, year, month, day) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            edtEndDate.setText(dateFormat.format(calendar.getTime()));
        }));

        btnApplyFilter.setOnClickListener(v -> filterOrders(spinnerFilterTime.getSelectedItemPosition()));

        btnExportPDF.setOnClickListener(v -> exportRevenueToPDF());
        btnExportExcel.setOnClickListener(v -> exportRevenueToExcel());
        btnExportWord.setOnClickListener(view -> exportRevenueToWord());
    }

    private void showDatePickerDialog(DatePickerDialog.OnDateSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
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
                        revenueAdapter.updateList(orderList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void filterOrders(int position) {
        filteredList.clear();
        long currentTime = System.currentTimeMillis();
        long startTime = 0;
        long endTime = currentTime;

        switch (position) {
            case 1: // Ngày
                startTime = currentTime - 24 * 60 * 60 * 1000;
                break;
            case 2: // Tuần
                startTime = currentTime - 7 * 24 * 60 * 60 * 1000;
                break;
            case 3: // Tháng
                startTime = getStartOfMonth();
                break;
            case 4: // Quý
                startTime = getStartOfQuarter();
                break;
            case 5: // Năm
                startTime = getStartOfYear();
                break;
            case 6: // Tùy chọn khoảng thời gian
                try {
                    startTime = dateFormat.parse(edtStartDate.getText().toString()).getTime();
                    endTime = dateFormat.parse(edtEndDate.getText().toString()).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }

        for (Order order : orderList) {
            long orderTime = Long.parseLong(order.getDateTime());
            if (orderTime >= startTime && orderTime <= endTime) {
                filteredList.add(order);
            }
        }

        revenueAdapter.updateList(filteredList);
        updateRevenueResult(filteredList);
    }

    private long getStartOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getStartOfQuarter() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int startMonth = currentMonth - (currentMonth % 3);
        calendar.set(Calendar.MONTH, startMonth);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getStartOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private void updateRevenueResult(List<Order> filteredList) {
        int totalRevenue = 0;
        for (Order order : filteredList) {
            totalRevenue += order.getTotal();
        }
        tvRevenueResult.setText("Doanh thu: " + totalRevenue + "000 VND");
    }

    private void exportRevenueToPDF() {
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu để xuất", Toast.LENGTH_SHORT).show();
            return;
        }

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/revenue_report.pdf";
        String title = "Revenue Report";
        List<String[]> data = new ArrayList<>();
        for (Order order : filteredList) {
            data.add(new String[]{
                    sanitizeString(String.valueOf(order.getId())),
                    sanitizeString(order.getUserEmail()),
                    sanitizeString(DateTimeUtils.convertTimeStampToDate(Long.parseLong(order.getDateTime()))),
                    sanitizeString(String.valueOf(order.getTotal() * 1000))
            });
        }

        try {
            FileUtils.createPDF(filePath, title, data);
            Toast.makeText(this, "Xuất file PDF thành công", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Xuất file PDF thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private String sanitizeString(String input) {
        return input == null ? "" : input.trim().replaceAll("[^\\x20-\\x7E]", "");
    }

    private void exportRevenueToWord() {
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu để xuất", Toast.LENGTH_SHORT).show();
            return;
        }

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/revenue_report.docx";
        String title = "Revenue Report";
        List<String[]> data = new ArrayList<>();
        for (Order order : filteredList) {
            data.add(new String[]{
                    sanitizeString(String.valueOf(order.getId())),
                    sanitizeString(order.getUserEmail()),
                    sanitizeString(DateTimeUtils.convertTimeStampToDate(Long.parseLong(order.getDateTime()))),
                    sanitizeString(String.valueOf(order.getTotal()))
            });
        }

        try {
            FileUtils.createWord(filePath, title, data);
            Toast.makeText(this, "Xuất file Word thành công", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Xuất file Word thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportRevenueToExcel() {
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu để xuất", Toast.LENGTH_SHORT).show();
            return;
        }

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/revenue_report.xlsx";
        List<String[]> data = new ArrayList<>();
        for (Order order : filteredList) {
            data.add(new String[]{
                    String.valueOf(order.getId()),
                    order.getUserEmail(),
                    DateTimeUtils.convertTimeStampToDate(Long.parseLong(order.getDateTime())),
                    String.valueOf(order.getTotal() * 1000)
            });
        }

        try {
            ExcelUtils.createExcelFile(filePath, data);
            Toast.makeText(this, "Xuất file Excel thành công", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Xuất file Excel thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
