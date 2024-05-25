package com.app.shopfee.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.adapter.FeedbackAdapter;
import com.app.shopfee.model.Feedback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewFeedbackActivity extends BaseActivity {

    private EditText edtSearchFeedback;
    private RecyclerView rcvFeedbacks;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedback);

        initToolbar();
        initUi();
        initListener();
        loadFeedbacksFromFirebase();
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.title_feedback));
    }

    private void initUi() {
        edtSearchFeedback = findViewById(R.id.edt_search_feedback);
        rcvFeedbacks = findViewById(R.id.rcv_feedbacks);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvFeedbacks.setLayoutManager(linearLayoutManager);
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        rcvFeedbacks.setAdapter(feedbackAdapter);
    }

    private void initListener() {
        edtSearchFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterFeedbacks(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void loadFeedbacksFromFirebase() {
        MyApplication.get(this).getFeedbackDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        feedbackList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Feedback feedback = dataSnapshot.getValue(Feedback.class);
                            if (feedback != null) {
                                feedbackList.add(feedback);
                            }
                        }
                        feedbackAdapter.updateList(feedbackList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void filterFeedbacks(String query) {
        List<Feedback> filteredList = new ArrayList<>();
        for (Feedback feedback : feedbackList) {
            if (feedback.getName().toLowerCase().contains(query.toLowerCase()) ||
                    feedback.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    feedback.getComment().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(feedback);
            }
        }
        feedbackAdapter.updateList(filteredList);
    }
}
