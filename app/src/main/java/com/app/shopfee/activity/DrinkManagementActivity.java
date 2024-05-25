package com.app.shopfee.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.adapter.CategoryManagementPagerAdapter;
import com.app.shopfee.event.SearchKeywordEvent;
import com.app.shopfee.listener.IClickDrinkListener;
import com.app.shopfee.model.Category;
import com.app.shopfee.model.Drink;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.StringUtil;
import com.app.shopfee.widget.CustomTabLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DrinkManagementActivity extends BaseActivity implements IClickDrinkListener {
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_PERMISSION_CAMERA = 100;

    private EditText edtSearchName;
    private ViewPager2 viewPagerCategory;
    private CustomTabLayout tabCategory;
    private CategoryManagementPagerAdapter categoryPagerAdapter;
    private List<Category> categoryList;
    private List<Drink> drinkList;
    private Uri photoUri;
    private String currentPhotoPath;
    private EditText edtImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinks_management);

        initToolbar();
        initUi();
        loadCategoriesFromFirebase();
        loadDrinksFromFirebase();

        Button btnAddDrink = findViewById(R.id.btn_add_drink);
        btnAddDrink.setOnClickListener(view -> onClickAddDrink());
    }

    private void loadDrinksFromFirebase() {
        DatabaseReference drinksRef = FirebaseDatabase.getInstance().getReference("drink");
        drinksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (drinkList != null) {
                    drinkList.clear();
                } else {
                    drinkList = new ArrayList<>();
                }
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Drink drink = postSnapshot.getValue(Drink.class);
                    if (drink != null) {
                        drinkList.add(drink);
                    }
                }
                // Notify fragments about data change
                categoryPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void onClickAddDrink() {
        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_add_drink, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        // init ui
        EditText edtName = viewDialog.findViewById(R.id.edt_name);
        EditText edtDescription = viewDialog.findViewById(R.id.edt_description);
        EditText edtPrice = viewDialog.findViewById(R.id.edt_price);
        edtImage = viewDialog.findViewById(R.id.edt_image);
        EditText edtSale = viewDialog.findViewById(R.id.edt_sale);
        Spinner spinnerCategory = viewDialog.findViewById(R.id.spinner_category);
        CheckBox checkboxFeatured = viewDialog.findViewById(R.id.checkbox_featured);
        TextView tvCancel = viewDialog.findViewById(R.id.tv_cancel);
        TextView tvAdd = viewDialog.findViewById(R.id.tv_add);
        Button btnSelectImage = viewDialog.findViewById(R.id.btn_select_image);
        Button btnCaptureImage = viewDialog.findViewById(R.id.btn_capture_image);

        // Load categories into spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getCategoryNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set listeners
        tvCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        btnSelectImage.setOnClickListener(v -> selectImageFromGallery());

        btnCaptureImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            } else {
                captureImage();
            }
        });

        tvAdd.setOnClickListener(v -> {
            String strName = edtName.getText().toString().trim();
            String strDescription = edtDescription.getText().toString().trim();
            String strPrice = edtPrice.getText().toString().trim();
            String strImage = edtImage.getText().toString().trim();
            String strSale = edtSale.getText().toString().trim();
            String strCategory = spinnerCategory.getSelectedItem().toString();
            boolean isFeatured = checkboxFeatured.isChecked();

            if (StringUtil.isEmpty(strName) || StringUtil.isEmpty(strDescription) ||
                    StringUtil.isEmpty(strPrice) || StringUtil.isEmpty(strImage)) {
                GlobalFunction.showToastMessage(this, getString(R.string.message_enter_infor));
            } else {
                int price = Integer.parseInt(strPrice);
                int sale = StringUtil.isEmpty(strSale) ? 0 : Integer.parseInt(strSale);
                int categoryId = getCategoryIdByName(strCategory);

                DatabaseReference drinksRef = MyApplication.get(this).getDrinkDatabaseReference();

                // Lấy ID cuối cùng
                drinksRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int lastId = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            lastId = Integer.parseInt(dataSnapshot.getKey());
                        }
                        int newId = lastId + 1;

                        Drink drink = new Drink();
                        drink.setId(newId);
                        drink.setName(strName);
                        drink.setDescription(strDescription);
                        drink.setPrice(price);
                        drink.setImage(strImage);
                        drink.setBanner(strImage); // Banner is set to the same value as image
                        drink.setSale(sale);
                        drink.setCategory_id(categoryId);
                        drink.setFeatured(isFeatured);

                        drinksRef.child(String.valueOf(newId)).setValue(drink, (error, ref) -> {
                            if (error == null) {
                                GlobalFunction.showToastMessage(DrinkManagementActivity.this, getString(R.string.msg_add_drink_success));
                                GlobalFunction.hideSoftKeyboard(DrinkManagementActivity.this);
                                bottomSheetDialog.dismiss();
                            } else {
                                GlobalFunction.showToastMessage(DrinkManagementActivity.this, getString(R.string.msg_add_drink_error));
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        GlobalFunction.showToastMessage(DrinkManagementActivity.this, getString(R.string.msg_add_drink_error));
                    }
                });
            }
        });

        bottomSheetDialog.show();
    }

    private List<String> getCategoryNames() {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categoryList) {
            categoryNames.add(category.getName());
        }
        return categoryNames;
    }

    private int getCategoryIdByName(String name) {
        for (Category category : categoryList) {
            if (category.getName().equals(name)) {
                return category.getId();
            }
        }
        return -1; // Return -1 if not found
    }

    private void loadCategoriesFromFirebase() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("category");
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (categoryList == null) {
                    categoryList = new ArrayList<>();
                }
                categoryList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    int id = postSnapshot.child("id").getValue(Integer.class);
                    String name = postSnapshot.child("name").getValue(String.class);
                    Category category = new Category(id, name);
                    categoryList.add(category);
                }
                categoryPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void initUi() {
        tabCategory = findViewById(R.id.tab_category);
        viewPagerCategory = findViewById(R.id.view_pager_category);
        edtSearchName = findViewById(R.id.edt_search_name);

        // Khởi tạo danh sách danh mục và bộ điều hợp
        categoryList = new ArrayList<>();
        categoryPagerAdapter = new CategoryManagementPagerAdapter(this, categoryList);
        viewPagerCategory.setAdapter(categoryPagerAdapter);

        new TabLayoutMediator(tabCategory, viewPagerCategory,
                (tab, position) -> tab.setText(categoryList.get(position).getName())
        ).attach();

        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                EventBus.getDefault().post(new SearchKeywordEvent(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.drink_title));
    }

    @Override
    public void onClickDrinkItem(Drink drink) {
        // Not used here
    }

    @Override
    public void onClickDrinkItemManagement(Drink drink) {
        Intent intent = new Intent(this, DrinkDetailManagementActivity.class);
        intent.putExtra(Constant.DRINK_OBJECT, drink);
        startActivity(intent);
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Handle error
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.app.shopfee.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                GlobalFunction.showToastMessage(this, "Camera permission is required to take pictures.");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImageUri = data.getData();
                uploadImageToFirebase(selectedImageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                uploadImageToFirebase(photoUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + imageUri.getLastPathSegment());
            UploadTask uploadTask = storageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                edtImage.setText(uri.toString());
                GlobalFunction.showToastMessage(this, "Image uploaded successfully.");
            })).addOnFailureListener(e -> {
                GlobalFunction.showToastMessage(this, "Failed to upload image.");
            });
        }
    }
}
