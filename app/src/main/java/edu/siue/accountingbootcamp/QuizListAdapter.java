package edu.siue.accountingbootcamp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    public static final String ITEM_ID_KEY = "item_id_key";
    public static final String ITEM_KEY = "item_key";
    private List<Quiz> quizList;
    private Context mContext;
    private SharedPreferences.OnSharedPreferenceChangeListener prefsListener;

    public QuizListAdapter(Context context, List<Quiz> items) {
        this.mContext = context;
        this.quizList = items;
    }

    @Override
    public QuizListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        prefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                  String key) {
                Log.i("preferences", "onSharedPreferenceChanged: " + key);
            }
        };
        settings.registerOnSharedPreferenceChangeListener(prefsListener);

        int layoutId = R.layout.list_view;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(QuizListAdapter.ViewHolder holder, int position) {
        final Quiz quiz = quizList.get(position);

        holder.tvName.setText(quiz.getName());
        holder.tvPercentage.setText(Integer.toString(quiz.getPercentage()) + "%");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemId = quiz.getId();
                Intent intent = new Intent(mContext, QuizActivity.class);
                intent.putExtra(ITEM_KEY, quiz);
                mContext.startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return quizList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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