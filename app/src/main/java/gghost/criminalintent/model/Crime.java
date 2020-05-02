package gghost.criminalintent.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Crime implements Serializable {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    @Nullable
    private String mSuspect;
    private String mPhoneNumber;

    public UUID getId() {
        return mId;
    }
    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }
    public Date getDate() {
        return mDate;
    }
    public void setDate(Date date) {
        mDate = date;
    }
    public boolean isSolved() {
        return mSolved;
    }
    public void setSolved(boolean solved) {
        mSolved = solved;
    }
    @Nullable
    public String getSuspect() {
        return mSuspect;
    }
    public void setSuspect(@Nullable String suspect) {
        mSuspect = suspect;
    }
    @Nullable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public Crime() {
        /* Достаточно странная конструкция. Но в целом понятно. Мы обращяемся внутри одного конструктора
        * через "this" к другому */
        this(UUID.randomUUID());
    }
    public Crime(UUID uuid) {
        mId = uuid;
        mDate = new Date();
        mSolved = false;
        mTitle = "";
        mSuspect = null;
    }

    @NonNull
    @Override
    public String toString() {
        return this.mTitle + " (" + this.mId.toString() + ")";
    }
}
