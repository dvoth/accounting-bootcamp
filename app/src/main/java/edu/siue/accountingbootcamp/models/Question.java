package edu.siue.accountingbootcamp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Question implements Parcelable {

    private int id;
    private String text;
    private List<QuestionOption> questionOptions = null;

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

    public List<QuestionOption> getAnswers() {
        return questionOptions;
    }

    public void setAnswers(List<QuestionOption> answers) {
        this.questionOptions = answers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.text);
        dest.writeList(this.questionOptions);
    }

    public Question() {
    }

    protected Question(Parcel in) {
        this.id = in.readInt();
        this.text = in.readString();
        this.questionOptions = new ArrayList<QuestionOption>();
        in.readList(this.questionOptions, QuestionOption.class.getClassLoader());
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
