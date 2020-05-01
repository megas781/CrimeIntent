package gghost.criminalintent.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Crime implements Serializable {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequiresPolice;

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
    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }
    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }

    public Crime() {
        /* Достаточно странная конструкция. Но в целом понятно. Мы обращяемся внутри одного конструктора
        * через "this" к другому */
        this(UUID.randomUUID());
    }
    public Crime(UUID uuid) {
        mId = uuid;
        mDate = new Date();
    }

    @NonNull
    @Override
    public String toString() {
        return this.mTitle + " (" + this.mId.toString() + ")";
    }
}
