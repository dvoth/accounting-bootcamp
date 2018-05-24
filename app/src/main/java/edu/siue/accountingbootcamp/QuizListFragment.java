package edu.siue.accountingbootcamp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import edu.siue.accountingbootcamp.models.Quiz;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class QuizListFragment extends Fragment {

    public static final String QUIZ_LIST = "quiz-list";

    private List<Quiz> quizList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QuizListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static QuizListFragment newInstance(List<Quiz> quizList) {
        QuizListFragment fragment = new QuizListFragment();
        Bundle args = new Bundle();
        args.putSerializable(QUIZ_LIST, (Serializable) quizList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Receive arguments from where the fragment was called
        if (getArguments() != null) {
            Quiz[] quizzes = (Quiz[]) getArguments().getParcelableArray(QUIZ_LIST);
            if (quizzes != null) {
                quizList = Arrays.asList(quizzes);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the recycler view and set the adapter to it
        View view = inflater.inflate(R.layout.fragment_quiz_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.quiz_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new QuizListAdapter(getActivity(), quizList));

        return recyclerView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction();
    }
}
