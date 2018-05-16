package edu.siue.accountingbootcamp.models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by chrismeats on 5/3/18.
 */

@Dao
public interface AnswerDAO {

    @Query("SELECT * FROM answers WHERE quizId = :quizId AND questionId = :questionId")
    List<Answer> getAll(int quizId, int questionId);
}
