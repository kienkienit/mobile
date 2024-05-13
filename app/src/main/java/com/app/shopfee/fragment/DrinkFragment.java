package com.app.shopfee.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.activity.DrinkDetailActivity;
import com.app.shopfee.adapter.DrinkAdapter;
import com.app.shopfee.adapter.FilterAdapter;
import com.app.shopfee.event.SearchKeywordEvent;
import com.app.shopfee.model.Drink;
import com.app.shopfee.model.Filter;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class DrinkFragment extends Fragment {

    private View mView;
    private RecyclerView rcvFilter;
    private RecyclerView rcvDrink;

    private List<Drink> listDrink;
    private List<Drink> listDrinkDisplay;
    private List<Drink> listDrinkKeyWord;
    private List<Filter> listFilter;
    private DrinkAdapter drinkAdapter;
    private FilterAdapter filterAdapter;
    private int categoryId;
    private Filter currentFilter;
    private String keyword = "";

    public static DrinkFragment newInstance(int categoryId) {
        DrinkFragment drinkFragment = new DrinkFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.CATEGORY_ID, categoryId);
        drinkFragment.setArguments(bundle);
        return drinkFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_drink, container, false);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        getDataArguments();
        initUi();
        initListener();

        getListFilter();
        getListDrink();

        return mView;
    }

    private void getDataArguments() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        categoryId = bundle.getInt(Constant.CATEGORY_ID);
    }

    private void initUi() {
        rcvFilter = mView.findViewById(R.id.rcv_filter);
        rcvDrink = mView.findViewById(R.id.rcv_drink);
        displayListDrink();
    }

    private void initListener() {
    }

    private void getListFilter() {
        listFilter = new ArrayList<>();
        listFilter.add(new Filter(Filter.TYPE_FILTER_ALL, getString(R.string.filter_all)));
        listFilter.add(new Filter(Filter.TYPE_FILTER_RATE, getString(R.string.filter_rate)));
        listFilter.add(new Filter(Filter.TYPE_FILTER_PRICE, getString(R.string.filter_price)));
        listFilter.add(new Filter(Filter.TYPE_FILTER_PROMOTION, getString(R.string.filter_promotion)));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        rcvFilter.setLayoutManager(linearLayoutManager);
        currentFilter = listFilter.get(0);
        currentFilter.setSelected(true);
        filterAdapter = new FilterAdapter(getActivity(), listFilter, this::handleClickFilter);
        rcvFilter.setAdapter(filterAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleClickFilter(Filter filter) {
        for (Filter filterEntity : listFilter) {
            if (filterEntity.getId() == filter.getId()) {
                filterEntity.setSelected(true);
                setListDrinkDisplay(filterEntity, keyword);
                currentFilter = filterEntity;
            } else {
                filterEntity.setSelected(false);
            }
        }
        if (filterAdapter != null) filterAdapter.notifyDataSetChanged();
    }

    private void getListDrink() {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).getDrinkDatabaseReference()
                .orderByChild(Constant.CATEGORY_ID).equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (listDrink != null) {
                            listDrink.clear();
                        } else {
                            listDrink = new ArrayList<>();
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Drink drink = dataSnapshot.getValue(Drink.class);
                            if (drink != null) {
                                listDrink.add(0, drink);
                            }
                        }
                        setListDrinkDisplay(new Filter(Filter.TYPE_FILTER_ALL, getString(R.string.filter_all)), keyword);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void displayListDrink() {
        if (getActivity() == null) return;
        listDrinkDisplay = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvDrink.setLayoutManager(linearLayoutManager);
        drinkAdapter = new DrinkAdapter(listDrinkDisplay, drink -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.DRINK_ID, drink.getId());
            GlobalFunction.startActivity(getActivity(), DrinkDetailActivity.class, bundle);
        });
        rcvDrink.setAdapter(drinkAdapter);
    }

    private void setListDrinkDisplay(@NonNull Filter filter,@Nullable String keyword) {
        if (listDrink == null || listDrink.isEmpty()) return;

        if (listDrinkKeyWord != null) {
            listDrinkKeyWord.clear();
        } else {
            listDrinkKeyWord = new ArrayList<>();
        }

        if (listDrinkDisplay != null) {
            listDrinkDisplay.clear();
        } else {
            listDrinkDisplay = new ArrayList<>();
        }

        if (!StringUtil.isEmpty(keyword)) {
            for (Drink drink: listDrink) {
                if (getTextSearch(drink.getName()).toLowerCase().trim()
                        .contains(getTextSearch(keyword).toLowerCase().trim())) {
                    listDrinkKeyWord.add(drink);
                }
            }
            switch (filter.getId()) {
                case Filter.TYPE_FILTER_ALL:
                    listDrinkDisplay.addAll(listDrinkKeyWord);
                    break;

                case Filter.TYPE_FILTER_RATE:
                    listDrinkDisplay.addAll(listDrinkKeyWord);
                    Collections.sort(listDrinkDisplay,
                            (drink1, drink2) -> Double.compare(drink2.getRate(), drink1.getRate()));
                    break;

                case Filter.TYPE_FILTER_PRICE:
                    listDrinkDisplay.addAll(listDrinkKeyWord);
                    Collections.sort(listDrinkDisplay,
                            (drink1, drink2) -> Integer.compare(drink1.getRealPrice(), drink2.getRealPrice()));
                    break;

                case Filter.TYPE_FILTER_PROMOTION:
                    for (Drink drink : listDrinkKeyWord) {
                        if (drink.getSale() > 0) listDrinkDisplay.add(drink);
                    }
                    break;
            }
        } else {
            switch (filter.getId()) {
                case Filter.TYPE_FILTER_ALL:
                    listDrinkDisplay.addAll(listDrink);
                    break;

                case Filter.TYPE_FILTER_RATE:
                    listDrinkDisplay.addAll(listDrink);
                    Collections.sort(listDrinkDisplay,
                            (drink1, drink2) -> Double.compare(drink2.getRate(), drink1.getRate()));
                    break;

                case Filter.TYPE_FILTER_PRICE:
                    listDrinkDisplay.addAll(listDrink);
                    Collections.sort(listDrinkDisplay,
                            (drink1, drink2) -> Integer.compare(drink1.getRealPrice(), drink2.getRealPrice()));
                    break;

                case Filter.TYPE_FILTER_PROMOTION:
                    for (Drink drink : listDrink) {
                        if (drink.getSale() > 0) listDrinkDisplay.add(drink);
                    }
                    break;
            }
        }
        reloadListDrink();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void reloadListDrink() {
        if (drinkAdapter != null) drinkAdapter.notifyDataSetChanged();
    }

    public String getTextSearch(String input) {
        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchKeywordEvent(SearchKeywordEvent event) {
        keyword = event.getKeyword();
        setListDrinkDisplay(currentFilter, keyword);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (filterAdapter != null) filterAdapter.release();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
