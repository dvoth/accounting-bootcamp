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
import android.view.View;
import android.widget.Button;
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

    List<Quiz> quizzesFromAPI;
    HashMap<Integer, Quiz> quizHashMap = new HashMap<>();
    HashMap<Integer, Question> questionHashMap = new HashMap<>();
    HashMap<Integer, Answer> answerHashMap = new HashMap<>();
    AppDatabase db;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Receives the parcelable quizzes from ApiService.java
            Quiz[] quizzes = (Quiz[]) intent.getParcelableArrayExtra(ApiService.MY_SERVICE_PAYLOAD);
            quizzesFromAPI = new ArrayList<>(Arrays.asList(quizzes));

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
        loadHashMaps();

        if (networkOk) {
            Intent intent = new Intent(this, ApiService.class);
            intent.setData(Uri.parse(JSON_URL));
            startService(intent);
        } else {
            Toast.makeText(this, "Could not connect to the Internet", Toast.LENGTH_LONG).show();

            if (quizHashMap.isEmpty()) {
                displayQuizLoadError();
            } else {
                displayQuizList();
            }
        }

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(ApiService.MY_SERVICE_MESSAGE));
    }

    private void loadHashMaps() {
        QuizDAO quizDAO = db.quizDAO();
        QuestionDAO questionDAO = db.questionDAO();
        AnswerDAO answerDAO = db.answerDAO();
        List<Quiz> quizzes = quizDAO.getAll();
        List<Question> questions;
        List<Answer> answers;

        for (Quiz quiz : quizzes ){
            questions = questionDAO.getAll(quiz.getId());

            for (Question question : questions) {
                answers = answerDAO.getAll(quiz.getId(), question.getId());

                for (Answer answer : answers) {
                    answerHashMap.put(answer.getId(), answer);
                }

                question.setAnswers(answers);
                questionHashMap.put(question.getId(), question);
            }
            quiz.setQuestions(questions);
            quizHashMap.put(quiz.getId(), quiz);
        }

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

        for (Quiz quizFromAPI : quizzesFromAPI) {
            Quiz quizFromDB = quizHashMap.get(quizFromAPI.getId());
            Quiz updatedQuiz = updateQuizHashMap(quizFromDB, quizFromAPI);
            quizQuestions = updatedQuiz.getQuestions();
            allQuizzes.add(updatedQuiz);

            for (Question questionFromAPI : quizQuestions) {
                Question questionFromDB = questionHashMap.get(questionFromAPI.getId());
                Question updatedQuestion = updateQuestionHashMap(questionFromDB, questionFromAPI);

                // Manually add correct foreign key ids (Room doesn't do this automatically)
                updatedQuestion.setQuizId(updatedQuiz.getId());
                questionAnswers = updatedQuestion.getAnswers();

                // Add question with updated foreign keys to master list
                allQuestions.add(updatedQuestion);

                for (Answer answerFromAPI : questionAnswers) {
                    Answer answerFromDB = answerHashMap.get(answerFromAPI.getId());
                    Answer updatedAnswer = updateAnswerHashMap(answerFromDB, answerFromAPI);

                    // Manually add correct foreign key ids (Room doesn't do this automatically)
                    answerFromAPI.setQuizId(updatedQuiz.getId());
                    answerFromAPI.setQuestionId(updatedQuestion.getId());

                    // Add answer with updated foreign keys to master list
                    allAnswers.add(updatedAnswer);
                }
            }
        }

        // Insert all quizzes, questions, and answers in one fell swoop outside of the loops (reduces query load)
        mQuizDao.insertAll(allQuizzes);
        mQuestionDao.insertAll(allQuestions);
        mAnswerDao.insertAll(allAnswers);
    }

    private Quiz updateQuizHashMap(Quiz quizFromDB, Quiz quizFromAPI) {
        // If the quiz was updated on the server, update the database data to store later
        if (quizFromDB != null) {
            quizFromDB.setName(quizFromAPI.getName());
            quizFromDB.setQuizOrder(quizFromAPI.getQuizOrder());
            quizHashMap.put(quizFromAPI.getId(), quizFromDB);
        } else {
            quizFromDB = quizFromAPI;
            quizHashMap.put(quizFromAPI.getId(), quizFromAPI);
        }

        return quizFromDB;
    }

    private Question updateQuestionHashMap(Question questionFromDB, Question questionFromAPI) {
        // If the quiz was updated on the server, update the database data to store later
        if (questionFromDB != null) {
            questionFromDB.setText(questionFromAPI.getText());
            questionHashMap.put(questionFromAPI.getId(), questionFromDB);
        } else {
            questionFromDB = questionFromAPI;
            questionHashMap.put(questionFromAPI.getId(), questionFromAPI);
        }

        return questionFromDB;
    }

    private Answer updateAnswerHashMap(Answer answerFromDB, Answer answerFromAPI) {
        // If the quiz was updated on the server, update the database data to store later
        if (answerFromDB != null) {
            answerFromDB.setText(answerFromAPI.getText());
            answerFromDB.setIsanswer(answerFromAPI.getIsanswer());
            answerFromDB.setColumn(answerFromAPI.getColumn());
            answerHashMap.put(answerFromAPI.getId(), answerFromDB);
        } else {
            answerFromDB = answerFromAPI;
            answerHashMap.put(answerFromAPI.getId(), answerFromAPI);
        }

        return answerFromDB;
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

    private void displayQuizList() {
        if (quizHashMap != null) {
            FragmentManager fm = getFragmentManager();
            Bundle bundle = new Bundle();

            // Convert quiz to array to make it easier to pass to the QuizListFragment
            Quiz[] quizArray = quizHashMap.values().toArray(new Quiz[quizHashMap.values().size()]);
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

    public void displayQuiz(int quizOrder) {
        Quiz nextQuiz = null;
        for (Quiz quiz : quizHashMap.values()) {
            if (quiz.getQuizOrder() == quizOrder)
                nextQuiz = quiz;
        }

        if (nextQuiz == null)
            nextQuiz = new Quiz();

        // get fragment manager
        FragmentManager fm = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putParcelable(QuizFragment.QUIZ_KEY, nextQuiz);

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
