package com.app.shopfee.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.adapter.ContactAdapter;
import com.app.shopfee.constant.AboutUsConfig;
import com.app.shopfee.model.Contact;
import com.app.shopfee.utils.GlobalFunction;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends BaseActivity {


    private TextView tvAboutUsTitle, tvAboutUsContent, tvAboutUsWebsite;
    private LinearLayout layoutWebsite;
    private RecyclerView rcvData;

    private ContactAdapter mContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        initToolbar();
        initUi();
        initData();
        initListener();
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText(getString(R.string.contact));
    }

    private void initUi() {
        tvAboutUsTitle = findViewById(R.id.tv_about_us_title);
        tvAboutUsContent = findViewById(R.id.tv_about_us_content);
        tvAboutUsWebsite = findViewById(R.id.tv_about_us_website);
        layoutWebsite = findViewById(R.id.layout_website);
        rcvData = findViewById(R.id.rcvData);
    }

    private void initData() {
        tvAboutUsTitle.setText(AboutUsConfig.ABOUT_US_TITLE);
        tvAboutUsContent.setText(AboutUsConfig.ABOUT_US_CONTENT);
        tvAboutUsWebsite.setText(AboutUsConfig.ABOUT_US_WEBSITE_TITLE);

        mContactAdapter = new ContactAdapter(this, getListContact(),
                () -> GlobalFunction.callPhoneNumber(this));
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rcvData.setNestedScrollingEnabled(false);
        rcvData.setFocusable(false);
        rcvData.setLayoutManager(layoutManager);
        rcvData.setAdapter(mContactAdapter);
    }

    private void initListener() {
        layoutWebsite.setOnClickListener(view
                -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.WEBSITE))));
    }

    public List<Contact> getListContact() {
        List<Contact> contactArrayList = new ArrayList<>();
        contactArrayList.add(new Contact(Contact.FACEBOOK, R.drawable.ic_facebook));
        contactArrayList.add(new Contact(Contact.HOTLINE, R.drawable.ic_hotline));
        contactArrayList.add(new Contact(Contact.GMAIL, R.drawable.ic_gmail));
        contactArrayList.add(new Contact(Contact.SKYPE, R.drawable.ic_skype));
        contactArrayList.add(new Contact(Contact.YOUTUBE, R.drawable.ic_youtube));
        contactArrayList.add(new Contact(Contact.ZALO, R.drawable.ic_zalo));

        return contactArrayList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactAdapter.release();
    }
}
