package edu.siue.accountingbootcamp.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import edu.siue.accountingbootcamp.models.Quiz;
import edu.siue.accountingbootcamp.utils.HttpHelper;

public class ApiService extends IntentService {

    public static final String TAG = "ApiService";
    public static final String MY_SERVICE_MESSAGE = "myServiceMessage";
    public static final String MY_SERVICE_PAYLOAD = "myServicePayload";

    public ApiService() {
        super("ApiService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Uri uri = intent.getData();

        String response;
        try {
            response = HttpHelper.downloadUrl(uri.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Gson gson = new Gson();
        Quiz[] quizzes = gson.fromJson(response, Quiz[].class);

        // Convert questions List received from api to be compatible with LiveData object
        for (Quiz quiz : quizzes) {
            quiz.setQuestions(quiz.getQuestionsFromApi());
        }

        Intent messageIntent = new Intent(MY_SERVICE_MESSAGE);
        messageIntent.putExtra(MY_SERVICE_PAYLOAD, quizzes);
        LocalBroadcastManager manager =
                LocalBroadcastManager.getInstance(getApplicationContext());
        manager.sendBroadcast(messageIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

}
