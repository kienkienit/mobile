package com.app.shopfee.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.shopfee.R;
import com.app.shopfee.adapter.MyViewPagerAdapter;
import com.app.shopfee.database.DrinkDatabase;
import com.app.shopfee.event.DisplayCartEvent;
import com.app.shopfee.model.Drink;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.StringUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MainActivity extends BaseActivity {

    private BottomNavigationView mBottomNavigationView;
    private ViewPager2 mViewPager2;
    private RelativeLayout layoutCartBottom;
    private TextView tvCountItem, tvDrinksName, tvAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        initUi();

        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mViewPager2 = findViewById(R.id.viewpager_2);
        mViewPager2.setUserInputEnabled(false);
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(this);
        mViewPager2.setAdapter(myViewPagerAdapter);

        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mBottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
                        break;

                    case 1:
                        mBottomNavigationView.getMenu().findItem(R.id.nav_history).setChecked(true);
                        break;

                    case 2:
                        mBottomNavigationView.getMenu().findItem(R.id.nav_account).setChecked(true);
                        break;
                }
            }
        });

        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                mViewPager2.setCurrentItem(0);
            } else if (id == R.id.nav_history) {
                mViewPager2.setCurrentItem(1);
            } else if (id == R.id.nav_account) {
                mViewPager2.setCurrentItem(2);
            }
            return true;
        });

        displayLayoutCartBottom();
    }

    private void initUi() {
        layoutCartBottom = findViewById(R.id.layout_cart_bottom);
        tvCountItem = findViewById(R.id.tv_count_item);
        tvDrinksName = findViewById(R.id.tv_drinks_name);
        tvAmount = findViewById(R.id.tv_amount);
    }

    @Override
    public void onBackPressed() {
        showConfirmExitApp();
    }

    private void showConfirmExitApp() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finish())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisplayCartEvent(DisplayCartEvent event) {
        displayLayoutCartBottom();
    }

    private void displayLayoutCartBottom() {
        List<Drink> listDrink = DrinkDatabase.getInstance(this).drinkDAO().getListDrinkCart();
        if (listDrink == null || listDrink.isEmpty()) {
            layoutCartBottom.setVisibility(View.GONE);
        } else {
            layoutCartBottom.setVisibility(View.VISIBLE);
            String strCountItem = listDrink.size() + " " + getString(R.string.label_item);
            tvCountItem.setText(strCountItem);

            String strDrinksName = "";
            for (Drink drink : listDrink) {
                if (StringUtil.isEmpty(strDrinksName)) {
                    strDrinksName += drink.getName();
                } else {
                    strDrinksName += ", " + drink.getName();
                }
            }
            if (StringUtil.isEmpty(strDrinksName)) {
                tvDrinksName.setVisibility(View.GONE);
            } else {
                tvDrinksName.setVisibility(View.VISIBLE);
                tvDrinksName.setText(strDrinksName);
            }

            int amount = 0;
            for (Drink drink : listDrink) {
                amount = amount + drink.getTotalPrice();
            }
            String strAmount = amount + Constant.CURRENCY;
            tvAmount.setText(strAmount);
        }
        layoutCartBottom.setOnClickListener(v ->
                GlobalFunction.startActivity(this, CartActivity.class));
    }

    public ViewPager2 getViewPager2() {
        return mViewPager2;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
