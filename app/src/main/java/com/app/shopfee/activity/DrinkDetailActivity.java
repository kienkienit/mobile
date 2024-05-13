package com.app.shopfee.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.adapter.ToppingAdapter;
import com.app.shopfee.database.DrinkDatabase;
import com.app.shopfee.event.DisplayCartEvent;
import com.app.shopfee.model.Drink;
import com.app.shopfee.model.RatingReview;
import com.app.shopfee.model.Topping;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlideUtils;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class DrinkDetailActivity extends BaseActivity {

    private ImageView imgDrink;
    private TextView tvName;
    private TextView tvPriceSale;
    private TextView tvDescription;
    private TextView tvSub;
    private TextView tvAdd;
    private TextView tvCount;
    private RelativeLayout layoutRatingAndReview;
    private TextView tvRate;
    private TextView tvCountReview;
    private TextView tvVariantIce, tvVariantHot;
    private TextView tvSizeRegular, tvSizeMedium, tvSizeLarge;
    private TextView tvSugarNormal, tvSugarLess;
    private TextView tvIceNormal, tvIceLess;
    private RecyclerView rcvTopping;
    private EditText edtNotes;
    private TextView tvTotal;
    private TextView tvAddOrder;

    private int mDrinkId;
    private Drink mDrinkOld;
    private Drink mDrink;
    private String currentVariant = Topping.VARIANT_ICE;
    private String currentSize = Topping.SIZE_REGULAR;
    private String currentSugar = Topping.SUGAR_NORMAL;
    private String currentIce = Topping.ICE_NORMAL;
    private List<Topping> listTopping;
    private ToppingAdapter toppingAdapter;

    private String variantText = "";
    private String sizeText = "";
    private String sugarText = "";
    private String iceText = "";
    private String toppingIdsText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_detail);

        getDataIntent();
        initUi();
        getDrinKDetailFromFirebase();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        mDrinkId = bundle.getInt(Constant.DRINK_ID);
        if (bundle.get(Constant.DRINK_OBJECT) != null) {
            mDrinkOld = (Drink) bundle.get(Constant.DRINK_OBJECT);
        }
    }

    private void initUi() {
        imgDrink = findViewById(R.id.img_drink);
        tvName = findViewById(R.id.tv_name);
        tvPriceSale = findViewById(R.id.tv_price_sale);
        tvDescription = findViewById(R.id.tv_description);
        tvSub = findViewById(R.id.tv_sub);
        tvAdd = findViewById(R.id.tv_add);
        tvCount = findViewById(R.id.tv_count);
        layoutRatingAndReview = findViewById(R.id.layout_rating_and_review);
        tvCountReview = findViewById(R.id.tv_count_review);
        tvRate = findViewById(R.id.tv_rate);
        tvVariantIce = findViewById(R.id.tv_variant_ice);
        tvVariantHot = findViewById(R.id.tv_variant_hot);
        tvSizeRegular = findViewById(R.id.tv_size_regular);
        tvSizeMedium = findViewById(R.id.tv_size_medium);
        tvSizeLarge = findViewById(R.id.tv_size_large);
        tvSugarNormal = findViewById(R.id.tv_sugar_normal);
        tvSugarLess = findViewById(R.id.tv_sugar_less);
        tvIceNormal = findViewById(R.id.tv_ice_normal);
        tvIceLess = findViewById(R.id.tv_ice_less);
        rcvTopping = findViewById(R.id.rcv_topping);
        edtNotes = findViewById(R.id.edt_notes);
        tvTotal = findViewById(R.id.tv_total);
        tvAddOrder = findViewById(R.id.tv_add_order);
    }

    private void getDrinKDetailFromFirebase() {
        showProgressDialog(true);
        MyApplication.get(this).getDrinkDetailDatabaseReference(mDrinkId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showProgressDialog(false);
                        mDrink = snapshot.getValue(Drink.class);
                        if (mDrink == null) return;

                        initToolbar();
                        initData();
                        initListener();
                        getListToppingFromFirebase();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showProgressDialog(false);
                        showToastMessage(getString(R.string.msg_get_date_error));
                    }
                });
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText(mDrink.getName());
    }

    private void initData() {
        if (mDrink == null) return;
        GlideUtils.loadUrlBanner(mDrink.getBanner(), imgDrink);
        tvName.setText(mDrink.getName());
        String strPrice = mDrink.getRealPrice() + Constant.CURRENCY;
        tvPriceSale.setText(strPrice);
        tvDescription.setText(mDrink.getDescription());
        if (mDrinkOld != null) {
            mDrink.setCount(mDrinkOld.getCount());
        } else {
            mDrink.setCount(1);
        }
        tvCount.setText(String.valueOf(mDrink.getCount()));
        tvRate.setText(String.valueOf(mDrink.getRate()));
        String strCountReview = "(" + mDrink.getCountReviews() + ")";
        tvCountReview.setText(strCountReview);

        if (mDrinkOld != null) {
            if (StringUtil.isEmpty(mDrinkOld.getToppingIds())) calculatorTotalPrice();
        } else {
            calculatorTotalPrice();
        }

        if (mDrinkOld != null) {
            setValueToppingVariant(mDrinkOld.getVariant());
            setValueToppingSize(mDrinkOld.getSize());
            setValueToppingSugar(mDrinkOld.getSugar());
            setValueToppingIce(mDrinkOld.getIce());
            edtNotes.setText(mDrinkOld.getNote());
        } else {
            setValueToppingVariant(Topping.VARIANT_ICE);
            setValueToppingSize(Topping.SIZE_REGULAR);
            setValueToppingSugar(Topping.SUGAR_NORMAL);
            setValueToppingIce(Topping.ICE_NORMAL);
        }
    }

    private void initListener() {
        tvSub.setOnClickListener(v -> {
            int count = Integer.parseInt(tvCount.getText().toString());
            if (count <= 1) {
                return;
            }
            int newCount = Integer.parseInt(tvCount.getText().toString()) - 1;
            tvCount.setText(String.valueOf(newCount));

            calculatorTotalPrice();
        });

        tvAdd.setOnClickListener(v -> {
            int newCount = Integer.parseInt(tvCount.getText().toString()) + 1;
            tvCount.setText(String.valueOf(newCount));

            calculatorTotalPrice();
        });

        tvVariantIce.setOnClickListener(v -> {
            if (!Topping.VARIANT_ICE.equals(currentVariant)) {
                setValueToppingVariant(Topping.VARIANT_ICE);
            }
        });

        tvVariantHot.setOnClickListener(v -> {
            if (!Topping.VARIANT_HOT.equals(currentVariant)) {
                setValueToppingVariant(Topping.VARIANT_HOT);
            }
        });

        tvSizeRegular.setOnClickListener(v -> {
            if (!Topping.SIZE_REGULAR.equals(currentSize)) {
                setValueToppingSize(Topping.SIZE_REGULAR);
            }
        });

        tvSizeMedium.setOnClickListener(v -> {
            if (!Topping.SIZE_MEDIUM.equals(currentSize)) {
                setValueToppingSize(Topping.SIZE_MEDIUM);
            }
        });

        tvSizeLarge.setOnClickListener(v -> {
            if (!Topping.SIZE_LARGE.equals(currentSize)) {
                setValueToppingSize(Topping.SIZE_LARGE);
            }
        });

        tvSugarNormal.setOnClickListener(v -> {
            if (!Topping.SUGAR_NORMAL.equals(currentSugar)) {
                setValueToppingSugar(Topping.SUGAR_NORMAL);
            }
        });

        tvSugarLess.setOnClickListener(v -> {
            if (!Topping.SUGAR_LESS.equals(currentSugar)) {
                setValueToppingSugar(Topping.SUGAR_LESS);
            }
        });

        tvIceNormal.setOnClickListener(v -> {
            if (!Topping.ICE_NORMAL.equals(currentIce)) {
                setValueToppingIce(Topping.ICE_NORMAL);
            }
        });

        tvIceLess.setOnClickListener(v -> {
            if (!Topping.ICE_LESS.equals(currentIce)) {
                setValueToppingIce(Topping.ICE_LESS);
            }
        });

        layoutRatingAndReview.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            RatingReview ratingReview = new RatingReview(RatingReview.TYPE_RATING_REVIEW_DRINK,
                    String.valueOf(mDrink.getId()));
            bundle.putSerializable(Constant.RATING_REVIEW_OBJECT, ratingReview);
            GlobalFunction.startActivity(DrinkDetailActivity.this,
                    RatingReviewActivity.class, bundle);
        });

        tvAddOrder.setOnClickListener(view -> {
            mDrink.setOption(getAllOption());
            mDrink.setVariant(currentVariant);
            mDrink.setSize(currentSize);
            mDrink.setSugar(currentSugar);
            mDrink.setIce(currentIce);
            mDrink.setToppingIds(toppingIdsText);
            String notes = edtNotes.getText().toString().trim();
            if (!StringUtil.isEmpty(notes)) {
                mDrink.setNote(notes);
            }

            if (!isDrinkInCart()) {
                DrinkDatabase.getInstance(DrinkDetailActivity.this).drinkDAO().insertDrink(mDrink);
            } else {
                DrinkDatabase.getInstance(DrinkDetailActivity.this).drinkDAO().updateDrink(mDrink);
            }
            GlobalFunction.startActivity(DrinkDetailActivity.this, CartActivity.class);
            EventBus.getDefault().post(new DisplayCartEvent());
            finish();
        });
    }

    private void setValueToppingVariant(String type) {
        currentVariant = type;
        switch (type) {
            case Topping.VARIANT_ICE:
                tvVariantIce.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvVariantIce.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvVariantHot.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvVariantHot.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                variantText = getString(R.string.label_variant) + " " + tvVariantIce.getText().toString();
                break;

            case Topping.VARIANT_HOT:
                tvVariantIce.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvVariantIce.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                tvVariantHot.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvVariantHot.setTextColor(ContextCompat.getColor(this, R.color.white));

                variantText = getString(R.string.label_variant) + " " + tvVariantHot.getText().toString();
                break;
        }
    }

    private void setValueToppingSize(String type) {
        currentSize = type;
        switch (type) {
            case Topping.SIZE_REGULAR:
                tvSizeRegular.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvSizeRegular.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSizeMedium.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvSizeMedium.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                tvSizeLarge.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvSizeLarge.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                sizeText = getString(R.string.label_size) + " " + tvSizeRegular.getText().toString();
                break;

            case Topping.SIZE_MEDIUM:
                tvSizeRegular.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvSizeRegular.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                tvSizeMedium.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvSizeMedium.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSizeLarge.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvSizeLarge.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                sizeText = getString(R.string.label_size) + " " + tvSizeMedium.getText().toString();
                break;

            case Topping.SIZE_LARGE:
                tvSizeRegular.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvSizeRegular.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                tvSizeMedium.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvSizeMedium.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                tvSizeLarge.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvSizeLarge.setTextColor(ContextCompat.getColor(this, R.color.white));

                sizeText = tvSizeLarge.getText().toString() + " "
                        + getString(R.string.label_size);
                break;
        }
    }

    private void setValueToppingSugar(String type) {
        currentSugar = type;
        switch (type) {
            case Topping.SUGAR_NORMAL:
                tvSugarNormal.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvSugarNormal.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSugarLess.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvSugarLess.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                sugarText = tvSugarNormal.getText().toString() + " "
                        + getString(R.string.label_sugar);
                break;

            case Topping.SUGAR_LESS:
                tvSugarNormal.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvSugarNormal.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                tvSugarLess.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvSugarLess.setTextColor(ContextCompat.getColor(this, R.color.white));

                sugarText = tvSugarLess.getText().toString() + " "
                        + getString(R.string.label_sugar);
                break;
        }
    }

    private void setValueToppingIce(String type) {
        currentIce = type;
        switch (type) {
            case Topping.ICE_NORMAL:
                tvIceNormal.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvIceNormal.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvIceLess.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvIceLess.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                iceText = tvIceNormal.getText().toString() + " " + getString(R.string.label_ice);
                break;

            case Topping.ICE_LESS:
                tvIceNormal.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvIceNormal.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                tvIceLess.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvIceLess.setTextColor(ContextCompat.getColor(this, R.color.white));

                iceText = tvIceLess.getText().toString() + " " + getString(R.string.label_ice);
                break;
        }
    }

    private void getListToppingFromFirebase() {
        MyApplication.get(this).getToppingDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (listTopping != null) {
                            listTopping.clear();
                        } else {
                            listTopping = new ArrayList<>();
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Topping topping = dataSnapshot.getValue(Topping.class);
                            if (topping != null) {
                                listTopping.add(topping);
                            }
                        }
                        displayListTopping();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void displayListTopping() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvTopping.setLayoutManager(linearLayoutManager);
        toppingAdapter = new ToppingAdapter(listTopping, this::handleClickItemTopping);
        rcvTopping.setAdapter(toppingAdapter);
        handleSetToppingDrinkOld();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleSetToppingDrinkOld() {
        if (mDrinkOld == null || StringUtil.isEmpty(mDrinkOld.getToppingIds())) return;
        if (listTopping == null || listTopping.isEmpty()) return;
        String[] tempId = mDrinkOld.getToppingIds().split(",");
        for (String s : tempId) {
            for (Topping topping : listTopping) {
                if (topping.getId() == Integer.parseInt(s)) {
                    topping.setSelected(true);
                    break;
                }
            }
        }
        if (toppingAdapter != null) toppingAdapter.notifyDataSetChanged();
        calculatorTotalPrice();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleClickItemTopping(Topping topping) {
        for (Topping toppingEntity : listTopping) {
            if (toppingEntity.getId() == topping.getId()) {
                toppingEntity.setSelected(!toppingEntity.isSelected());
            }
        }
        if (toppingAdapter != null) toppingAdapter.notifyDataSetChanged();
        calculatorTotalPrice();
    }

    private void calculatorTotalPrice() {
        int count = Integer.parseInt(tvCount.getText().toString().trim());
        int priceOneDrink = mDrink.getRealPrice() + getTotalPriceTopping();
        int totalPrice = priceOneDrink * count;
        String strTotalPrice = totalPrice + Constant.CURRENCY;
        tvTotal.setText(strTotalPrice);

        mDrink.setCount(count);
        mDrink.setPriceOneDrink(priceOneDrink);
        mDrink.setTotalPrice(totalPrice);
    }

    private int getTotalPriceTopping() {
        if (listTopping == null || listTopping.isEmpty()) return 0;
        int total = 0;
        for (Topping topping : listTopping) {
            if (topping.isSelected()) {
                total += topping.getPrice();
            }
        }
        return total;
    }

    private String getAllToppingSelected() {
        if (listTopping == null || listTopping.isEmpty()) return "";
        String strTopping = "";
        for (Topping topping : listTopping) {
            if (topping.isSelected()) {
                if (StringUtil.isEmpty(strTopping)) {
                    strTopping += topping.getName();
                    toppingIdsText += String.valueOf(topping.getId());
                } else {
                    strTopping += ", " + topping.getName();
                }
                if (StringUtil.isEmpty(toppingIdsText)) {
                    toppingIdsText += String.valueOf(topping.getId());
                } else {
                    toppingIdsText += "," + topping.getId();
                }
            }
        }
        return strTopping;
    }

    private boolean isDrinkInCart() {
        List<Drink> list = DrinkDatabase.getInstance(this)
                .drinkDAO().checkDrinkInCart(mDrink.getId());
        return list != null && !list.isEmpty();
    }

    private String getAllOption() {
        String option = variantText + ", " + sizeText + ", " + sugarText + ", " + iceText;
        if (!StringUtil.isEmpty(getAllToppingSelected())) {
            option += ", " + getAllToppingSelected();
        }
        String notes = edtNotes.getText().toString().trim();
        if (!StringUtil.isEmpty(notes)) {
            option += ", " + notes;
        }
        return option;
    }
}
