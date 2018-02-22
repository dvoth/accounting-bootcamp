package edu.siue.accountingbootcamp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Quiz implements Parcelable {

    private int id;
    private String name;
    private int quizOrder;
    private List<Question> questions = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuizOrder() {
        return quizOrder;
    }

    public void setQuizOrder(int quizOrder) {
        this.quizOrder = quizOrder;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.quizOrder);
        dest.writeList(this.questions);
    }

    public Quiz() {
    }

    protected Quiz(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.quizOrder = in.readInt();
        this.questions = new ArrayList<Question>();
        in.readList(this.questions, Question.class.getClassLoader());
    }

    public static final Parcelable.Creator<Quiz> CREATOR = new Parcelable.Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel source) {
            return new Quiz(source);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };
}