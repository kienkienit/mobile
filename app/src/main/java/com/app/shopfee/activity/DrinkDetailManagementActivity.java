package com.app.shopfee.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.model.Category;
import com.app.shopfee.model.Drink;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.StringUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DrinkDetailManagementActivity extends BaseActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private EditText edtName, edtDescription, edtPrice, edtImage, edtSale;
    private Spinner spinnerCategory;
    private CheckBox checkboxFeatured;
    private TextView tvUpdate, tvDelete;
    private Button btnSelectImage, btnCaptureImage;
    private List<Category> categoryList = new ArrayList<>();
    private Drink drink;
    private String currentPhotoPath;
    private Uri photoUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_detail_management);

        loadDataIntent();
        initToolbar();
        initUi();
        loadCategoriesFromFirebase();

        tvUpdate.setOnClickListener(v -> showUpdateConfirmationDialog());
        tvDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
        btnSelectImage.setOnClickListener(v -> selectImageFromGallery());
        btnCaptureImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                captureImage();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa đồ uống này không?")
                .setPositiveButton("Có", (dialog, which) -> onDeleteDrink())
                .setNegativeButton("Không", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showUpdateConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận cập nhật")
                .setMessage("Bạn có chắc chắn muốn cập nhật đồ uống này không?")
                .setPositiveButton("Có", (dialog, which) -> onUpdateDrink())
                .setNegativeButton("Không", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void onDeleteDrink() {
        DatabaseReference drinksRef = MyApplication.get(this).getDrinkDatabaseReference();
        drinksRef.child(String.valueOf(drink.getId())).removeValue((error, ref) -> {
            if (error == null) {
                GlobalFunction.showToastMessage(DrinkDetailManagementActivity.this, getString(R.string.msg_delete_drink_success));
                finish();
            } else {
                GlobalFunction.showToastMessage(DrinkDetailManagementActivity.this, getString(R.string.msg_delete_drink_error));
            }
        });
    }

    private void onUpdateDrink() {
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
            try {
                int price = Integer.parseInt(strPrice);
                int sale = StringUtil.isEmpty(strSale) ? 0 : Integer.parseInt(strSale);
                int categoryId = getCategoryIdByName(strCategory);

                drink.setName(strName);
                drink.setDescription(strDescription);
                drink.setPrice(price);
                drink.setImage(strImage);
                drink.setBanner(strImage); // Banner is set to the same value as image
                drink.setSale(sale);
                drink.setCategory_id(categoryId);
                drink.setFeatured(isFeatured);

                DatabaseReference drinksRef = MyApplication.get(this).getDrinkDatabaseReference();
                drinksRef.child(String.valueOf(drink.getId())).setValue(drink, (error, ref) -> {
                    if (error == null) {
                        GlobalFunction.showToastMessage(DrinkDetailManagementActivity.this, getString(R.string.msg_update_drink_success));
                        finish();
                    } else {
                        GlobalFunction.showToastMessage(DrinkDetailManagementActivity.this, getString(R.string.msg_update_drink_error));
                    }
                });
            } catch (NumberFormatException e) {
                GlobalFunction.showToastMessage(this, getString(R.string.message_enter_valid_number));
            }
        }
    }

    private void populateData(Drink drink) {
        edtName.setText(drink.getName());
        edtDescription.setText(drink.getDescription());
        edtPrice.setText(String.valueOf(drink.getPrice()));
        edtImage.setText(drink.getImage());
        edtSale.setText(String.valueOf(drink.getSale()));
        checkboxFeatured.setChecked(drink.isFeatured());

        int categoryId = drink.getCategory_id();
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == categoryId) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    private void loadCategoriesFromFirebase() {
        DatabaseReference categoriesRef = MyApplication.get(this).getCategoryDatabaseReference();
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int id = dataSnapshot.child("id").getValue(Integer.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    Category category = new Category(id, name);
                    categoryList.add(category);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(DrinkDetailManagementActivity.this, android.R.layout.simple_spinner_item, getCategoryNames());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);

                // Ensure the data is populated after loading the categories
                if (drink != null) {
                    populateData(drink);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(DrinkDetailManagementActivity.this, getString(R.string.msg_load_category_error));
            }
        });
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

    private void initUi() {
        edtName = findViewById(R.id.edt_name);
        edtDescription = findViewById(R.id.edt_description);
        edtPrice = findViewById(R.id.edt_price);
        edtImage = findViewById(R.id.edt_image);
        edtSale = findViewById(R.id.edt_sale);
        spinnerCategory = findViewById(R.id.spinner_category);
        checkboxFeatured = findViewById(R.id.checkbox_featured);
        tvUpdate = findViewById(R.id.tv_update);
        tvDelete = findViewById(R.id.tv_delete);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnCaptureImage = findViewById(R.id.btn_capture_image);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.drink_detail_title));
    }

    private void loadDataIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            drink = (Drink) intent.getSerializableExtra(Constant.DRINK_OBJECT);
        }
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
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.app.shopfee.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null && data.getData() != null) {
                photoUri = data.getData();
                uploadImageToFirebaseStorage(photoUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                uploadImageToFirebaseStorage(photoUri);
            }
        }
    }

    private void uploadImageToFirebaseStorage(Uri uri) {
        if (uri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("images/" + uri.getLastPathSegment());
            imageRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    edtImage.setText(downloadUri.toString());
                                    GlobalFunction.showToastMessage(DrinkDetailManagementActivity.this, "Tải ảnh lên thành công.");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            GlobalFunction.showToastMessage(DrinkDetailManagementActivity.this, "Lỗi khi tải ảnh lên.");
                        }
                    });
        }
    }
}
