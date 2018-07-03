package com.abdu.firebasechatdemo.DataModel;

/**
 * Created by Abdullah on 6/1/2018.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class UsersChattingList implements Parcelable {


    public final static Parcelable.Creator<UsersChattingList> CREATOR = new Creator<UsersChattingList>() {


        @SuppressWarnings({
                "unchecked"
        })
        public UsersChattingList createFromParcel(Parcel in) {
            return new UsersChattingList(in);
        }

        public UsersChattingList[] newArray(int size) {
            return (new UsersChattingList[size]);
        }

    };
    private String chatRoomId;
    private String chatRoomName;
    private String chatRoomImageUrl;

    protected UsersChattingList(Parcel in) {
        this.chatRoomId = ((String) in.readValue((String.class.getClassLoader())));
        this.chatRoomName = ((String) in.readValue((String.class.getClassLoader())));
        this.chatRoomImageUrl = ((String) in.readValue((String.class.getClassLoader())));
    }

    public UsersChattingList() {
    }

    public UsersChattingList(String s, String s1) {
        chatRoomId = s;
        chatRoomName = s1;
        chatRoomImageUrl = "";
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getChatRoomImageUrl() {
        return chatRoomImageUrl;
    }

    public void setChatRoomImageUrl(String chatRoomImageUrl) {
        this.chatRoomImageUrl = chatRoomImageUrl;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(chatRoomId);
        dest.writeValue(chatRoomName);
        dest.writeValue(chatRoomImageUrl);
    }

    public int describeContents() {
        return 0;
    }

}
