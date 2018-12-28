package edu.siue.accountingbootcamp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;

import edu.siue.accountingbootcamp.models.Quiz;

public class ResultsFragment extends Fragment {
    public static final String RESULTS_KEY = "results";
    private Quiz quiz;
    private Context mContext;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment newInstance(Quiz newQuiz) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putParcelable(RESULTS_KEY, newQuiz);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quiz = getArguments().getParcelable(RESULTS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Ensures fragments don't display over one another
        if (container != null) {
            container.removeAllViews();
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        int correctAnswers = quiz.getCorrectAnswersCount();
        int totalAnswers = quiz.getQuestions().size();
        TextView quizName = getView().findViewById(R.id.quiz_name);
        TextView quizScore = getView().findViewById(R.id.quiz_score);

        quizName.setText(quiz.getName());
        quizScore.setText(correctAnswers + "/" + totalAnswers + " (" + quiz.getPercentage() + "%)");

        Button tryAgain = getView().findViewById(R.id.try_again);

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quiz.reset();

                // get fragment manager
                FragmentManager fm = ((Activity) mContext).getFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putParcelable(QuizFragment.QUIZ_KEY, quiz);

                // Add data to the new fragment
                QuizFragment fragment = new QuizFragment();
                fragment.setArguments(bundle);

                // Add the new fragment on top of the previous
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.quiz_list_container, fragment);

                ft.commit();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        void onFragmentInteraction(Uri uri);
    }
}
