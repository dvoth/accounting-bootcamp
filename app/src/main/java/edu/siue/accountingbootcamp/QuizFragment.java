package edu.siue.accountingbootcamp;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import edu.siue.accountingbootcamp.models.Answer;
import edu.siue.accountingbootcamp.models.Question;
import edu.siue.accountingbootcamp.models.QuestionDAO;
import edu.siue.accountingbootcamp.models.Quiz;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuizFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String QUIZ_KEY = "quiz-key";

    public Quiz quiz;
    public Question question;
    public int questionNumber = 0;
    QuestionDAO mQuestionDao;
    TableLayout creditTable;
    TableLayout debitTable;
    TextView questionText;
    Button nextButton;
    Button previousButton;
    AppDatabase db;

    private OnFragmentInteractionListener mListener;

    public QuizFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param newQuiz Parameter 1.
     * @return A new instance of fragment QuizFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizFragment newInstance(Quiz newQuiz) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putParcelable(QUIZ_KEY, newQuiz);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getAppDatabase(getActivity());
        mQuestionDao = db.questionDAO();

        if (getArguments() != null) {
            quiz = getArguments().getParcelable(QUIZ_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        creditTable = view.findViewById(R.id.credit_table);
        debitTable = view.findViewById(R.id.debit_table);
        nextButton = view.findViewById(R.id.next_button);
        previousButton = view.findViewById(R.id.previous_button);
        questionText = view.findViewById(R.id.question_text);
        questionNumber = quiz.getCurrentQuestion();

        // Listeners for next and previous questions
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionNumber < quiz.getQuestions().size() - 1 && question.isAnswerAttempted()) {
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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        displayQuestion();
    }

    private void displayQuestion() {
        question = quiz.getQuestions().get(questionNumber);
        questionText.setText(question.getText());

        // Create a table row for each available answer and add it to the necessary column
        for (final Answer answer : question.getAnswers()) {
            TableRow tr = new TableRow(getActivity());
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            final Button b = new Button(getActivity());
            b.setText(answer.getText());
            b.setBackgroundResource(R.drawable.btn_default_normal);
            b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), answer.getIsanswer().toString(), Toast.LENGTH_SHORT).show();

                    question.setAnswerAttempted(true);

                    if (answer.getIsanswer()) {
                        question.setAnsweredCorrectly(true);
                        mQuestionDao.updateAnsweredCorrectly(question.getId(), answer.getIsanswer());
                        b.setBackgroundColor(Color.rgb(112, 43, 45));
                    } else {
                        b.setBackgroundColor(Color.RED);
                    }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
