package edu.siue.accountingbootcamp;

import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.siue.accountingbootcamp.models.Answer;
import edu.siue.accountingbootcamp.models.AnswerDAO;
import edu.siue.accountingbootcamp.models.Question;
import edu.siue.accountingbootcamp.models.QuestionDAO;
import edu.siue.accountingbootcamp.models.Quiz;
import edu.siue.accountingbootcamp.models.QuizDAO;
import edu.siue.accountingbootcamp.services.ApiService;
import edu.siue.accountingbootcamp.utils.NetworkHelper;

public class MainActivity extends AppCompatActivity
        implements QuizListFragment.OnListFragmentInteractionListener,
                   ResultsFragment.OnFragmentInteractionListener,
                   QuizFragment.OnFragmentInteractionListener{

    private static final String JSON_URL =
            "https://abootcamp.isg.siue.edu/api/all.php";

    List<Quiz> quizList;
    HashMap<Integer, Quiz> quizHashMap = new HashMap<>();
    AppDatabase db;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Receives the parcelable quizzes from ApiService.java
            Quiz[] quizzes = (Quiz[]) intent.getParcelableArrayExtra(ApiService.MY_SERVICE_PAYLOAD);
            quizList = new ArrayList<>(Arrays.asList(quizzes));
            for (Quiz quiz : quizList) {
                quizHashMap.put(quiz.getId(), quiz);
            }

            loadOnDevice();
            displayQuizList();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getAppDatabase(this);
        boolean networkOk = NetworkHelper.hasNetworkAccess(this);

        if (networkOk) {
            Intent intent = new Intent(this, ApiService.class);
            intent.setData(Uri.parse(JSON_URL));
            startService(intent);
        } else {
            Toast.makeText(this, "Could not connect to the Internet", Toast.LENGTH_LONG).show();
            quizList = loadFromDevice();

            if (quizList.isEmpty()) {
                displayQuizLoadError();
            } else {
                displayQuizList();
            }
        }

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(ApiService.MY_SERVICE_MESSAGE));
    }

    private void displayQuizLoadError() {
        Button tryAgain = findViewById(R.id.try_loading_again);

        findViewById(R.id.no_quizzes).setVisibility(View.VISIBLE);
        tryAgain.setVisibility(View.VISIBLE);

        // Refreshes activity 
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });

    }

    /**
     * Loops through the quizzes from the api and stores the quizzes, questions, and answers to the device's database
     */
    private void loadOnDevice() {
        // Get database objects
        QuizDAO mQuizDao = db.quizDAO();
        QuestionDAO mQuestionDao = db.questionDAO();
        AnswerDAO mAnswerDao = db.answerDAO();

        List<Quiz> currentQuizzes = mQuizDao.getAll();

        // Keeps track of the list of questions for each quiz
        List<Question> quizQuestions;
        // Keeps track of the list of answers for each question
        List<Answer> questionAnswers;

        // Master list of all quizzes, questions, and answers from api
        List<Quiz> quizzesToUpdate = new ArrayList<>();
        List<Question> allQuestions = new ArrayList<>();
        List<Answer> allAnswers = new ArrayList<>();

        for (Quiz quizFromAPI : quizList) {
            Quiz quizFromDB = quizHashMap.get(quizFromAPI.getId());

            // If the quiz was updated on the server, update the database data to store later
            if (quizFromDB != null && !quizFromDB.equals(quizFromAPI)) {
                quizFromDB.setName(quizFromAPI.getName());
                quizFromDB.setQuizOrder(quizFromAPI.getQuizOrder());
                quizzesToUpdate.add(quizFromAPI);
            } else {
                quizzesToUpdate.add(quizFromAPI);
            }

            quizQuestions = quizFromAPI.getQuestions();

            for (Question question : quizQuestions) {
                // Manually add correct foreign key ids (Room doesn't do this automatically)
                question.setQuizId(quizFromAPI.getId());
                questionAnswers = question.getAnswers();

                // Add question with updated foreign keys to master list
                allQuestions.add(question);

                for (Answer answer : questionAnswers) {
                    // Manually add correct foreign key ids (Room doesn't do this automatically)
                    answer.setQuizId(quizFromAPI.getId());
                    answer.setQuestionId(question.getId());

                    // Add answer with updated foreign keys to master list
                    allAnswers.add(answer);
                }
            }
        }

        // Insert all quizzes, questions, and answers in one fell swoop outside of the loops (reduces query load)
        mQuizDao.insertAll(quizzesToUpdate);
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

    private void displayQuizList() {
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

    public void displayQuiz(int quizId) {
        Quiz quiz = quizList.get(quizId);

        // get fragment manager
        FragmentManager fm = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putParcelable(QuizFragment.QUIZ_KEY, quiz);

        // Add data to the new fragment
        QuizFragment fragment = new QuizFragment();
        fragment.setArguments(bundle);

        // Add the new fragment on top of the previous
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.quiz_list_container, fragment);

        // Add to back stack so we can press the back button to return to the QuizListFragment
        ft.commit();
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
