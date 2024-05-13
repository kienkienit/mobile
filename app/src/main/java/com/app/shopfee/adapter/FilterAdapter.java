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
import com.app.shopfee.model.Filter;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private Context context;
    private final List<Filter> listFilter;
    private final IClickFilterListener iClickFilterListener;

    public interface IClickFilterListener {
        void onClickFilterItem(Filter filter);
    }

    public FilterAdapter(Context context, List<Filter> list, IClickFilterListener listener) {
        this.context = context;
        this.listFilter = list;
        this.iClickFilterListener = listener;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        Filter filter = listFilter.get(position);
        if (filter == null) return;

        switch (filter.getId()) {
            case Filter.TYPE_FILTER_ALL:
                holder.imgFilter.setImageResource(R.drawable.ic_filter);
                break;

            case Filter.TYPE_FILTER_RATE:
                holder.imgFilter.setImageResource(R.drawable.ic_rate);
                break;

            case Filter.TYPE_FILTER_PRICE:
                holder.imgFilter.setImageResource(R.drawable.ic_price);
                break;

            case Filter.TYPE_FILTER_PROMOTION:
                holder.imgFilter.setImageResource(R.drawable.ic_promotion);
                break;
        }
        holder.tvTitle.setText(filter.getName());

        if (filter.isSelected()) {
            holder.layoutItem.setBackgroundResource(R.drawable.bg_button_enable_corner_16);
            int color = ContextCompat.getColor(context, R.color.white);
            holder.tvTitle.setTextColor(color);
            holder.imgFilter.setColorFilter(color);
        } else {
            holder.layoutItem.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
            int color = ContextCompat.getColor(context, R.color.textColorHeading);
            holder.tvTitle.setTextColor(color);
            holder.imgFilter.setColorFilter(color);
        }

        holder.layoutItem.setOnClickListener(view
                -> iClickFilterListener.onClickFilterItem(filter));
    }

    @Override
    public int getItemCount() {
        if (listFilter != null) {
            return listFilter.size();
        }
        return 0;
    }

    public void release() {
        if (context != null) context = null;
    }

    public static class FilterViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgFilter;
        private final TextView tvTitle;
        private final LinearLayout layoutItem;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFilter = itemView.findViewById(R.id.img_filter);
            tvTitle = itemView.findViewById(R.id.tv_title);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
