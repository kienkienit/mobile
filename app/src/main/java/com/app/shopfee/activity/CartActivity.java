<<<<<<< HEAD
////package com.app.shopfee.activity;
////
////import android.os.Bundle;
////import android.widget.ImageView;
////import android.widget.LinearLayout;
////import android.widget.RelativeLayout;
////import android.widget.TextView;
////
////import androidx.recyclerview.widget.LinearLayoutManager;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.app.shopfee.MyApplication;
////import com.app.shopfee.R;
////import com.app.shopfee.adapter.CartAdapter;
////import com.app.shopfee.database.DrinkDatabase;
////import com.app.shopfee.event.AddressDeletedEvent;
////import com.app.shopfee.event.AddressSelectedEvent;
////import com.app.shopfee.event.AddressUpdatedEvent;
////import com.app.shopfee.event.DisplayCartEvent;
////import com.app.shopfee.event.OrderSuccessEvent;
////import com.app.shopfee.event.PaymentMethodSelectedEvent;
////import com.app.shopfee.event.VoucherSelectedEvent;
////import com.app.shopfee.model.Address;
////import com.app.shopfee.model.Drink;
////import com.app.shopfee.model.DrinkOrder;
////import com.app.shopfee.model.Order;
////import com.app.shopfee.model.PaymentMethod;
////import com.app.shopfee.model.Voucher;
////import com.app.shopfee.prefs.DataStoreManager;
////import com.app.shopfee.utils.Constant;
////import com.app.shopfee.utils.GlobalFunction;
////import com.app.shopfee.utils.NotificationHelper;
////
////import org.greenrobot.eventbus.EventBus;
////import org.greenrobot.eventbus.Subscribe;
////import org.greenrobot.eventbus.ThreadMode;
////
////import java.util.ArrayList;
////import java.util.List;
////
////public class CartActivity extends BaseActivity {
////
////    private RecyclerView rcvCart;
////    private LinearLayout layoutAddOrder;
////    private RelativeLayout layoutPaymentMethod;
////    private TextView tvPaymentMethod;
////
////    private RelativeLayout layoutAddress;
////    private TextView tvAddress;
////    private RelativeLayout layoutVoucher;
////    private TextView tvVoucher;
////    private TextView tvNameVoucher;
////    private TextView tvPriceDrink;
////    private TextView tvCountItem;
////    private TextView tvAmount;
////    private TextView tvPriceVoucher;
////    private TextView tvCheckout;
////
////    private List<Drink> listDrinkCart;
////    private CartAdapter cartAdapter;
////    private int priceDrink;
////    private int mAmount;
////    private PaymentMethod paymentMethodSelected;
////    private Address addressSelected;
////    private Voucher voucherSelected;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_cart);
////
////        if (!EventBus.getDefault().isRegistered(this)) {
////            EventBus.getDefault().register(this);
////        }
////
////        initToolbar();
////        initUi();
////        initListener();
////        initData();
////    }
////
////    private void initToolbar() {
////        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
////        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
////        imgToolbarBack.setOnClickListener(view -> finish());
////        tvToolbarTitle.setText(getString(R.string.label_cart));
////    }
////
////    private void initUi() {
////        rcvCart = findViewById(R.id.rcv_cart);
////        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
////        rcvCart.setLayoutManager(linearLayoutManager);
////        layoutAddOrder = findViewById(R.id.layout_add_order);
////        layoutPaymentMethod = findViewById(R.id.layout_payment_method);
////        tvPaymentMethod = findViewById(R.id.tv_payment_method);
////        layoutAddress = findViewById(R.id.layout_address);
////        tvAddress = findViewById(R.id.tv_address);
////        layoutVoucher = findViewById(R.id.layout_voucher);
////        tvVoucher = findViewById(R.id.tv_voucher);
////        tvNameVoucher = findViewById(R.id.tv_name_voucher);
////        tvCountItem = findViewById(R.id.tv_count_item);
////        tvPriceDrink = findViewById(R.id.tv_price_drink);
////        tvAmount = findViewById(R.id.tv_amount);
////        tvPriceVoucher = findViewById(R.id.tv_price_voucher);
////        tvCheckout = findViewById(R.id.tv_checkout);
////    }
////
////    private void initListener() {
////        layoutAddOrder.setOnClickListener(v -> finish());
////        layoutPaymentMethod.setOnClickListener(view -> {
////            Bundle bundle = new Bundle();
////            if (paymentMethodSelected != null) {
////                bundle.putInt(Constant.PAYMENT_METHOD_ID, paymentMethodSelected.getId());
////            }
////            GlobalFunction.startActivity(CartActivity.this, PaymentMethodActivity.class, bundle);
////        });
////
////        layoutAddress.setOnClickListener(view -> {
////            Bundle bundle = new Bundle();
////            if (addressSelected != null) {
////                bundle.putLong(Constant.ADDRESS_ID, addressSelected.getId());
////            }
////            GlobalFunction.startActivity(CartActivity.this, AddressActivity.class, bundle);
////        });
////
////        layoutVoucher.setOnClickListener(view -> {
////            Bundle bundle = new Bundle();
////            bundle.putInt(Constant.AMOUNT_VALUE, priceDrink);
////            if (voucherSelected != null) {
////                bundle.putInt(Constant.VOUCHER_ID, voucherSelected.getId());
////            }
////            GlobalFunction.startActivity(CartActivity.this, VoucherActivity.class, bundle);
////        });
////
////        tvCheckout.setOnClickListener(view -> {
////            if (listDrinkCart == null || listDrinkCart.isEmpty()) return;
////            if (paymentMethodSelected == null) {
////                showToastMessage(getString(R.string.label_choose_payment_method));
////                return;
////            }
////            if (addressSelected == null) {
////                showToastMessage(getString(R.string.label_choose_address));
////                return;
////            }
////
////            Order orderBooking = new Order();
////            orderBooking.setId(System.currentTimeMillis());
////            orderBooking.setUserEmail(DataStoreManager.getUser().getEmail());
////            orderBooking.setDateTime(String.valueOf(System.currentTimeMillis()));
////            List<DrinkOrder> drinks = new ArrayList<>();
////            for (Drink drink : listDrinkCart) {
////                drinks.add(new DrinkOrder(drink.getName(), drink.getOption(), drink.getCount(),
////                        drink.getPriceOneDrink(), drink.getImage()));
////            }
////            orderBooking.setDrinks(drinks);
////            orderBooking.setPrice(priceDrink);
////            if (voucherSelected != null) {
////                orderBooking.setVoucher(voucherSelected.getPriceDiscount(priceDrink));
////            }
////            orderBooking.setTotal(mAmount);
////            orderBooking.setPaymentMethod(paymentMethodSelected.getName());
////            orderBooking.setAddress(addressSelected);
////            orderBooking.setStatus(Order.STATUS_NEW);
////
////            NotificationHelper.sendOrderSuccessNotification(this);
////
////            Bundle bundle = new Bundle();
////            bundle.putSerializable(Constant.ORDER_OBJECT, orderBooking);
////            GlobalFunction.startActivity(CartActivity.this, PaymentActivity.class, bundle);
////        });
////    }
////
////    private void initData() {
////        listDrinkCart = new ArrayList<>();
////        listDrinkCart = DrinkDatabase.getInstance(this).drinkDAO().getListDrinkCart();
////        if (listDrinkCart == null || listDrinkCart.isEmpty()) {
////            return;
////        }
////        cartAdapter = new CartAdapter(listDrinkCart, new CartAdapter.IClickCartListener() {
////            @Override
////            public void onClickDeleteItem(Drink drink, int position) {
////                DrinkDatabase.getInstance(CartActivity.this).drinkDAO().deleteDrink(drink);
////                listDrinkCart.remove(position);
////                cartAdapter.notifyItemRemoved(position);
////
////                displayCountItemCart();
////                calculateTotalPrice();
////                EventBus.getDefault().post(new DisplayCartEvent());
////            }
////
////            @Override
////            public void onClickUpdateItem(Drink drink, int position) {
////                DrinkDatabase.getInstance(CartActivity.this).drinkDAO().updateDrink(drink);
////                cartAdapter.notifyItemChanged(position);
////
////                calculateTotalPrice();
////                EventBus.getDefault().post(new DisplayCartEvent());
////            }
////
////            @Override
////            public void onClickEditItem(Drink drink) {
////                Bundle bundle = new Bundle();
////                bundle.putInt(Constant.DRINK_ID, drink.getId());
////                bundle.putSerializable(Constant.DRINK_OBJECT, drink);
////                GlobalFunction.startActivity(CartActivity.this, DrinkDetailActivity.class, bundle);
////            }
////        });
////        rcvCart.setAdapter(cartAdapter);
////        calculateTotalPrice();
////        displayCountItemCart();
////    }
////
////    private void displayCountItemCart() {
////        String strCountItem = "(" + listDrinkCart.size() + " " + getString(R.string.label_item) + ")";
////        tvCountItem.setText(strCountItem);
////    }
////
////    private void calculateTotalPrice() {
////        if (listDrinkCart == null || listDrinkCart.isEmpty()) {
////            String strZero = 0 + Constant.CURRENCY;
////            priceDrink = 0;
////            tvPriceDrink.setText(strZero);
////
////            mAmount = 0;
////            tvAmount.setText(strZero);
////            return;
////        }
////
////        int totalPrice = 0;
////        for (Drink drink : listDrinkCart) {
////            totalPrice = totalPrice + drink.getTotalPrice();
////        }
////
////        priceDrink = totalPrice;
////        String strPriceDrink = priceDrink + Constant.CURRENCY;
////        tvPriceDrink.setText(strPriceDrink);
////
////        mAmount = totalPrice;
////        if (voucherSelected != null) {
////            mAmount = mAmount - voucherSelected.getPriceDiscount(priceDrink);
////        }
////        String strAmount = mAmount + Constant.CURRENCY;
////        tvAmount.setText(strAmount);
////    }
////
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    public void onPaymentMethodSelectedEvent(PaymentMethodSelectedEvent event) {
////        if (event.getPaymentMethod() != null) {
////            paymentMethodSelected = event.getPaymentMethod();
////            tvPaymentMethod.setText(paymentMethodSelected.getName());
////        } else {
////            tvPaymentMethod.setText(getString(R.string.label_no_payment_method));
////        }
////    }
////
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    public void onAddressSelectedEvent(AddressSelectedEvent event) {
////        if (event.getAddress() != null) {
////            addressSelected = event.getAddress();
////            tvAddress.setText(addressSelected.getAddress());
////        } else {
////            tvAddress.setText(getString(R.string.label_no_address));
////        }
////    }
////
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    public void onVoucherSelectedEvent(VoucherSelectedEvent event) {
////        if (event.getVoucher() != null) {
////            voucherSelected = event.getVoucher();
////            tvVoucher.setText(voucherSelected.getTitle());
////            tvNameVoucher.setText(voucherSelected.getTitle());
////            String strPriceVoucher = "-" + voucherSelected.getPriceDiscount(priceDrink)
////                    + Constant.CURRENCY;
////            tvPriceVoucher.setText(strPriceVoucher);
////        } else {
////            tvVoucher.setText(getString(R.string.label_no_voucher));
////            tvNameVoucher.setText(getString(R.string.label_no_voucher));
////            String strPriceVoucher = "-0" + Constant.CURRENCY;
////            tvPriceVoucher.setText(strPriceVoucher);
////        }
////        calculateTotalPrice();
////    }
////
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    public void onOrderSuccessEvent(OrderSuccessEvent event) {
////        finish();
////    }
////
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    public void onAddressDeletedEvent(AddressDeletedEvent event) {
////        if (addressSelected != null && addressSelected.getId() == event.getAddressId()) {
////            addressSelected = null;
////            tvAddress.setText(getString(R.string.label_no_address));
////        }
////    }
////
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    public void onAddressUpdatedEvent(AddressUpdatedEvent event) {
////        if (addressSelected != null && addressSelected.getId() == event.getAddress().getId()) {
////            addressSelected = event.getAddress();
////            tvAddress.setText(addressSelected.getAddress());
////        }
////    }
////
////
////    @Override
////    protected void onDestroy() {
////        super.onDestroy();
////        if (EventBus.getDefault().isRegistered(this)) {
////            EventBus.getDefault().unregister(this);
////        }
////    }
////}
//
//package com.app.shopfee.activity;
//
//import android.os.Bundle;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.app.shopfee.R;
//import com.app.shopfee.adapter.CartAdapter;
//import com.app.shopfee.database.DrinkDatabase;
//import com.app.shopfee.event.AddressDeletedEvent;
//import com.app.shopfee.event.AddressSelectedEvent;
//import com.app.shopfee.event.AddressUpdatedEvent;
//import com.app.shopfee.event.DisplayCartEvent;
//import com.app.shopfee.event.OrderSuccessEvent;
//import com.app.shopfee.event.PaymentMethodSelectedEvent;
//import com.app.shopfee.event.VoucherSelectedEvent;
//import com.app.shopfee.model.Address;
//import com.app.shopfee.model.Drink;
//import com.app.shopfee.model.DrinkOrder;
//import com.app.shopfee.model.Order;
//import com.app.shopfee.model.PaymentMethod;
//import com.app.shopfee.model.Voucher;
//import com.app.shopfee.prefs.DataStoreManager;
//import com.app.shopfee.utils.Constant;
//import com.app.shopfee.utils.GlobalFunction;
//import com.app.shopfee.utils.NotificationHelper;
//import com.app.shopfee.prefs.MySharedPreferences;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CartActivity extends BaseActivity {
//
//    private RecyclerView rcvCart;
//    private LinearLayout layoutAddOrder;
//    private RelativeLayout layoutPaymentMethod;
//    private TextView tvPaymentMethod;
//
//    private RelativeLayout layoutAddress;
//    private TextView tvAddress;
//    private RelativeLayout layoutVoucher;
//    private TextView tvVoucher;
//    private TextView tvNameVoucher;
//    private TextView tvPriceDrink;
//    private TextView tvCountItem;
//    private TextView tvAmount;
//    private TextView tvPriceVoucher;
//    private TextView tvCheckout;
//
//    private List<Drink> listDrinkCart;
//    private CartAdapter cartAdapter;
//    private int priceDrink;
//    private int mAmount;
//    private PaymentMethod paymentMethodSelected;
//    private Address addressSelected;
//    private Voucher voucherSelected;
//    private MySharedPreferences mySharedPreferences;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cart);
//
//        mySharedPreferences = new MySharedPreferences(this);
//
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
//
//        initToolbar();
//        initUi();
//        initListener();
//        initData();
//    }
//
//    private void initToolbar() {
//        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
//        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
//        imgToolbarBack.setOnClickListener(view -> finish());
//        tvToolbarTitle.setText(getString(R.string.label_cart));
//    }
//
//    private void initUi() {
//        rcvCart = findViewById(R.id.rcv_cart);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        rcvCart.setLayoutManager(linearLayoutManager);
//        layoutAddOrder = findViewById(R.id.layout_add_order);
//        layoutPaymentMethod = findViewById(R.id.layout_payment_method);
//        tvPaymentMethod = findViewById(R.id.tv_payment_method);
//        layoutAddress = findViewById(R.id.layout_address);
//        tvAddress = findViewById(R.id.tv_address);
//        layoutVoucher = findViewById(R.id.layout_voucher);
//        tvVoucher = findViewById(R.id.tv_voucher);
//        tvNameVoucher = findViewById(R.id.tv_name_voucher);
//        tvCountItem = findViewById(R.id.tv_count_item);
//        tvPriceDrink = findViewById(R.id.tv_price_drink);
//        tvAmount = findViewById(R.id.tv_amount);
//        tvPriceVoucher = findViewById(R.id.tv_price_voucher);
//        tvCheckout = findViewById(R.id.tv_checkout);
//    }
//
//    private void initListener() {
//        layoutAddOrder.setOnClickListener(v -> finish());
//        layoutPaymentMethod.setOnClickListener(view -> {
//            Bundle bundle = new Bundle();
//            if (paymentMethodSelected != null) {
//                bundle.putInt(Constant.PAYMENT_METHOD_ID, paymentMethodSelected.getId());
//            }
//            GlobalFunction.startActivity(CartActivity.this, PaymentMethodActivity.class, bundle);
//        });
//
//        layoutAddress.setOnClickListener(view -> {
//            Bundle bundle = new Bundle();
//            if (addressSelected != null) {
//                bundle.putLong(Constant.ADDRESS_ID, addressSelected.getId());
//            }
//            GlobalFunction.startActivity(CartActivity.this, AddressActivity.class, bundle);
//        });
//
//        layoutVoucher.setOnClickListener(view -> {
//            Bundle bundle = new Bundle();
//            bundle.putInt(Constant.AMOUNT_VALUE, priceDrink);
//            if (voucherSelected != null) {
//                bundle.putInt(Constant.VOUCHER_ID, voucherSelected.getId());
//            }
//            GlobalFunction.startActivity(CartActivity.this, VoucherActivity.class, bundle);
//        });
//
//        tvCheckout.setOnClickListener(view -> {
//            if (listDrinkCart == null || listDrinkCart.isEmpty()) return;
//            if (paymentMethodSelected == null) {
//                showToastMessage(getString(R.string.label_choose_payment_method));
//                return;
//            }
//            if (addressSelected == null) {
//                showToastMessage(getString(R.string.label_choose_address));
//                return;
//            }
//
//            Order orderBooking = new Order();
//            orderBooking.setId(System.currentTimeMillis());
//            orderBooking.setUserEmail(DataStoreManager.getUser().getEmail());
//            orderBooking.setDateTime(String.valueOf(System.currentTimeMillis()));
//            List<DrinkOrder> drinks = new ArrayList<>();
//            for (Drink drink : listDrinkCart) {
//                drinks.add(new DrinkOrder(drink.getName(), drink.getOption(), drink.getCount(),
//                        drink.getPriceOneDrink(), drink.getImage()));
//            }
//            orderBooking.setDrinks(drinks);
//            orderBooking.setPrice(priceDrink);
//            if (voucherSelected != null) {
//                orderBooking.setVoucher(voucherSelected.getPriceDiscount(priceDrink));
//            }
//            orderBooking.setTotal(mAmount);
//            orderBooking.setPaymentMethod(paymentMethodSelected.getName());
//            orderBooking.setAddress(addressSelected);
//            orderBooking.setStatus(Order.STATUS_NEW);
//
//            NotificationHelper.sendOrderSuccessNotification(this);
//
//            // Clear cart items after checkout
//            clearCartItems();
//
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(Constant.ORDER_OBJECT, orderBooking);
//            GlobalFunction.startActivity(CartActivity.this, PaymentActivity.class, bundle);
//        });
//    }
//
//    private void initData() {
//        listDrinkCart = getCartItems();
//        if (listDrinkCart == null || listDrinkCart.isEmpty()) {
//            listDrinkCart = DrinkDatabase.getInstance(this).drinkDAO().getListDrinkCart();
//        }
//        if (listDrinkCart == null || listDrinkCart.isEmpty()) {
//            return;
//        }
//        cartAdapter = new CartAdapter(listDrinkCart, new CartAdapter.IClickCartListener() {
//            @Override
//            public void onClickDeleteItem(Drink drink, int position) {
//                DrinkDatabase.getInstance(CartActivity.this).drinkDAO().deleteDrink(drink);
//                listDrinkCart.remove(position);
//                cartAdapter.notifyItemRemoved(position);
//                displayCountItemCart();
//                calculateTotalPrice();
//                saveCartItems(listDrinkCart);
//                EventBus.getDefault().post(new DisplayCartEvent());
//            }
//
//            @Override
//            public void onClickUpdateItem(Drink drink, int position) {
//                DrinkDatabase.getInstance(CartActivity.this).drinkDAO().updateDrink(drink);
//                cartAdapter.notifyItemChanged(position);
//                calculateTotalPrice();
//                saveCartItems(listDrinkCart);
//                EventBus.getDefault().post(new DisplayCartEvent());
//            }
//
//            @Override
//            public void onClickEditItem(Drink drink) {
//                Bundle bundle = new Bundle();
//                bundle.putInt(Constant.DRINK_ID, drink.getId());
//                bundle.putSerializable(Constant.DRINK_OBJECT, drink);
//                GlobalFunction.startActivity(CartActivity.this, DrinkDetailActivity.class, bundle);
//            }
//        });
//        rcvCart.setAdapter(cartAdapter);
//        calculateTotalPrice();
//        displayCountItemCart();
//    }
//
//    private void displayCountItemCart() {
//        String strCountItem = "(" + listDrinkCart.size() + " " + getString(R.string.label_item) + ")";
//        tvCountItem.setText(strCountItem);
//    }
//
//    private void calculateTotalPrice() {
//        if (listDrinkCart == null || listDrinkCart.isEmpty()) {
//            String strZero = 0 + Constant.CURRENCY;
//            priceDrink = 0;
//            tvPriceDrink.setText(strZero);
//
//            mAmount = 0;
//            tvAmount.setText(strZero);
//            return;
//        }
//
//        int totalPrice = 0;
//        for (Drink drink : listDrinkCart) {
//            totalPrice = totalPrice + drink.getTotalPrice();
//        }
//
//        priceDrink = totalPrice;
//        String strPriceDrink = priceDrink + Constant.CURRENCY;
//        tvPriceDrink.setText(strPriceDrink);
//
//        mAmount = totalPrice;
//        if (voucherSelected != null) {
//            mAmount = mAmount - voucherSelected.getPriceDiscount(priceDrink);
//        }
//        String strAmount = mAmount + Constant.CURRENCY;
//        tvAmount.setText(strAmount);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onPaymentMethodSelectedEvent(PaymentMethodSelectedEvent event) {
//        if (event.getPaymentMethod() != null) {
//            paymentMethodSelected = event.getPaymentMethod();
//            tvPaymentMethod.setText(paymentMethodSelected.getName());
//        } else {
//            tvPaymentMethod.setText(getString(R.string.label_no_payment_method));
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onAddressSelectedEvent(AddressSelectedEvent event) {
//        if (event.getAddress() != null) {
//            addressSelected = event.getAddress();
//            tvAddress.setText(addressSelected.getAddress());
//        } else {
//            tvAddress.setText(getString(R.string.label_no_address));
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onVoucherSelectedEvent(VoucherSelectedEvent event) {
//        if (event.getVoucher() != null) {
//            voucherSelected = event.getVoucher();
//            tvVoucher.setText(voucherSelected.getTitle());
//            tvNameVoucher.setText(voucherSelected.getTitle());
//            String strPriceVoucher = "-" + voucherSelected.getPriceDiscount(priceDrink)
//                    + Constant.CURRENCY;
//            tvPriceVoucher.setText(strPriceVoucher);
//        } else {
//            tvVoucher.setText(getString(R.string.label_no_voucher));
//            tvNameVoucher.setText(getString(R.string.label_no_voucher));
//            String strPriceVoucher = "-0" + Constant.CURRENCY;
//            tvPriceVoucher.setText(strPriceVoucher);
//        }
//        calculateTotalPrice();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onOrderSuccessEvent(OrderSuccessEvent event) {
//        finish();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onAddressDeletedEvent(AddressDeletedEvent event) {
//        if (addressSelected != null && addressSelected.getId() == event.getAddressId()) {
//            addressSelected = null;
//            tvAddress.setText(getString(R.string.label_no_address));
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onAddressUpdatedEvent(AddressUpdatedEvent event) {
//        if (addressSelected != null && addressSelected.getId() == event.getAddress().getId()) {
//            addressSelected = event.getAddress();
//            tvAddress.setText(addressSelected.getAddress());
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
//    }
//
//    // Save cart items to SharedPreferences
//    private void saveCartItems(List<Drink> cartItems) {
//        String userEmail = DataStoreManager.getUser().getEmail();
//        if (userEmail != null) {
//            String key = "CART_ITEMS_" + userEmail;
//            Gson gson = new Gson();
//            String json = gson.toJson(cartItems);
//            mySharedPreferences.putStringValue(key, json);
//        }
//    }
//
//    // Get cart items from SharedPreferences
//    private List<Drink> getCartItems() {
//        String userEmail = DataStoreManager.getUser().getEmail();
//        if (userEmail != null) {
//            String key = "CART_ITEMS_" + userEmail;
//            String json = mySharedPreferences.getStringValue(key, "");
//            if (json.isEmpty()) {
//                return new ArrayList<>();
//            }
//            Gson gson = new Gson();
//            Type type = new TypeToken<List<Drink>>() {}.getType();
//            return gson.fromJson(json, type);
//        }
//        return new ArrayList<>();
//    }
//
//    // Clear cart items from SharedPreferences
//    private void clearCartItems() {
//        String userEmail = DataStoreManager.getUser().getEmail();
//        if (userEmail != null) {
//            String key = "CART_ITEMS_" + userEmail;
//            mySharedPreferences.removeValue(key);
//        }
//    }
//}

=======
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be
package com.app.shopfee.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.adapter.CartAdapter;
import com.app.shopfee.database.DrinkDatabase;
import com.app.shopfee.event.AddressDeletedEvent;
import com.app.shopfee.event.AddressSelectedEvent;
import com.app.shopfee.event.AddressUpdatedEvent;
import com.app.shopfee.event.DisplayCartEvent;
import com.app.shopfee.event.OrderSuccessEvent;
import com.app.shopfee.event.PaymentMethodSelectedEvent;
import com.app.shopfee.event.VoucherSelectedEvent;
import com.app.shopfee.model.Address;
import com.app.shopfee.model.Drink;
import com.app.shopfee.model.DrinkOrder;
import com.app.shopfee.model.Order;
import com.app.shopfee.model.PaymentMethod;
import com.app.shopfee.model.Voucher;
import com.app.shopfee.prefs.DataStoreManager;
<<<<<<< HEAD
import com.app.shopfee.prefs.MySharedPreferences;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.NotificationHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
=======
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.NotificationHelper;
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

<<<<<<< HEAD
import java.lang.reflect.Type;
=======
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseActivity {

    private RecyclerView rcvCart;
    private LinearLayout layoutAddOrder;
    private RelativeLayout layoutPaymentMethod;
    private TextView tvPaymentMethod;

    private RelativeLayout layoutAddress;
    private TextView tvAddress;
    private RelativeLayout layoutVoucher;
    private TextView tvVoucher;
    private TextView tvNameVoucher;
    private TextView tvPriceDrink;
    private TextView tvCountItem;
    private TextView tvAmount;
    private TextView tvPriceVoucher;
    private TextView tvCheckout;

    private List<Drink> listDrinkCart;
    private CartAdapter cartAdapter;
    private int priceDrink;
    private int mAmount;
    private PaymentMethod paymentMethodSelected;
    private Address addressSelected;
    private Voucher voucherSelected;
<<<<<<< HEAD
    private MySharedPreferences mySharedPreferences;
    private String currentUserEmail;
=======
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

<<<<<<< HEAD
        mySharedPreferences = new MySharedPreferences(this);
        currentUserEmail = DataStoreManager.getUser().getEmail();

=======
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        initToolbar();
        initUi();
        initListener();
        initData();
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText(getString(R.string.label_cart));
    }

    private void initUi() {
        rcvCart = findViewById(R.id.rcv_cart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvCart.setLayoutManager(linearLayoutManager);
        layoutAddOrder = findViewById(R.id.layout_add_order);
        layoutPaymentMethod = findViewById(R.id.layout_payment_method);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        layoutAddress = findViewById(R.id.layout_address);
        tvAddress = findViewById(R.id.tv_address);
        layoutVoucher = findViewById(R.id.layout_voucher);
        tvVoucher = findViewById(R.id.tv_voucher);
        tvNameVoucher = findViewById(R.id.tv_name_voucher);
        tvCountItem = findViewById(R.id.tv_count_item);
        tvPriceDrink = findViewById(R.id.tv_price_drink);
        tvAmount = findViewById(R.id.tv_amount);
        tvPriceVoucher = findViewById(R.id.tv_price_voucher);
        tvCheckout = findViewById(R.id.tv_checkout);
    }

    private void initListener() {
        layoutAddOrder.setOnClickListener(v -> finish());
        layoutPaymentMethod.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            if (paymentMethodSelected != null) {
                bundle.putInt(Constant.PAYMENT_METHOD_ID, paymentMethodSelected.getId());
            }
            GlobalFunction.startActivity(CartActivity.this, PaymentMethodActivity.class, bundle);
        });

        layoutAddress.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            if (addressSelected != null) {
                bundle.putLong(Constant.ADDRESS_ID, addressSelected.getId());
            }
            GlobalFunction.startActivity(CartActivity.this, AddressActivity.class, bundle);
        });

        layoutVoucher.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.AMOUNT_VALUE, priceDrink);
            if (voucherSelected != null) {
                bundle.putInt(Constant.VOUCHER_ID, voucherSelected.getId());
            }
            GlobalFunction.startActivity(CartActivity.this, VoucherActivity.class, bundle);
        });

        tvCheckout.setOnClickListener(view -> {
            if (listDrinkCart == null || listDrinkCart.isEmpty()) return;
            if (paymentMethodSelected == null) {
                showToastMessage(getString(R.string.label_choose_payment_method));
                return;
            }
            if (addressSelected == null) {
                showToastMessage(getString(R.string.label_choose_address));
                return;
            }
<<<<<<< HEAD

=======
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be
            Order orderBooking = new Order();
            orderBooking.setId(System.currentTimeMillis());
            orderBooking.setUserEmail(DataStoreManager.getUser().getEmail());
            orderBooking.setDateTime(String.valueOf(System.currentTimeMillis()));
            List<DrinkOrder> drinks = new ArrayList<>();
            for (Drink drink : listDrinkCart) {
                drinks.add(new DrinkOrder(drink.getName(), drink.getOption(), drink.getCount(),
                        drink.getPriceOneDrink(), drink.getImage()));
            }
            orderBooking.setDrinks(drinks);
            orderBooking.setPrice(priceDrink);
            if (voucherSelected != null) {
                orderBooking.setVoucher(voucherSelected.getPriceDiscount(priceDrink));
            }
            orderBooking.setTotal(mAmount);
            orderBooking.setPaymentMethod(paymentMethodSelected.getName());
            orderBooking.setAddress(addressSelected);
            orderBooking.setStatus(Order.STATUS_NEW);

            NotificationHelper.sendOrderSuccessNotification(this);

<<<<<<< HEAD
            // Clear cart items after checkout
            clearCartItems();

=======
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.ORDER_OBJECT, orderBooking);
            GlobalFunction.startActivity(CartActivity.this, PaymentActivity.class, bundle);
        });
    }

    private void initData() {
<<<<<<< HEAD
        listDrinkCart = getCartItems();
        if (listDrinkCart == null || listDrinkCart.isEmpty()) {
            listDrinkCart = DrinkDatabase.getInstance(this).drinkDAO().getListDrinkCart();
        }
=======
        listDrinkCart = new ArrayList<>();
        listDrinkCart = DrinkDatabase.getInstance(this).drinkDAO().getListDrinkCart();
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be
        if (listDrinkCart == null || listDrinkCart.isEmpty()) {
            return;
        }
        cartAdapter = new CartAdapter(listDrinkCart, new CartAdapter.IClickCartListener() {
            @Override
            public void onClickDeleteItem(Drink drink, int position) {
                DrinkDatabase.getInstance(CartActivity.this).drinkDAO().deleteDrink(drink);
                listDrinkCart.remove(position);
                cartAdapter.notifyItemRemoved(position);
<<<<<<< HEAD
                displayCountItemCart();
                calculateTotalPrice();
                saveCartItems(listDrinkCart);
=======

                displayCountItemCart();
                calculateTotalPrice();
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be
                EventBus.getDefault().post(new DisplayCartEvent());
            }

            @Override
            public void onClickUpdateItem(Drink drink, int position) {
                DrinkDatabase.getInstance(CartActivity.this).drinkDAO().updateDrink(drink);
                cartAdapter.notifyItemChanged(position);
<<<<<<< HEAD
                calculateTotalPrice();
                saveCartItems(listDrinkCart);
=======

                calculateTotalPrice();
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be
                EventBus.getDefault().post(new DisplayCartEvent());
            }

            @Override
            public void onClickEditItem(Drink drink) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constant.DRINK_ID, drink.getId());
                bundle.putSerializable(Constant.DRINK_OBJECT, drink);
                GlobalFunction.startActivity(CartActivity.this, DrinkDetailActivity.class, bundle);
            }
        });
        rcvCart.setAdapter(cartAdapter);
        calculateTotalPrice();
        displayCountItemCart();
    }

    private void displayCountItemCart() {
        String strCountItem = "(" + listDrinkCart.size() + " " + getString(R.string.label_item) + ")";
        tvCountItem.setText(strCountItem);
    }

    private void calculateTotalPrice() {
        if (listDrinkCart == null || listDrinkCart.isEmpty()) {
            String strZero = 0 + Constant.CURRENCY;
            priceDrink = 0;
            tvPriceDrink.setText(strZero);

            mAmount = 0;
            tvAmount.setText(strZero);
            return;
        }

        int totalPrice = 0;
        for (Drink drink : listDrinkCart) {
            totalPrice = totalPrice + drink.getTotalPrice();
        }

        priceDrink = totalPrice;
        String strPriceDrink = priceDrink + Constant.CURRENCY;
        tvPriceDrink.setText(strPriceDrink);

        mAmount = totalPrice;
        if (voucherSelected != null) {
            mAmount = mAmount - voucherSelected.getPriceDiscount(priceDrink);
        }
        String strAmount = mAmount + Constant.CURRENCY;
        tvAmount.setText(strAmount);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPaymentMethodSelectedEvent(PaymentMethodSelectedEvent event) {
        if (event.getPaymentMethod() != null) {
            paymentMethodSelected = event.getPaymentMethod();
            tvPaymentMethod.setText(paymentMethodSelected.getName());
        } else {
            tvPaymentMethod.setText(getString(R.string.label_no_payment_method));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddressSelectedEvent(AddressSelectedEvent event) {
        if (event.getAddress() != null) {
            addressSelected = event.getAddress();
            tvAddress.setText(addressSelected.getAddress());
        } else {
            tvAddress.setText(getString(R.string.label_no_address));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVoucherSelectedEvent(VoucherSelectedEvent event) {
        if (event.getVoucher() != null) {
            voucherSelected = event.getVoucher();
            tvVoucher.setText(voucherSelected.getTitle());
            tvNameVoucher.setText(voucherSelected.getTitle());
            String strPriceVoucher = "-" + voucherSelected.getPriceDiscount(priceDrink)
                    + Constant.CURRENCY;
            tvPriceVoucher.setText(strPriceVoucher);
        } else {
            tvVoucher.setText(getString(R.string.label_no_voucher));
            tvNameVoucher.setText(getString(R.string.label_no_voucher));
            String strPriceVoucher = "-0" + Constant.CURRENCY;
            tvPriceVoucher.setText(strPriceVoucher);
        }
        calculateTotalPrice();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderSuccessEvent(OrderSuccessEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddressDeletedEvent(AddressDeletedEvent event) {
        if (addressSelected != null && addressSelected.getId() == event.getAddressId()) {
            addressSelected = null;
            tvAddress.setText(getString(R.string.label_no_address));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddressUpdatedEvent(AddressUpdatedEvent event) {
        if (addressSelected != null && addressSelected.getId() == event.getAddress().getId()) {
            addressSelected = event.getAddress();
            tvAddress.setText(addressSelected.getAddress());
        }
    }

<<<<<<< HEAD
    private void saveCartItems(List<Drink> cartItems) {
        if (currentUserEmail != null && !currentUserEmail.isEmpty()) {
            Gson gson = new Gson();
            String json = gson.toJson(cartItems);
            mySharedPreferences.putStringValue(currentUserEmail + "_cart", json);
        }
    }

    private List<Drink> getCartItems() {
        if (currentUserEmail != null && !currentUserEmail.isEmpty()) {
            String json = mySharedPreferences.getStringValue(currentUserEmail + "_cart", "");
            if (!json.isEmpty()) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Drink>>() {}.getType();
                return gson.fromJson(json, type);
            }
        }
        return new ArrayList<>();
    }

    private void clearCartItems() {
        if (currentUserEmail != null && !currentUserEmail.isEmpty()) {
            mySharedPreferences.removeValue(currentUserEmail + "_cart");
        }
    }
=======
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
<<<<<<< HEAD
}

=======
}
>>>>>>> 730775bb52baef2754489b5d73080ff0f35cc5be
