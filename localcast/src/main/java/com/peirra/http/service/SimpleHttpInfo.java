package com.peirra.http.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kurt on 2015/11/23.
 */
public class SimpleHttpInfo implements Parcelable {

    public String ip;
    public int port;
    public String message;

    public SimpleHttpInfo() {}

    public SimpleHttpInfo(String ip, int port,String message) {
        this.ip = ip;
        this.port = port;
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ip);
        dest.writeInt(this.port);
        dest.writeString(this.message);
    }

    protected SimpleHttpInfo(Parcel in) {
        this.ip = in.readString();
        this.port = in.readInt();
        this.message = in.readString();
    }



    public static final Creator<SimpleHttpInfo> CREATOR = new Creator<SimpleHttpInfo>() {
        public SimpleHttpInfo createFromParcel(Parcel source) {
            return new SimpleHttpInfo(source);
        }

        public SimpleHttpInfo[] newArray(int size) {
            return new SimpleHttpInfo[size];
        }
    };


    @Override
    public String toString() {
        return  "[" + ip + ':' + port + "]";
    }
}
