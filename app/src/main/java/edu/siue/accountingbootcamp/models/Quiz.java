package edu.siue.accountingbootcamp.models;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "quizzes")
public class Quiz extends ViewModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int quizOrder;

    // Serialized name to put results of questions from the API to this List field so we can later send to MutableLiveData object
    @Ignore
    @SerializedName("questions")
    private List<Question> questionsFromApi;

    @Ignore
    // 'transient' means that this field will not be serialized
    // Done because we can't put questions from the api directly into a MutableLiveData object
    private transient MutableLiveData<List<Question>> questions = new MutableLiveData<>();

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
        return questions.getValue();
    }

    public void setQuestions(List<Question> questionse) {
        this.questions.postValue(questionse);
    }

    public int getPercentage() {
        int correct = getCorrectAnswersCount();
        int size = questions.getValue().size();
        double answer = ((double) correct/size) * 100;
        return (int) Math.round(answer);
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
        dest.writeList(this.questions.getValue());
    }

    public Quiz() {
    }

    public Quiz(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.quizOrder = in.readInt();
        in.readValue(Question.class.getClassLoader());
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

    public int getCorrectAnswersCount() {
        int numCorrect = 0;
        for (Question question : questions.getValue()) {
            if (question.isAnsweredCorrectly()) {
                numCorrect++;
            }
        }

        return numCorrect;
    }

    public List<Question> getQuestionsFromApi() {
        return questionsFromApi;
    }

    public void setQuestionsFromApi(List<Question> questionsFromApi) {
        this.questionsFromApi = questionsFromApi;
    }
}