package com.app.shopfee.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.app.shopfee.R;
import com.app.shopfee.activity.MainActivity;
import com.app.shopfee.adapter.OrderPagerAdapter;
import com.app.shopfee.model.TabOrder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private View mView;
    private ViewPager2 viewPagerOrder;
    private TabLayout tabOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_history, container, false);

        initToolbar();
        initUi();
        displayTabsOrder();

        return mView;
    }

    private void initToolbar() {
        ImageView imgToolbarBack = mView.findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = mView.findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> backToHomeScreen());
        tvToolbarTitle.setText(getString(R.string.nav_history));
    }

    private void backToHomeScreen() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) return;
        mainActivity.getViewPager2().setCurrentItem(0);
    }

    private void initUi() {
        viewPagerOrder = mView.findViewById(R.id.view_pager_order);
        viewPagerOrder.setUserInputEnabled(false);
        tabOrder = mView.findViewById(R.id.tab_order);
    }

    private void displayTabsOrder() {
        List<TabOrder> list = new ArrayList<>();
        list.add(new TabOrder(TabOrder.TAB_ORDER_PROCESS, getString(R.string.label_process)));
        list.add(new TabOrder(TabOrder.TAB_ORDER_DONE, getString(R.string.label_done)));
        if (getActivity() == null) return;
        viewPagerOrder.setOffscreenPageLimit(list.size());
        OrderPagerAdapter adapter = new OrderPagerAdapter(getActivity(), list);
        viewPagerOrder.setAdapter(adapter);
        new TabLayoutMediator(tabOrder, viewPagerOrder,
                (tab, position) -> tab.setText(list.get(position).getName().toLowerCase()))
                .attach();
    }
}
