package edu.siue.accountingbootcamp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import edu.siue.accountingbootcamp.models.Quiz;
import edu.siue.accountingbootcamp.models.QuizDAO;
import edu.siue.accountingbootcamp.services.MyService;
import edu.siue.accountingbootcamp.utils.NetworkHelper;

public class MainActivity extends AppCompatActivity {

    private static final String JSON_URL =
            "https://abootcamp.isg.siue.edu/api/all.php";

    private boolean networkOk;
    TextView output;
    List<Quiz> quizList;
    RecyclerView mRecyclerView;
    QuizListAdapter quizListAdapter;
    AppDatabase db;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Receives the parcelable quizzes from MyService.java
            Quiz[] quizzes = (Quiz[]) intent.getParcelableArrayExtra(MyService.MY_SERVICE_PAYLOAD);
            quizList = Arrays.asList(quizzes);

            loadOnDevice();
            displayDataItems(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = db.getAppDatabase(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvItems);
        networkOk = NetworkHelper.hasNetworkAccess(this);

        if (networkOk) {
            Toast.makeText(this, "Network working", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MyService.class);
            intent.setData(Uri.parse(JSON_URL));
            startService(intent);
        } else {
            Toast.makeText(this, "Network issue", Toast.LENGTH_LONG).show();
            quizList = loadFromDevice();
            displayDataItems(null);
        }


        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(MyService.MY_SERVICE_MESSAGE));

//        networkOk = NetworkHelper.hasNetworkAccess(this);
//        output.append("Network ok: " + networkOk);

    }

    private void loadOnDevice() {
        QuizDAO mQuizDao = db.quizDAO();// Get DAO object

        for (Quiz quiz : quizList) {
            mQuizDao.insert(quiz);
        }
    }

    private List <Quiz> loadFromDevice() {
        QuizDAO mQuizDao = db.quizDAO();
//        AnswerDAO mQuestionDao = db.questionDAO();
//        QuestionOptionDAO mQuestionOptionDao = db.questionOptionDAO();
//        List<Quiz> quizzes = mQuizDao.getAll();
//
//        for (Quiz quiz : quizzes) {
//            // Gets questions associated with quiz
//            List<Question> questions = new ArrayList<>();
////            questions = mQuestionDao.getAll(quiz.getId());
//
//            for (Question question: questions) {
//                // Gets questions associated with quiz
//                List<Answer> questionOptions = new ArrayList<>();
////                questionOptions = mQuestionOptionDao.getAll(quiz.getId(), question.getId());
//
//                question.setAnswers(questionOptions);
//            }
//
//            quiz.setQuestions(questions);
//        }
        
        return mQuizDao.getAll();
    }

    private void displayDataItems(String category) {
//        quizList = mDataSource.getAllItems(category);
        if (quizList != null) {
            quizListAdapter = new QuizListAdapter(this, quizList);
            mRecyclerView.setAdapter(quizListAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mBroadcastReceiver);
    }

    public void runClickHandler(View view) {

        if (networkOk) {
            Intent intent = new Intent(this, MyService.class);
            intent.setData(Uri.parse(JSON_URL));
            this.startService(intent);
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }

    }

    public void clearClickHandler(View view) {
        output.setText("");
    }

}
