package edu.siue.accountingbootcamp.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "questions",
        foreignKeys =
                @ForeignKey(entity = Quiz.class,
                        parentColumns = "id",
                        childColumns = "quizId",
                        onDelete = CASCADE))
public class Question implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int quizId;
    private String text;
    private boolean answeredCorrectly;

    @Ignore
    private List<Answer> answers = null;

    /*
        GETTERS AND SETTERS
     */
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

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public boolean isAnsweredCorrectly() {
        return answeredCorrectly;
    }

    public void setAnsweredCorrectly(boolean answeredCorrectly) {
        this.answeredCorrectly = answeredCorrectly;
    }

    /*
        PARCELABLE INTERFACE METHODS
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.text);
        dest.writeList(this.answers);
    }

    public Question() {
    }

    protected Question(Parcel in) {
        this.id = in.readInt();
        this.text = in.readString();
        this.answers = new ArrayList<Answer>();
        in.readList(this.answers, Answer.class.getClassLoader());
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
