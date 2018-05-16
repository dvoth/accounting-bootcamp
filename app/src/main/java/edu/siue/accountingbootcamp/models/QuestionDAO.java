package edu.siue.accountingbootcamp.models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by chrismeats on 5/16/18.
 */

@Dao
public interface QuestionDAO {
    @Query("SELECT * FROM questions WHERE quizId = :quizId")
    List<Question> getAll(int quizId);
}
