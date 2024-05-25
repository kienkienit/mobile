package com.app.shopfee.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.shopfee.fragment.OrderFragment;
import com.app.shopfee.model.TabOrder;

import java.util.List;

public class OrderPagerAdapter extends FragmentStateAdapter {

    private final List<TabOrder> listTabOrder;

    public OrderPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<TabOrder> list) {
        super(fragmentActivity);
        listTabOrder = list;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return OrderFragment.newInstance(listTabOrder.get(position).getType());
    }

    @Override
    public int getItemCount() {
        if (listTabOrder != null) return listTabOrder.size();
        return 0;
    }
}
