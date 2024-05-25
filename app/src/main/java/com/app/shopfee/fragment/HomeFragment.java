package com.app.shopfee.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.activity.DrinkDetailActivity;
import com.app.shopfee.adapter.BannerViewPagerAdapter;
import com.app.shopfee.adapter.CategoryPagerAdapter;
import com.app.shopfee.event.SearchKeywordEvent;
import com.app.shopfee.listener.IClickDrinkListener;
import com.app.shopfee.model.Category;
import com.app.shopfee.model.Drink;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment {

    private View mView;
    private ViewPager2 viewPagerDrinkFeatured;
    private CircleIndicator3 indicatorDrinkFeatured;
    private ViewPager2 viewPagerCategory;
    private TabLayout tabCategory;
    private EditText edtSearchName;
    private ImageView imgSearch;

    private List<Drink> listDrinkFeatured;
    private List<Category> listCategory;

    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (viewPagerDrinkFeatured == null || listDrinkFeatured == null || listDrinkFeatured.isEmpty()) {
                return;
            }
            if (viewPagerDrinkFeatured.getCurrentItem() == listDrinkFeatured.size() - 1) {
                viewPagerDrinkFeatured.setCurrentItem(0);
                return;
            }
            viewPagerDrinkFeatured.setCurrentItem(viewPagerDrinkFeatured.getCurrentItem() + 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        initUi();
        initListener();

        getListDrinkBanner();
        getListCategory();

        return mView;
    }

    private void initUi() {
        viewPagerDrinkFeatured = mView.findViewById(R.id.view_pager_drink_featured);
        indicatorDrinkFeatured = mView.findViewById(R.id.indicator_drink_featured);
        viewPagerCategory = mView.findViewById(R.id.view_pager_category);
        viewPagerCategory.setUserInputEnabled(false);
        tabCategory = mView.findViewById(R.id.tab_category);
        edtSearchName = mView.findViewById(R.id.edt_search_name);
        imgSearch = mView.findViewById(R.id.img_search);
    }

    private void initListener() {
        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    searchDrink();
                }
            }
        });

        imgSearch.setOnClickListener(view -> searchDrink());

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchDrink();
                return true;
            }
            return false;
        });
    }

    private void getListDrinkBanner() {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).getDrinkDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (listDrinkFeatured != null) {
                            listDrinkFeatured.clear();
                        } else {
                            listDrinkFeatured = new ArrayList<>();
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Drink drink = dataSnapshot.getValue(Drink.class);
                            if (drink != null && drink.isFeatured()) {
                                listDrinkFeatured.add(drink);
                            }
                        }
                        displayListBanner();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void displayListBanner() {
        BannerViewPagerAdapter adapter = new BannerViewPagerAdapter(listDrinkFeatured, new IClickDrinkListener() {
            @Override
            public void onClickDrinkItem(Drink drink) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constant.DRINK_ID, drink.getId());
                GlobalFunction.startActivity(getActivity(), DrinkDetailActivity.class, bundle);
            }

            @Override
            public void onClickDrinkItemManagement(Drink drink) {

            }
        });
        viewPagerDrinkFeatured.setAdapter(adapter);
        indicatorDrinkFeatured.setViewPager(viewPagerDrinkFeatured);

        viewPagerDrinkFeatured.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private void getListCategory() {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).getCategoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (listCategory != null) {
                            listCategory.clear();
                        } else {
                            listCategory = new ArrayList<>();
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Category category = dataSnapshot.getValue(Category.class);
                            if (category != null) {
                                listCategory.add(category);
                            }
                        }
                        displayTabsCategory();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void displayTabsCategory() {
        if (getActivity() == null || listCategory == null || listCategory.isEmpty()) return;
        viewPagerCategory.setOffscreenPageLimit(listCategory.size());
        CategoryPagerAdapter adapter = new CategoryPagerAdapter(getActivity(), listCategory);
        viewPagerCategory.setAdapter(adapter);
        new TabLayoutMediator(tabCategory, viewPagerCategory,
                (tab, position) -> tab.setText(listCategory.get(position).getName().toLowerCase()))
                .attach();
    }

    private void searchDrink() {
        String strKey = edtSearchName.getText().toString().trim();
        EventBus.getDefault().post(new SearchKeywordEvent(strKey));
        Utils.hideSoftKeyboard(getActivity());
    }
}
