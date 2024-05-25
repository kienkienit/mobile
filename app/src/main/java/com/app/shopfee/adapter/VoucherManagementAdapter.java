package com.app.shopfee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.model.Voucher;

import java.util.List;

public class VoucherManagementAdapter extends RecyclerView.Adapter<VoucherManagementAdapter.VoucherViewHolder> {

    private List<Voucher> voucherList;
    private Context context;
    private VoucherActionListener listener;

    public VoucherManagementAdapter(List<Voucher> voucherList, Context context, VoucherActionListener listener) {
        this.voucherList = voucherList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher_management, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        if (voucher != null) {
            holder.tvVoucherTitle.setText(voucher.getTitle());
            holder.tvVoucherMinimum.setText(voucher.getMinimumText());

            holder.itemView.setOnClickListener(view -> {
                CharSequence options[] = new CharSequence[]{"Cập nhật", "Xóa"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Tùy chọn");
                builder.setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            listener.onUpdateVoucher(voucher);
                            break;
                        case 1:
                            listener.onDeleteVoucher(voucher);
                            break;
                    }
                });
                builder.show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public void updateList(List<Voucher> newVoucherList) {
        voucherList = newVoucherList;
        notifyDataSetChanged();
    }

    public static class VoucherViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvVoucherTitle;
        private final TextView tvVoucherMinimum;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVoucherTitle = itemView.findViewById(R.id.tv_voucher_title);
            tvVoucherMinimum = itemView.findViewById(R.id.tv_voucher_minimum);
        }
    }

    public interface VoucherActionListener {
        void onUpdateVoucher(Voucher voucher);
        void onDeleteVoucher(Voucher voucher);
    }
}
