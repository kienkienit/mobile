package com.app.shopfee.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.shopfee.R;
import com.app.shopfee.activity.ChangePasswordActivity;
import com.app.shopfee.activity.ContactActivity;
import com.app.shopfee.activity.FeedbackActivity;
import com.app.shopfee.activity.LoginActivity;
import com.app.shopfee.activity.MainActivity;
import com.app.shopfee.model.User;
import com.app.shopfee.prefs.DataStoreManager;
import com.app.shopfee.utils.GlobalFunction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    private View mView;
    private LinearLayout layoutFeedback;
    private LinearLayout layoutContact;
    private LinearLayout layoutChangePassword;
    private LinearLayout layoutSignOut;
    private TextView tvUsername;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_account, container, false);

        initToolbar();
        initUi();
        initListener();
        loadUserEmail();

        return mView;
    }

    private void loadUserEmail() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    tvUsername.setText(user.getEmail());
                } else {
                    tvUsername.setText("No user email available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvUsername.setText("No user email available");
            }
        });
    }

    private void initToolbar() {
        ImageView imgToolbarBack = mView.findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = mView.findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> backToHomeScreen());
        tvToolbarTitle.setText(getString(R.string.nav_account));
    }

    private void backToHomeScreen() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) return;
        mainActivity.getViewPager2().setCurrentItem(0);
    }

    private void initUi() {
        tvUsername = mView.findViewById(R.id.tv_username);
//        tvUsername.setText(DataStoreManager.getUser().getEmail());
        layoutFeedback = mView.findViewById(R.id.layout_feedback);
        layoutContact = mView.findViewById(R.id.layout_contact);
        layoutChangePassword = mView.findViewById(R.id.layout_change_password);
        layoutSignOut = mView.findViewById(R.id.layout_sign_out);
    }

    private void initListener() {
        layoutFeedback.setOnClickListener(view ->
                GlobalFunction.startActivity(getActivity(), FeedbackActivity.class));
        layoutContact.setOnClickListener(view ->
                GlobalFunction.startActivity(getActivity(), ContactActivity.class));
        layoutChangePassword.setOnClickListener(view ->
                GlobalFunction.startActivity(getActivity(), ChangePasswordActivity.class));
        layoutSignOut.setOnClickListener(view -> onClickSignOut());
    }

    private void onClickSignOut() {
        if (getActivity() == null) return;

        FirebaseAuth.getInstance().signOut();
        DataStoreManager.setUser(null);
        GlobalFunction.startActivity(getActivity(), LoginActivity.class);
        getActivity().finishAffinity();
    }
}
