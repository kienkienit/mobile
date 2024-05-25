package com.app.shopfee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.model.DrinkOrder;
import com.app.shopfee.model.Order;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlideUtils;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private final List<Order> listOrder;
    private final IClickOrderListener iClickOrderListener;

    public interface IClickOrderListener {
        void onClickTrackingOrder(long orderId);
        void onClickReceiptOrder(Order order);
    }

    public OrderAdapter(Context context, List<Order> list, IClickOrderListener listener) {
        this.context = context;
        this.listOrder = list;
        this.iClickOrderListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = listOrder.get(position);
        if (order == null) return;

        DrinkOrder firstDrinkOrder = order.getDrinks().get(0);
        GlideUtils.loadUrl(firstDrinkOrder.getImage(), holder.imgDrink);
        holder.tvOrderId.setText(String.valueOf(order.getId()));
        String strTotal = order.getTotal() + Constant.CURRENCY;
        holder.tvTotal.setText(strTotal);
        holder.tvDrinksName.setText(order.getListDrinksName());
        String strQuantity = "(" + order.getDrinks().size() + " " + context.getString(R.string.label_item) + ")";
        holder.tvQuantity.setText(strQuantity);

        if (Order.STATUS_COMPLETE == order.getStatus()) {
            holder.tvSuccess.setVisibility(View.VISIBLE);
            holder.tvAction.setText(context.getString(R.string.label_receipt_order));
            holder.layoutReview.setVisibility(View.VISIBLE);
            holder.tvRate.setText(String.valueOf(order.getRate()));
            holder.tvReview.setText(order.getReview());
            holder.layoutAction.setOnClickListener(v ->
                    iClickOrderListener.onClickReceiptOrder(order));
        } else {
            holder.tvSuccess.setVisibility(View.GONE);
            holder.tvAction.setText(context.getString(R.string.label_tracking_order));
            holder.layoutReview.setVisibility(View.GONE);
            holder.layoutAction.setOnClickListener(v ->
                    iClickOrderListener.onClickTrackingOrder(order.getId()));
        }
    }

    @Override
    public int getItemCount() {
        if (listOrder != null) {
            return listOrder.size();
        }
        return 0;
    }

    public void release() {
        if (context != null) context = null;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgDrink;
        private final TextView tvOrderId;
        private final TextView tvTotal;
        private final TextView tvDrinksName;
        private final TextView tvQuantity;
        private final TextView tvSuccess;
        private final LinearLayout layoutAction;
        private final TextView tvAction;
        private final LinearLayout layoutReview;
        private final TextView tvRate;
        private final TextView tvReview;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDrink = itemView.findViewById(R.id.img_drink);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvTotal = itemView.findViewById(R.id.tv_total);
            tvDrinksName = itemView.findViewById(R.id.tv_drinks_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSuccess = itemView.findViewById(R.id.tv_success);
            layoutAction = itemView.findViewById(R.id.layout_action);
            tvAction = itemView.findViewById(R.id.tv_action);
            layoutReview = itemView.findViewById(R.id.layout_review);
            tvRate = itemView.findViewById(R.id.tv_rate);
            tvReview = itemView.findViewById(R.id.tv_review);
        }
    }
}
