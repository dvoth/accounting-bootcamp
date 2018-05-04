package edu.siue.accountingbootcamp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import edu.siue.accountingbootcamp.models.Answer;
import edu.siue.accountingbootcamp.models.Question;
import edu.siue.accountingbootcamp.models.Quiz;

public class QuizActivity extends AppCompatActivity {

    public Quiz quiz;
    public Question question;
    public int questionNumber = 0;
    TableLayout creditTable;
    TableLayout debitTable;
    TextView questionText;
    Button nextButton;
    Button previousButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        creditTable = findViewById(R.id.credit_table);
        debitTable = findViewById(R.id.debit_table);
        nextButton = findViewById(R.id.next_button);
        previousButton = findViewById(R.id.previous_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionNumber < quiz.getQuestions().size() - 1) {
                    questionNumber++;
                    clearTables();
                    displayQuestion();
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionNumber > 0) {
                    questionNumber--;
                    clearTables();
                    displayQuestion();
                }
            }
        });

        // Get the intent (containing quiz data) from QuizListAdapter
        Intent intent = getIntent();
        quiz = intent.getParcelableExtra(QuizListAdapter.ITEM_KEY);

        // Add toolbar and update default text to quiz name
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(quiz.getName());
        setSupportActionBar(toolbar);

        displayQuestion();
    }

    private void displayQuestion() {
        question = quiz.getQuestions().get(questionNumber);
        questionText = findViewById(R.id.question_text);
        questionText.setText(question.getText());

        for (final Answer answer : question.getAnswers()) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            Button b = new Button(this);
            b.setText(answer.getText());
            b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(QuizActivity.this, answer.getIsanswer().toString(), Toast.LENGTH_SHORT).show();
                }
            });
            tr.addView(b);
            String column = answer.getColumn();
            if (column.equals("dr")) {
                debitTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            } else {
                creditTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    private void clearTables() {
        while (creditTable.getChildCount() > 1)
            creditTable.removeView(creditTable.getChildAt(creditTable.getChildCount() - 1));
        while (debitTable.getChildCount() > 1)
            debitTable.removeView(debitTable.getChildAt(debitTable.getChildCount() - 1));
    }
}
