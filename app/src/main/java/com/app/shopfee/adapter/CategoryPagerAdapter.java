package com.app.shopfee.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.shopfee.fragment.DrinkFragment;
import com.app.shopfee.model.Category;

import java.util.List;

public class CategoryPagerAdapter extends FragmentStateAdapter {

    private final List<Category> listCategory;

    public CategoryPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Category> list) {
        super(fragmentActivity);
        listCategory = list;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return DrinkFragment.newInstance(listCategory.get(position).getId());
    }

    @Override
    public int getItemCount() {
        if (listCategory != null) return listCategory.size();
        return 0;
    }
}
