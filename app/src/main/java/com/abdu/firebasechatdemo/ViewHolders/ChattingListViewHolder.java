package com.abdu.firebasechatdemo.ViewHolders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.abdu.firebasechatdemo.R;

/**
 * Created by Abdullah on 6/1/2018.
 */

public class ChattingListViewHolder extends RecyclerView.ViewHolder {
    public TextView userName;
    CircularImageView userImage;
    public ConstraintLayout cell;
    public ChattingListViewHolder(View itemView) {
        super(itemView);
        userName = (TextView) itemView.findViewById(R.id.users_chatting_list_username_tv);
        userImage = (CircularImageView) itemView.findViewById(R.id.users_chatting_list_user_profile_img);
        cell = (ConstraintLayout)itemView.findViewById(R.id.item_users_chatting_list_cell);
    }
}
