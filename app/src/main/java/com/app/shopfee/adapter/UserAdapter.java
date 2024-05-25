package com.app.shopfee.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.model.Order;
import com.app.shopfee.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> originalUserList;
    private List<User> userList;
    private final Map<String, UserOrderSummary> userOrderSummaries = new HashMap<>();

    public UserAdapter(List<User> userList) {
        this.originalUserList = new ArrayList<>(userList);
        this.userList = new ArrayList<>(userList);
        loadOrderSummaries();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        if (user != null) {
            UserOrderSummary summary = userOrderSummaries.get(user.getEmail());
            holder.tvEmail.setText(user.getEmail());
            holder.tvTotalOrders.setText("Tổng số đơn hàng: " + (summary != null ? summary.totalOrders : 0));
            holder.tvCompletedOrders.setText("Đơn hàng đã nhận: " + (summary != null ? summary.completedOrders : 0));
            holder.tvPendingOrders.setText("Đơn hàng chưa nhận: " + (summary != null ? summary.pendingOrders : 0));
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEmail;
        private final TextView tvTotalOrders;
        private final TextView tvCompletedOrders;
        private final TextView tvPendingOrders;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvTotalOrders = itemView.findViewById(R.id.tv_total_orders);
            tvCompletedOrders = itemView.findViewById(R.id.tv_completed_orders);
            tvPendingOrders = itemView.findViewById(R.id.tv_pending_orders);
        }
    }

    private void loadOrderSummaries() {
        FirebaseDatabase.getInstance().getReference("order")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userOrderSummaries.clear();
                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                            Order order = orderSnapshot.getValue(Order.class);
                            if (order != null) {
                                String userEmail = order.getUserEmail();
                                UserOrderSummary summary = userOrderSummaries.get(userEmail);
                                if (summary == null) {
                                    summary = new UserOrderSummary();
                                    userOrderSummaries.put(userEmail, summary);
                                }
                                summary.totalOrders++;
                                if (order.getStatus() == Order.STATUS_COMPLETE) {
                                    summary.completedOrders++;
                                } else if (order.getStatus() < Order.STATUS_COMPLETE) {
                                    summary.pendingOrders++;
                                }
                            }
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle possible errors.
                    }
                });
    }

    public static class UserOrderSummary {
        public int totalOrders;
        public int completedOrders;
        public int pendingOrders;

        public UserOrderSummary() {
            this.totalOrders = 0;
            this.completedOrders = 0;
            this.pendingOrders = 0;
        }
    }

    public void filter(String text) {
        userList = new ArrayList<>();
        if (text.isEmpty()) {
            userList.addAll(originalUserList);
        } else {
            text = text.toLowerCase();
            for (User user : originalUserList) {
                Log.d("UserAdapter", "Checking user: " + user.getEmail().toLowerCase());
                if (user.getEmail().toLowerCase().contains(text)) {
                    userList.add(user);
                }
            }
        }
        Log.d("UserAdapter", "Filtered User List Size: " + userList.size());
        notifyDataSetChanged();
    }
}
