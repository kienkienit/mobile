package com.app.shopfee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.model.Drink;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlideUtils;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<Drink> listDrink;
    private final IClickCartListener iClickCartListener;

    public interface IClickCartListener {
        void onClickDeleteItem(Drink drink, int position);
        void onClickUpdateItem(Drink drink, int position);
        void onClickEditItem(Drink drink);
    }

    public CartAdapter(List<Drink> list, IClickCartListener listener) {
        this.listDrink = list;
        this.iClickCartListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Drink drink = listDrink.get(position);
        if (drink == null) return;

        GlideUtils.loadUrl(drink.getImage(), holder.imgDrink);
        holder.tvName.setText(drink.getName());
        String strPrice = drink.getPriceOneDrink() + Constant.CURRENCY;
        holder.tvPrice.setText(strPrice);
        holder.tvOption.setText(drink.getOption());
        String strQuantity = "x" + drink.getCount();
        holder.tvQuantity.setText(strQuantity);
        holder.tvCount.setText(String.valueOf(drink.getCount()));

        holder.tvSub.setOnClickListener(v -> {
            String strCount = holder.tvCount.getText().toString();
            int count = Integer.parseInt(strCount);
            if (count <= 1) {
                return;
            }
            int newCount = count - 1;
            holder.tvCount.setText(String.valueOf(newCount));

            int totalPrice = drink.getPriceOneDrink() * newCount;
            drink.setCount(newCount);
            drink.setTotalPrice(totalPrice);

            iClickCartListener.onClickUpdateItem(drink, holder.getAdapterPosition());
        });

        holder.tvAdd.setOnClickListener(v -> {
            int newCount = Integer.parseInt(holder.tvCount.getText().toString()) + 1;
            holder.tvCount.setText(String.valueOf(newCount));

            int totalPrice = drink.getPriceOneDrink() * newCount;
            drink.setCount(newCount);
            drink.setTotalPrice(totalPrice);

            iClickCartListener.onClickUpdateItem(drink, holder.getAdapterPosition());
        });

        holder.imgEdit.setOnClickListener(v -> iClickCartListener.onClickEditItem(drink));
        holder.imgDelete.setOnClickListener(v
                -> iClickCartListener.onClickDeleteItem(drink, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        if (listDrink != null) {
            return listDrink.size();
        }
        return 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgDrink;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvOption;
        private final TextView tvQuantity;
        private final TextView tvSub;
        private final TextView tvCount;
        private final TextView tvAdd;
        private final ImageView imgEdit;
        private final ImageView imgDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDrink = itemView.findViewById(R.id.img_drink);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvOption = itemView.findViewById(R.id.tv_option);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSub = itemView.findViewById(R.id.tv_sub);
            tvAdd = itemView.findViewById(R.id.tv_add);
            tvCount = itemView.findViewById(R.id.tv_count);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}
