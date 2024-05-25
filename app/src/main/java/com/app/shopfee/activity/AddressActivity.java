package com.app.shopfee.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.adapter.AddressAdapter;
import com.app.shopfee.event.AddressDeletedEvent;
import com.app.shopfee.event.AddressSelectedEvent;
import com.app.shopfee.event.AddressUpdatedEvent;
import com.app.shopfee.model.Address;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.StringUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends BaseActivity {

    private List<Address> listAddress;
    private AddressAdapter addressAdapter;
    private long addressSelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        loadDataIntent();
        initToolbar();
        initUi();
        loadListAddressFromFirebase();
    }

    private void loadDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        addressSelectedId = bundle.getLong(Constant.ADDRESS_ID, 0);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.address_title));
    }

    private void initUi() {
        RecyclerView rcvAddress = findViewById(R.id.rcv_address);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvAddress.setLayoutManager(linearLayoutManager);
        listAddress = new ArrayList<>();
//        addressAdapter = new AddressAdapter(listAddress, this::handleClickAddress);
        addressAdapter = new AddressAdapter(listAddress, new AddressAdapter.IClickAddressListener() {
            @Override
            public void onClickAddressItem(Address address) {
                handleClickAddress(address);
            }
            @Override
            public void onLongClickAddressItem(Address address, View anchorView) {
                showOptionsDialog(address, anchorView);
            }
            @Override
            public void onEditAddressItem(Address address) {
                showEditAddressForm(address);
            }
            @Override
            public void onDeleteAddressItem(Address address) {
                deleteAddress(address);
            }
        });

        rcvAddress.setAdapter(addressAdapter);

        Button btnAddAddress = findViewById(R.id.btn_add_address);
        btnAddAddress.setOnClickListener(view -> onClickAddAddress());
    }

    private void deleteAddress(Address address) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    MyApplication.get(this).getAddressDatabaseReference().child(String.valueOf(address.getId()))
                            .removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    EventBus.getDefault().post(new AddressDeletedEvent(address.getId()));
                                    showToastMessage("Địa chỉ đã được xóa.");
                                    loadListAddressFromFirebase();
                                } else {
                                    showToastMessage("Lỗi khi xóa địa chỉ.");
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditAddressForm(Address address) {
        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_add_address, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(viewDialog);

        TextView edtName = viewDialog.findViewById(R.id.edt_name);
        TextView edtPhone = viewDialog.findViewById(R.id.edt_phone);
        TextView edtAddress = viewDialog.findViewById(R.id.edt_address);

        // Điền thông tin hiện tại của địa chỉ vào form
        edtName.setText(address.getName());
        edtPhone.setText(address.getPhone());
        edtAddress.setText(address.getAddress());

        TextView tvAdd = viewDialog.findViewById(R.id.tv_add);
        tvAdd.setText(getString(R.string.update)); // Thay đổi text nút thêm thành cập nhật
        tvAdd.setOnClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get current user ID
            Address newAddress = new Address(address.getId(), edtName.getText().toString(), edtPhone.getText().toString(), edtAddress.getText().toString(), currentUserId);
            updateAddress(newAddress);
            bottomSheetDialog.dismiss();
        });


        TextView tvCancel = viewDialog.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void updateAddress(Address address) {
        MyApplication.get(this).getAddressDatabaseReference().child(String.valueOf(address.getId()))
                .setValue(address)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        EventBus.getDefault().post(new AddressUpdatedEvent(address));
                        showToastMessage("Địa chỉ đã được cập nhật.");
                    } else {
                        showToastMessage("Lỗi khi cập nhật địa chỉ.");
                    }
                });
    }


    private void showOptionsDialog(Address address, View anchor) {
//        View anchor = findViewById(R.id.rcv_address); // Lấy view làm neo cho PopupMenu, ở đây có thể là item view được nhấn giữ
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.inflate(R.menu.address_menu); // Giả sử bạn đã tạo menu XML với id là address_options_menu

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    showEditAddressForm(address);
                    return true;
                case R.id.action_delete:
                    deleteAddress(address);
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadListAddressFromFirebase() {
        showProgressDialog(true);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        MyApplication.get(this).getAddressDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showProgressDialog(false);
                        resetListAddress();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Address address = dataSnapshot.getValue(Address.class);
                            if (address != null && address.getUserId().equals(currentUserId)) {
                                listAddress.add(0, address);
                            }
                        }

                        if (addressSelectedId > 0 && listAddress != null && !listAddress.isEmpty()) {
                            for (Address address : listAddress) {
                                if (address.getId() == addressSelectedId) {
                                    address.setSelected(true);
                                    break;
                                }
                            }
                        }

                        if (addressAdapter != null) addressAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showProgressDialog(false);
                        showToastMessage(getString(R.string.msg_get_date_error));
                    }
                });
    }

    private void resetListAddress() {
        if (listAddress != null) {
            listAddress.clear();
        } else {
            listAddress = new ArrayList<>();
        }
    }

    private void handleClickAddress(Address address) {
        EventBus.getDefault().post(new AddressSelectedEvent(address));
        finish();
    }

    @SuppressLint("InflateParams, MissingInflatedId")
    public void onClickAddAddress() {
        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_add_address, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        // init ui
        TextView edtName = viewDialog.findViewById(R.id.edt_name);
        TextView edtPhone = viewDialog.findViewById(R.id.edt_phone);
        TextView edtAddress = viewDialog.findViewById(R.id.edt_address);
        TextView tvCancel = viewDialog.findViewById(R.id.tv_cancel);
        TextView tvAdd = viewDialog.findViewById(R.id.tv_add);

        // Set listener
        tvCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        tvAdd.setOnClickListener(v -> {
            String strName = edtName.getText().toString().trim();
            String strPhone = edtPhone.getText().toString().trim();
            String strAddress = edtAddress.getText().toString().trim();

            if (StringUtil.isEmpty(strName) || StringUtil.isEmpty(strPhone) || StringUtil.isEmpty(strAddress)) {
                GlobalFunction.showToastMessage(this, getString(R.string.message_enter_infor));
            } else {
                long id = System.currentTimeMillis();
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Address address = new Address(id, strName, strPhone, strAddress, currentUserId);
                MyApplication.get(this).getAddressDatabaseReference()
                        .child(String.valueOf(id))
                        .setValue(address, (error1, ref1) -> {
                            GlobalFunction.showToastMessage(this,
                                    getString(R.string.msg_add_address_success));
                            GlobalFunction.hideSoftKeyboard(this);
                            bottomSheetDialog.dismiss();
                        });
            }
        });

        bottomSheetDialog.show();
    }
}
