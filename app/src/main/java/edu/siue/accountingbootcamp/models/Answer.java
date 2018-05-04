package edu.siue.accountingbootcamp.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "answers")
public class Answer implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String text;
    private Boolean isanswer;
    private String column;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getIsanswer() {
        return isanswer;
    }

    public void setIsanswer(Boolean isanswer) {
        this.isanswer = isanswer;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.text);
        dest.writeValue(this.isanswer);
        dest.writeString(this.column);
    }

    public Answer() {
    }

    protected Answer(Parcel in) {
        this.id = in.readInt();
        this.text = in.readString();
        this.isanswer = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.column = in.readString();
    }

    public static final Parcelable.Creator<Answer> CREATOR = new Parcelable.Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel source) {
            return new Answer(source);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };
}