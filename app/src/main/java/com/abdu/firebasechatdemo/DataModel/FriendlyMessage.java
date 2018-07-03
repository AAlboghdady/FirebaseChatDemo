package com.abdu.firebasechatdemo.DataModel;

/**
 * Created by Abdullah on 6/1/2018.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class FriendlyMessage implements Parcelable {


    public final static Parcelable.Creator<FriendlyMessage> CREATOR = new Creator<FriendlyMessage>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FriendlyMessage createFromParcel(Parcel in) {
            return new FriendlyMessage(in);
        }

        public FriendlyMessage[] newArray(int size) {
            return (new FriendlyMessage[size]);
        }

    };
    private String createTimeStamp;
    private String deliverTimeStamp;
    private String message;
    private String messageType;
    private String outboundTimeStamp;
    private String owner;
    private String readTimeStamp;

    protected FriendlyMessage(Parcel in) {
        this.createTimeStamp = ((String) in.readValue((String.class.getClassLoader())));
        this.deliverTimeStamp = ((String) in.readValue((String.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.messageType = ((String) in.readValue((String.class.getClassLoader())));
        this.outboundTimeStamp = ((String) in.readValue((String.class.getClassLoader())));
        this.owner = ((String) in.readValue((String.class.getClassLoader())));
        this.readTimeStamp = ((String) in.readValue((String.class.getClassLoader())));
    }

    public FriendlyMessage() {
    }

    public String getCreateTimeStamp() {
        return createTimeStamp;
    }

    public void setCreateTimeStamp(String createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    public String getDeliverTimeStamp() {
        return deliverTimeStamp;
    }

    public void setDeliverTimeStamp(String deliverTimeStamp) {
        this.deliverTimeStamp = deliverTimeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getOutboundTimeStamp() {
        return outboundTimeStamp;
    }

    public void setOutboundTimeStamp(String outboundTimeStamp) {
        this.outboundTimeStamp = outboundTimeStamp;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getReadTimeStamp() {
        return readTimeStamp;
    }

    public void setReadTimeStamp(String readTimeStamp) {
        this.readTimeStamp = readTimeStamp;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(createTimeStamp);
        dest.writeValue(deliverTimeStamp);
        dest.writeValue(message);
        dest.writeValue(messageType);
        dest.writeValue(outboundTimeStamp);
        dest.writeValue(owner);
        dest.writeValue(readTimeStamp);
    }

    public int describeContents() {
        return 0;
    }

}