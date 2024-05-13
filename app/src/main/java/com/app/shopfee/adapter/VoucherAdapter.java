package com.app.shopfee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.model.Voucher;
import com.app.shopfee.utils.StringUtil;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    private Context context;
    private final List<Voucher> listVoucher;
    private final int amount;
    private final IClickVoucherListener iClickVoucherListener;

    public interface IClickVoucherListener {
        void onClickVoucherItem(Voucher voucher);
    }

    public VoucherAdapter(Context context, List<Voucher> list, int amount, IClickVoucherListener listener) {
        this.context = context;
        this.listVoucher = list;
        this.amount = amount;
        this.iClickVoucherListener = listener;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = listVoucher.get(position);
        if (voucher == null) return;
        holder.tvVoucherTitle.setText(voucher.getTitle());
        holder.tvVoucherMinimum.setText(voucher.getMinimumText());
        if (StringUtil.isEmpty(voucher.getCondition(amount))) {
            holder.tvVoucherCondition.setVisibility(View.GONE);
        } else {
            holder.tvVoucherCondition.setVisibility(View.VISIBLE);
            holder.tvVoucherCondition.setText(voucher.getCondition(amount));
        }

        if (voucher.isVoucherEnable(amount)) {
            holder.imgStatus.setVisibility(View.VISIBLE);
            holder.tvVoucherTitle.setTextColor(ContextCompat.getColor(context,
                    R.color.textColorHeading));
            holder.tvVoucherMinimum.setTextColor(ContextCompat.getColor(context,
                    R.color.textColorSecondary));
        } else {
            holder.imgStatus.setVisibility(View.GONE);
            holder.tvVoucherTitle.setTextColor(ContextCompat.getColor(context,
                    R.color.textColorAccent));
            holder.tvVoucherMinimum.setTextColor(ContextCompat.getColor(context,
                    R.color.textColorAccent));
        }
        if (voucher.isSelected()) {
            holder.imgStatus.setImageResource(R.drawable.ic_item_selected);
        } else {
            holder.imgStatus.setImageResource(R.drawable.ic_item_unselect);
        }

        holder.layoutItem.setOnClickListener(view -> {
            if (!voucher.isVoucherEnable(amount)) return;
            iClickVoucherListener.onClickVoucherItem(voucher);
        });
    }

    @Override
    public int getItemCount() {
        if (listVoucher != null) {
            return listVoucher.size();
        }
        return 0;
    }

    public void release() {
        if (context != null) context = null;
    }

    public static class VoucherViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgStatus;
        private final TextView tvVoucherTitle;
        private final TextView tvVoucherMinimum;
        private final TextView tvVoucherCondition;
        private final LinearLayout layoutItem;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStatus = itemView.findViewById(R.id.img_status);
            tvVoucherTitle = itemView.findViewById(R.id.tv_voucher_title);
            tvVoucherMinimum = itemView.findViewById(R.id.tv_voucher_minimum);
            tvVoucherCondition = itemView.findViewById(R.id.tv_voucher_condition);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
