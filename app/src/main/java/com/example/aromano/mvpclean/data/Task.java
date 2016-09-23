package com.example.aromano.mvpclean.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by aRomano on 20/09/2016.
 */

public class Task implements Parcelable {

    private String id;
    private String title;
    private String description;
    private boolean isCompleted;
    private long timestamp;

    public Task(String title) {
        this(title, "");
    }

    public Task(String title, String description) {
        this(UUID.randomUUID().toString(), title, description, false, Calendar.getInstance().getTimeInMillis());
    }

    public Task(String id, String title, String description, boolean isCompleted, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.timestamp = timestamp;
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void resetTimestamp() {
        this.timestamp = Calendar.getInstance().getTimeInMillis();
    }

    public String getFormattedTimestamp() {
        String formatted;

        Calendar createdDate = Calendar.getInstance();
        createdDate.setTimeInMillis(timestamp);
        Calendar current = Calendar.getInstance();

        long diff = current.getTimeInMillis() - createdDate.getTimeInMillis();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 1) {
            formatted = days + " days ago";
        } else if (hours > 1) {
            formatted = hours + " hours ago";
        } else if (minutes > 1) {
            formatted = minutes + " minutes ago";
        } else {
            formatted = seconds + " seconds ago";
        }

        return formatted;
    }


    // parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByte((byte) (isCompleted ? 1 : 0));
        dest.writeLong(timestamp);
    }

    private Task(Parcel source) {
        this.id = source.readString();
        this.title = source.readString();
        this.description = source.readString();
        this.isCompleted = source.readByte() != 0;
        this.timestamp = source.readLong();
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
