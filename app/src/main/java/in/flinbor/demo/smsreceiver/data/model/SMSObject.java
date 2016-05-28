/*
 * Copyright 2016 Flinbor Aleksandr Bogdanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.flinbor.demo.smsreceiver.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * This class represents SMS data model
 */
public class SMSObject implements Parcelable {
    private String        sender;
    private StringBuilder content;
    private Date          timestamp;

    public SMSObject(String sender) {
        this.sender = sender;
        this.timestamp = new Date();
        this.content = new StringBuilder();
    }

    /**
     * append sms context (body) to message
     *
     * @param contents sms body
     */
    public void addContent(String contents) {
        this.content.append(contents);
    }

    public String getContent() {
        return content.toString();
    }

    public String getSender() {
        return sender;
    }

    public Date getTimestamp() {
        return timestamp;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sender);
        dest.writeSerializable(this.content);
        dest.writeLong(this.timestamp != null ? this.timestamp.getTime() : -1);
    }

    protected SMSObject(Parcel in) {
        this.sender = in.readString();
        this.content = (StringBuilder) in.readSerializable();
        long tmpTimestamp = in.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
    }

    public static final Parcelable.Creator<SMSObject> CREATOR = new Parcelable.Creator<SMSObject>() {
        @Override
        public SMSObject createFromParcel(Parcel source) {
            return new SMSObject(source);
        }

        @Override
        public SMSObject[] newArray(int size) {
            return new SMSObject[size];
        }
    };
}
