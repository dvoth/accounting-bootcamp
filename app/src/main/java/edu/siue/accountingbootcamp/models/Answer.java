package edu.siue.accountingbootcamp.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "answers",
        foreignKeys = {
                @ForeignKey(entity = Quiz.class,
                        parentColumns = "id",
                        childColumns = "quizId",
                        onDelete = CASCADE),
                @ForeignKey(entity = Question.class,
                        parentColumns = "id",
                        childColumns = "questionId",
                        onDelete = CASCADE)
        })
public class Answer implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int quizId;
    private int questionId;
    private String text;
    private Boolean isanswer;
    private Boolean selectedAnswer;

    @Ignore
    private String column;

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

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }


    public Boolean isSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(Boolean selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
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
        dest.writeValue(this.isanswer);
        dest.writeString(this.column);
    }

    public Answer() {
        selectedAnswer = false;
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