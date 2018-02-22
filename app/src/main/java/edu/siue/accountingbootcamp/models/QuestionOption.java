package edu.siue.accountingbootcamp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class QuestionOption implements Parcelable {

    private int id;
    private String text;
    private Boolean correctAnswer;
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

    public Boolean getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
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
        dest.writeValue(this.correctAnswer);
        dest.writeString(this.column);
    }

    public QuestionOption() {
    }

    protected QuestionOption(Parcel in) {
        this.id = in.readInt();
        this.text = in.readString();
        this.correctAnswer = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.column = in.readString();
    }

    public static final Parcelable.Creator<QuestionOption> CREATOR = new Parcelable.Creator<QuestionOption>() {
        @Override
        public QuestionOption createFromParcel(Parcel source) {
            return new QuestionOption(source);
        }

        @Override
        public QuestionOption[] newArray(int size) {
            return new QuestionOption[size];
        }
    };
}