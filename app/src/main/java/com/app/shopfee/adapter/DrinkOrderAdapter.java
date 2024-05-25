package com.app.shopfee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.model.DrinkOrder;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlideUtils;

import java.util.List;

public class DrinkOrderAdapter extends RecyclerView.Adapter<DrinkOrderAdapter.DrinkOrderViewHolder> {

    private final List<DrinkOrder> listDrinkOrder;

    public DrinkOrderAdapter(List<DrinkOrder> list) {
        this.listDrinkOrder = list;
    }

    @NonNull
    @Override
    public DrinkOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drink_order, parent, false);
        return new DrinkOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkOrderViewHolder holder, int position) {
        DrinkOrder drinkOrder = listDrinkOrder.get(position);
        if (drinkOrder == null) return;

        GlideUtils.loadUrl(drinkOrder.getImage(), holder.imgDrink);
        holder.tvName.setText(drinkOrder.getName());
        String strPrice = drinkOrder.getPrice() + Constant.CURRENCY;
        holder.tvPrice.setText(strPrice);
        holder.tvOption.setText(drinkOrder.getOption());
        String strCount = "x" + drinkOrder.getCount();
        holder.tvCount.setText(strCount);
    }

    @Override
    public int getItemCount() {
        if (listDrinkOrder != null) {
            return listDrinkOrder.size();
        }
        return 0;
    }

    public static class DrinkOrderViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgDrink;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvCount;
        private final TextView tvOption;

        public DrinkOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDrink = itemView.findViewById(R.id.img_drink);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvOption = itemView.findViewById(R.id.tv_option);
        }
    }
}
