package com.app.shopfee.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.listener.IClickDrinkListener;
import com.app.shopfee.model.Drink;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlideUtils;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {

    private final List<Drink> listDrink;
    private final IClickDrinkListener iClickDrinkListener;

    public DrinkAdapter(List<Drink> list, IClickDrinkListener listener) {
        this.listDrink = list;
        this.iClickDrinkListener = listener;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drink, parent, false);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        Drink drink = listDrink.get(position);
        if (drink == null) return;

        GlideUtils.loadUrl(drink.getImage(), holder.imgDrink);
        holder.tvName.setText(drink.getName());
        holder.tvDescription.setText(drink.getDescription());
        holder.tvRate.setText(String.valueOf(drink.getRate()));

        if (drink.getSale() <= 0) {
            holder.tvPrice.setVisibility(View.GONE);
            String strPrice = drink.getPrice() + Constant.CURRENCY;
            holder.tvPriceSale.setText(strPrice);
        } else {
            holder.tvPrice.setVisibility(View.VISIBLE);

            String strOldPrice = drink.getPrice() + Constant.CURRENCY;
            holder.tvPrice.setText(strOldPrice);
            holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String strRealPrice = drink.getRealPrice() + Constant.CURRENCY;
            holder.tvPriceSale.setText(strRealPrice);
        }

        holder.layoutItem.setOnClickListener(view
                -> iClickDrinkListener.onClickDrinkItem(drink));
    }

    @Override
    public int getItemCount() {
        if (listDrink != null) {
            return listDrink.size();
        }
        return 0;
    }

    public static class DrinkViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgDrink;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvPriceSale;
        private final TextView tvDescription;
        private final TextView tvRate;
        private final LinearLayout layoutItem;

        public DrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDrink = itemView.findViewById(R.id.img_drink);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPriceSale = itemView.findViewById(R.id.tv_price_sale);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvRate = itemView.findViewById(R.id.tv_rate);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
