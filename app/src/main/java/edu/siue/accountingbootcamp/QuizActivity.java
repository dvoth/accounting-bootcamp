package edu.siue.accountingbootcamp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import edu.siue.accountingbootcamp.models.Question;
import edu.siue.accountingbootcamp.models.Quiz;

public class QuizActivity extends AppCompatActivity {

    Quiz quiz;
    List<Question> question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get the intent (containing quiz data) from QuizListAdapter
        Intent intent = getIntent();
        quiz = intent.getParcelableExtra(QuizListAdapter.ITEM_KEY);

        // Add toolbar and update default text to quiz name
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(quiz.getName());
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        beginQuiz();
    }

    private void beginQuiz() {

    }

    private void displayQuestion() {

    }
}
