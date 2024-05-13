package com.app.shopfee.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app.shopfee.MyApplication;
import com.app.shopfee.R;
import com.app.shopfee.model.Rating;
import com.app.shopfee.model.RatingReview;
import com.app.shopfee.utils.Constant;
import com.app.shopfee.utils.GlobalFunction;
import com.app.shopfee.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class RatingReviewActivity extends BaseActivity {

    private RatingBar ratingBar;
    private EditText edtReview;
    private TextView tvSendReview;

    private RatingReview ratingReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_review);

        getDataIntent();
        initToolbar();
        initUi();
        initListener();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        ratingReview = (RatingReview) bundle.get(Constant.RATING_REVIEW_OBJECT);
    }

    private void initUi() {
        ratingBar = findViewById(R.id.ratingbar);
        ratingBar.setRating(5f);
        edtReview = findViewById(R.id.edt_review);
        tvSendReview = findViewById(R.id.tv_send_review);

        TextView tvMessageReview = findViewById(R.id.tv_message_review);
        if (RatingReview.TYPE_RATING_REVIEW_DRINK == ratingReview.getType()) {
            tvMessageReview.setText(getString(R.string.label_rating_review_drink));
        } else if (RatingReview.TYPE_RATING_REVIEW_ORDER == ratingReview.getType()) {
            tvMessageReview.setText(getString(R.string.label_rating_review_order));
        }
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText(getString(R.string.ratings_and_reviews));
    }

    private void initListener() {
        tvSendReview.setOnClickListener(v -> {
            float rate = ratingBar.getRating();
            String review = edtReview.getText().toString().trim();
            Rating rating = new Rating(review, Double.parseDouble(String.valueOf(rate)));
            if (RatingReview.TYPE_RATING_REVIEW_DRINK == ratingReview.getType()) {
                sendRatingDrink(rating);
            } else if (RatingReview.TYPE_RATING_REVIEW_ORDER == ratingReview.getType()) {
                sendRatingOrder(rating);
            }
        });
    }

    private void sendRatingDrink(Rating rating) {
        MyApplication.get(this).getRatingDrinkDatabaseReference(ratingReview.getId())
                .child(String.valueOf(GlobalFunction.encodeEmailUser()))
                .setValue(rating, (error, ref) -> {
                    showToastMessage(getString(R.string.msg_send_review_success));
                    ratingBar.setRating(5f);
                    edtReview.setText("");
                    Utils.hideSoftKeyboard(RatingReviewActivity.this);
                });
    }

    private void sendRatingOrder(Rating rating) {
        Map<String, Object> map = new HashMap<>();
        map.put("rate", rating.getRate());
        map.put("review", rating.getReview());

        MyApplication.get(this).getOrderDatabaseReference()
                .child(String.valueOf(ratingReview.getId()))
                .updateChildren(map, (error, ref) -> {
                    showToastMessage(getString(R.string.msg_send_review_success));
                    ratingBar.setRating(5f);
                    edtReview.setText("");
                    Utils.hideSoftKeyboard(RatingReviewActivity.this);
                });
    }
}
