package edu.siue.accountingbootcamp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.siue.accountingbootcamp.models.Answer;
import edu.siue.accountingbootcamp.models.AnswerDAO;
import edu.siue.accountingbootcamp.models.Question;
import edu.siue.accountingbootcamp.models.QuestionDAO;
import edu.siue.accountingbootcamp.models.Quiz;
import edu.siue.accountingbootcamp.models.QuizDAO;
import edu.siue.accountingbootcamp.services.MyService;
import edu.siue.accountingbootcamp.utils.NetworkHelper;

public class MainActivity extends AppCompatActivity
        implements QuizListFragment.OnListFragmentInteractionListener,
                   QuizFragment.OnFragmentInteractionListener{

    private static final String JSON_URL =
            "https://abootcamp.isg.siue.edu/api/all.php";

    List<Quiz> quizList;
    AppDatabase db;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Receives the parcelable quizzes from MyService.java
            Quiz[] quizzes = (Quiz[]) intent.getParcelableArrayExtra(MyService.MY_SERVICE_PAYLOAD);
            quizList = new ArrayList<>(Arrays.asList(quizzes));

            loadOnDevice();
            displayDataItems();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getAppDatabase(this);
        boolean networkOk = NetworkHelper.hasNetworkAccess(this);

        if (networkOk) {
            Toast.makeText(this, "Network working", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MyService.class);
            intent.setData(Uri.parse(JSON_URL));
            startService(intent);
        } else {
            Toast.makeText(this, "Network issue", Toast.LENGTH_LONG).show();
            quizList = loadFromDevice();
            displayDataItems();
        }


        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(MyService.MY_SERVICE_MESSAGE));
    }

    /**
     * Loops through the quizzes from the api and stores the quizzes, questions, and answers to the device's database
     */
    private void loadOnDevice() {
        // Get database objects
        QuizDAO mQuizDao = db.quizDAO();
        QuestionDAO mQuestionDao = db.questionDAO();
        AnswerDAO mAnswerDao = db.answerDAO();

        // Keeps track of the list of questions for each quiz
        List<Question> quizQuestions;
        // Keeps track of the list of answers for each question
        List<Answer> questionAnswers;

        // Master list of all quizzes, questions, and answers from api
        List<Quiz> allQuizzes = new ArrayList<>();
        List<Question> allQuestions = new ArrayList<>();
        List<Answer> allAnswers = new ArrayList<>();

        for (Quiz quiz : quizList) {
            allQuizzes.add(quiz);
            quizQuestions = quiz.getQuestions();

            for (Question question : quizQuestions) {
                // Manually add correct foreign key ids (Room doesn't do this automatically)
                question.setQuizId(quiz.getId());
                questionAnswers = question.getAnswers();

                // Add question with updated foreign keys to master list
                allQuestions.add(question);

                for (Answer answer : questionAnswers) {
                    // Manually add correct foreign key ids (Room doesn't do this automatically)
                    answer.setQuizId(quiz.getId());
                    answer.setQuestionId(question.getId());

                    // Add answer with updated foreign keys to master list
                    allAnswers.add(answer);
                }
            }
        }

        // Insert all quizzes, questions, and answers in one fell swoop outside of the loops (reduces query load)
        mQuizDao.insertAll(allQuizzes);
        mQuestionDao.insertAll(allQuestions);
        mAnswerDao.insertAll(allAnswers);
    }

    /**
     * If there is a network problem and we can't contact the api, load the quizzes from the device's database
     */
    private List <Quiz> loadFromDevice() {
        QuizDAO mQuizDao = db.quizDAO();
        QuestionDAO mQuestionDao = db.questionDAO();
        AnswerDAO mAnswerDao = db.answerDAO();
        List<Quiz> quizzes = mQuizDao.getAll();

        for (Quiz quiz : quizzes) {
            // Gets questions associated with quiz
            List<Question> questions;
            questions = mQuestionDao.getAll(quiz.getId());

            for (Question question: questions) {
                // Gets questions associated with quiz
                List<Answer> answers;
                answers = mAnswerDao.getAll(quiz.getId(), question.getId());

                question.setAnswers(answers);
            }

            quiz.setQuestions(questions);
        }
        
        return quizzes;
    }

    private void displayDataItems() {
        if (quizList != null) {
            FragmentManager fm = getFragmentManager();
            Bundle bundle = new Bundle();

            // Convert quiz to array to make it easier to pass to the QuizListFragment
            Quiz[] quizArray = quizList.toArray(new Quiz[quizList.size()]);
            bundle.putParcelableArray(QuizListFragment.QUIZ_LIST, quizArray);

            // Add data to the new fragment
            QuizListFragment fragment = new QuizListFragment();
            fragment.setArguments(bundle);

            // Add the fragment
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.quiz_list_container, fragment);
            ft.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onListFragmentInteraction() {
        // Left empty for QuizListFragment interface method implementation
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // Left empty for QuizFragment interface method implementation
    }
}
