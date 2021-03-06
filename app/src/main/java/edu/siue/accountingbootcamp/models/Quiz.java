package edu.siue.accountingbootcamp.models;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

import edu.siue.accountingbootcamp.AppDatabase;

@Entity(tableName = "quizzes")
public class Quiz extends ViewModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int quizOrder;
    private boolean locked;

    // Serialized name to put results of questions from the API to this List field so we can later send to MutableLiveData object
    @Ignore
    @SerializedName("questions")
    private List<Question> questionsFromApi;

    @Ignore
    // 'transient' means that this field will not be serialized
    // Done because we can't put questions from the api directly into a MutableLiveData object
    private transient MutableLiveData<List<Question>> questions = new MutableLiveData<>();

    @Ignore
    private static final int PASS_PERCENTAGE = 70;

    @Ignore
    private int lastQuestionIndex;

    /*
        GETTERS AND SETTERS
     */
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

    public void setQuestions(List<Question> questions) {
        this.questions.postValue(questions);
        lastQuestionIndex = questions.size() - 1;
    }

    public List<Question> getQuestionsFromApi() {
        return questionsFromApi;
    }

    public void setQuestionsFromApi(List<Question> questionsFromApi) {
        this.questionsFromApi = questionsFromApi;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getLastQuestionIndex() {
        return lastQuestionIndex;
    }

    public void setLastQuestionIndex(int lastQuestionIndex) {
        this.lastQuestionIndex = lastQuestionIndex;
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
        dest.writeString(this.name);
        dest.writeInt(this.quizOrder);
        dest.writeList(this.questions.getValue());
        dest.writeInt((this.locked ? 1 : 0));
    }

    public Quiz() {
        locked = true;
    }

    public Quiz(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.quizOrder = in.readInt();
        in.readValue(Question.class.getClassLoader());
        this.locked = (in.readInt() == 0) ? false : true;
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

    /*
        UTILITY METHODS
     */
    public int getPercentage() {
        int correct = getCorrectAnswersCount();
        int size = questions.getValue().size();
        double answer = ((double) correct/size) * 100;
        return (int) Math.round(answer);
    }

    public int getCorrectAnswersCount() {
        int numCorrect = 0;
        for (Question question : questions.getValue()) {
            if (question.isAnsweredCorrectly()) {
                numCorrect++;
            }
        }

        return numCorrect;
    }

    public int getPassPercentage() {
        return PASS_PERCENTAGE;
    }

    public int getCurrentQuestion() {
        // Initialize to the last question to return if the entire quiz is completed
        int questionNumber = getQuestions().size() - 1;

        // Return the first question that isn't attempted
        for (int i=0; i<getQuestions().size() - 1; i++) {
            if (!getQuestions().get(i).isAnswerAttempted()) {
                return i;
            }
        }

        // If all questions were attempted, return the last question
        return questionNumber;
    }

    public boolean started() {
        for (Question question : getQuestions()) {
            if (question.getQuestionOrder() == -1) {
                return false;
            }
        }

        return true;
    }

    public void reset(Activity activity) {

        AppDatabase db;
        QuestionDAO mQuestionDao;
        QuizDAO mQuizDao;
        db = AppDatabase.getAppDatabase(activity);
        mQuizDao = db.quizDAO();
        mQuizDao.updateLocked(getId(), true);


        for (Question question : getQuestions()) {
            mQuestionDao = db.questionDAO();

            mQuestionDao.updateAnswerAttempted(question.getId(), false);
            mQuestionDao.updateAnsweredCorrectly(question.getId(), false);
            mQuestionDao.updateQuestionOrder(question.getId(), -1);
            question.setAnswerAttempted(false);
            question.setAnsweredCorrectly(false);
            question.setQuestionOrder(-1);
        }
    }
}