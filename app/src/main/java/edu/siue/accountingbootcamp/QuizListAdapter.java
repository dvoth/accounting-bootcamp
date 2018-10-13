package edu.siue.accountingbootcamp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import edu.siue.accountingbootcamp.models.Quiz;
import edu.siue.accountingbootcamp.models.QuizDAO;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.ViewHolder> {

    private List<Quiz> quizList;
    private Context mContext;
    QuizDAO mQuizDao;
    AppDatabase db;


    QuizListAdapter(Context context, List<Quiz> items) {
        this.mContext = context;
        this.quizList = items;
        db = AppDatabase.getAppDatabase(mContext);
        mQuizDao = db.quizDAO();
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(QuizListAdapter.ViewHolder holder, int position) {
        final Quiz quiz = quizList.get(position);
        Quiz previousQuiz = quizList.get(quiz.getQuizOrder() - 1);
        @SuppressLint("ResourceType") String lightGreyString = mContext.getString(R.color.inaccessible);

        ColorFilter green = new LightingColorFilter( Color.parseColor("#216C2A"), Color.parseColor("#216C2A"));
        ColorFilter red = new LightingColorFilter( Color.parseColor("#8A0707"), Color.parseColor("#8A0707"));
        ColorFilter grey = new LightingColorFilter( Color.GRAY, Color.GRAY);

        Drawable lightGreyBackground = ContextCompat.getDrawable(mContext, R.drawable.quiz_container_light);
        Drawable percentageCircle = ContextCompat.getDrawable(mContext, R.drawable.circle);

        percentageCircle.setColorFilter(red);

        holder.tvName.setText(quiz.getName());

        if (quiz.getPercentage() == 0) {
            percentageCircle.setColorFilter(grey);
        } else if (quiz.getPercentage() > 70) {
            percentageCircle.setColorFilter(green);
        } else {
            percentageCircle.setColorFilter(red);
        }

        holder.tvPercentage.setBackground(percentageCircle);
        holder.tvPercentage.setText(Integer.toString(quiz.getPercentage()) + "%");

        if (previousQuiz.getPercentage() >= previousQuiz.getPassPercentage() || position == 0 ) {
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
                    ft.replace(R.id.quiz_list_container, fragment);

                    // Add to back stack so we can press the back button to return to the QuizListFragment
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        } else {
            holder.mView.setBackground(lightGreyBackground);
        }
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