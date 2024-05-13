package com.app.shopfee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.model.PaymentMethod;
import com.app.shopfee.utils.Constant;

import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder> {

    private final List<PaymentMethod> listPaymentMethod;
    private final IClickPaymentMethodListener iClickPaymentMethodListener;

    public interface IClickPaymentMethodListener {
        void onClickPaymentMethodItem(PaymentMethod paymentMethod);
    }

    public PaymentMethodAdapter(List<PaymentMethod> list, IClickPaymentMethodListener listener) {
        this.listPaymentMethod = list;
        this.iClickPaymentMethodListener = listener;
    }

    @NonNull
    @Override
    public PaymentMethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_method, parent, false);
        return new PaymentMethodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentMethodViewHolder holder, int position) {
        PaymentMethod paymentMethod = listPaymentMethod.get(position);
        if (paymentMethod == null) return;

        switch (paymentMethod.getId()) {
            case Constant.TYPE_GOPAY:
                holder.imgPaymentMethod.setImageResource(R.drawable.ic_gopay);
                break;

            case Constant.TYPE_CREDIT:
                holder.imgPaymentMethod.setImageResource(R.drawable.ic_credit);
                break;

            case Constant.TYPE_BANK:
                holder.imgPaymentMethod.setImageResource(R.drawable.ic_bank);
                break;

            case Constant.TYPE_ZALO_PAY:
                holder.imgPaymentMethod.setImageResource(R.drawable.ic_zalopay);
                break;
        }
        holder.tvName.setText(paymentMethod.getName());
        holder.tvDescription.setText(paymentMethod.getDescription());
        if (paymentMethod.isSelected()) {
            holder.imgStatus.setImageResource(R.drawable.ic_item_selected);
        } else {
            holder.imgStatus.setImageResource(R.drawable.ic_item_unselect);
        }

        holder.layoutItem.setOnClickListener(view
                -> iClickPaymentMethodListener.onClickPaymentMethodItem(paymentMethod));
    }

    @Override
    public int getItemCount() {
        if (listPaymentMethod != null) {
            return listPaymentMethod.size();
        }
        return 0;
    }

    public static class PaymentMethodViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgPaymentMethod;
        private final ImageView imgStatus;
        private final TextView tvName;
        private final TextView tvDescription;
        private final LinearLayout layoutItem;

        public PaymentMethodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPaymentMethod = itemView.findViewById(R.id.img_payment_method);
            imgStatus = itemView.findViewById(R.id.img_status);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
