package edu.siue.accountingbootcamp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.siue.accountingbootcamp.models.Quiz;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.ViewHolder> {

    private List<Quiz> quizList;
    private Context mContext;

    QuizListAdapter(Context context, List<Quiz> items) {
        this.mContext = context;
        this.quizList = items;
    }

    @Override
    public QuizListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.OnSharedPreferenceChangeListener prefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                  String key) {
                Log.i("preferences", "onSharedPreferenceChanged: " + key);
            }
        };
        settings.registerOnSharedPreferenceChangeListener(prefsListener);

        int layoutId = R.layout.quiz_list_view;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(layoutId, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuizListAdapter.ViewHolder holder, int position) {
        final Quiz quiz = quizList.get(position);

        holder.tvName.setText(quiz.getName());
        holder.tvPercentage.setText(Integer.toString(quiz.getPercentage()) + "%");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get fragment manager
                FragmentManager fm = ((Activity) mContext).getFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putParcelable(QuizFragment.QUIZ_KEY, quiz);

                // Add data to the new fragment
                QuizFragment fragment = new QuizFragment();
                fragment.setArguments(bundle);

                // Add the new fragment on top of the previous
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.quiz_list_container, fragment);

                // Add to back stack so we can press the back button to return to the QuizListFragment
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    public int getItemCount() {
        return quizList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvPercentage;
        View mView;
        ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.itemNameText);
            tvPercentage = itemView.findViewById(R.id.percentageText);
            mView = itemView;
        }
    }
}