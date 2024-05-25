package com.app.shopfee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shopfee.R;
import com.app.shopfee.model.Contact;
import com.app.shopfee.utils.GlobalFunction;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private Context context;
    private final List<Contact> listContact;
    private final ICallPhone iCallPhone;

    public interface ICallPhone {
        void onClickCallPhone();
    }

    public ContactAdapter(Context context, List<Contact> listContact, ICallPhone iCallPhone) {
        this.context = context;
        this.listContact = listContact;
        this.iCallPhone = iCallPhone;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = listContact.get(position);
        if (contact == null) {
            return;
        }
        holder.imgContact.setImageResource(contact.getImage());
        switch (contact.getId()) {
            case Contact.FACEBOOK:
                holder.tvContact.setText(context.getString(R.string.label_facebook));
                break;

            case Contact.HOTLINE:
                holder.tvContact.setText(context.getString(R.string.label_call));
                break;

            case Contact.GMAIL:
                holder.tvContact.setText(context.getString(R.string.label_gmail));
                break;

            case Contact.SKYPE:
                holder.tvContact.setText(context.getString(R.string.label_skype));
                break;

            case Contact.YOUTUBE:
                holder.tvContact.setText(context.getString(R.string.label_youtube));
                break;

            case Contact.ZALO:
                holder.tvContact.setText(context.getString(R.string.label_zalo));
                break;
        }

        holder.layoutItem.setOnClickListener(v -> {
            switch (contact.getId()) {
                case Contact.FACEBOOK:
                    GlobalFunction.onClickOpenFacebook(context);
                    break;

                case Contact.HOTLINE:
                    iCallPhone.onClickCallPhone();
                    break;

                case Contact.GMAIL:
                    GlobalFunction.onClickOpenGmail(context);
                    break;

                case Contact.SKYPE:
                    GlobalFunction.onClickOpenSkype(context);
                    break;

                case Contact.YOUTUBE:
                    GlobalFunction.onClickOpenYoutubeChannel(context);
                    break;

                case Contact.ZALO:
                    GlobalFunction.onClickOpenZalo(context);
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == listContact ? 0 : listContact.size();
    }

    public void release() {
        context = null;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout layoutItem;
        private final ImageView imgContact;
        private final TextView tvContact;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            imgContact = itemView.findViewById(R.id.img_contact);
            tvContact = itemView.findViewById(R.id.tv_contact);
        }
    }
}