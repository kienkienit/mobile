package com.app.shopfee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.model.Order;
import com.app.shopfee.utils.DateTimeUtils;

import java.util.List;

public class RevenueAdapter extends RecyclerView.Adapter<RevenueAdapter.RevenueViewHolder> {

    private List<Order> orderList;

    public RevenueAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public RevenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_revenue_item, parent, false);
        return new RevenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RevenueViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (order != null) {
            holder.tvOrderId.setText("Mã đơn hàng: " + order.getId());
            holder.tvUserEmail.setText("Email: " + order.getUserEmail());
            holder.tvDateTime.setText("Ngày đặt: " + DateTimeUtils.convertTimeStampToDate(Long.parseLong(order.getDateTime())));
            holder.tvTotalPrice.setText("Tổng giá: " + order.getTotal() + "000 VND");
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

    public static class RevenueViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderId;
        private final TextView tvUserEmail;
        private final TextView tvDateTime;
        private final TextView tvTotalPrice;

        public RevenueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
        }
    }
}
