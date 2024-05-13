package com.app.shopfee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.model.Topping;
import com.app.shopfee.utils.Constant;

import java.util.List;

public class ToppingAdapter extends RecyclerView.Adapter<ToppingAdapter.ToppingViewHolder> {

    private final List<Topping> listTopping;
    private final IClickToppingListener iClickToppingListener;

    public interface IClickToppingListener {
        void onClickToppingItem(Topping topping);
    }

    public ToppingAdapter(List<Topping> list, IClickToppingListener listener) {
        this.listTopping = list;
        this.iClickToppingListener = listener;
    }

    @NonNull
    @Override
    public ToppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topping, parent, false);
        return new ToppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToppingViewHolder holder, int position) {
        Topping topping = listTopping.get(position);
        if (topping == null) return;

        holder.tvName.setText(topping.getName());
        String strPrice = "+" + topping.getPrice() + Constant.CURRENCY;
        holder.tvPrice.setText(strPrice);
        holder.chbSelected.setChecked(topping.isSelected());

        holder.chbSelected.setOnCheckedChangeListener((buttonView, isChecked)
                -> iClickToppingListener.onClickToppingItem(topping));
    }

    @Override
    public int getItemCount() {
        if (listTopping != null) {
            return listTopping.size();
        }
        return 0;
    }

    public static class ToppingViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvPrice;
        private final CheckBox chbSelected;

        public ToppingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            chbSelected = itemView.findViewById(R.id.chb_selected);
        }
    }
}
