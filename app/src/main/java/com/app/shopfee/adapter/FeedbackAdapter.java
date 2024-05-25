package com.app.shopfee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.model.Feedback;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<Feedback> feedbackList;

    public FeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_feedback_item, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);
        if (feedback != null) {
            holder.tvName.setText(feedback.getName());
            holder.tvPhone.setText(feedback.getPhone());
            holder.tvEmail.setText(feedback.getEmail());
            holder.tvComment.setText(feedback.getComment());
        }
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public void updateList(List<Feedback> newFeedbackList) {
        feedbackList = newFeedbackList;
        notifyDataSetChanged();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvPhone;
        private final TextView tvEmail;
        private final TextView tvComment;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvComment = itemView.findViewById(R.id.tv_comment);
        }
    }
}
