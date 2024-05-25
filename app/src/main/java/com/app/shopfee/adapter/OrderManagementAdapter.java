package com.app.shopfee.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.activity.OrderDetailActivity;
import com.app.shopfee.model.Order;
import com.app.shopfee.utils.Constant;

import java.util.List;

public class OrderManagementAdapter extends RecyclerView.Adapter<OrderManagementAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;

    public OrderManagementAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (order != null) {
            holder.tvOrderId.setText("Mã đơn hàng: " + order.getId());
            holder.tvUserEmail.setText("Email: " + order.getUserEmail());
            holder.tvDateTime.setText("Ngày đặt: " + order.getDateTime());
            holder.tvStatus.setText("Trạng thái: " + getOrderStatus(order.getStatus()));
            holder.tvTotalPrice.setText("Tổng giá: " + order.getTotal() + "000 VND");

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra(Constant.ORDER_OBJECT, order);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateList(List<Order> newOrderList) {
        orderList = newOrderList;
        notifyDataSetChanged();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderId;
        private final TextView tvUserEmail;
        private final TextView tvDateTime;
        private final TextView tvStatus;
        private final TextView tvTotalPrice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
        }
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
}
