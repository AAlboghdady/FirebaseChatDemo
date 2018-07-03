package com.abdu.firebasechatdemo.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.abdu.firebasechatdemo.R;

/**
 * Created by Abdullah on 6/4/2018.
 */

public class ChattingMessages extends RecyclerView.ViewHolder {
    public TextView message;

    public ChattingMessages(View itemView) {
        super(itemView);
        message = (TextView) itemView.findViewById(R.id.chatMessage);

    }
}
