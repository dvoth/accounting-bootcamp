package edu.siue.accountingbootcamp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import edu.siue.accountingbootcamp.models.Answer;
import edu.siue.accountingbootcamp.models.Question;
import edu.siue.accountingbootcamp.models.QuestionDAO;
import edu.siue.accountingbootcamp.models.AnswerDAO;
import edu.siue.accountingbootcamp.models.Quiz;
import edu.siue.accountingbootcamp.models.QuizDAO;

@Database(entities = {Quiz.class, Question.class, Answer.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract QuizDAO quizDAO();
    public abstract AnswerDAO answerDAO();
    public abstract QuestionDAO questionDAO();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "quizzes").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
