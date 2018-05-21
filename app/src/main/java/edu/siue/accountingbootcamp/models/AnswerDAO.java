package edu.siue.accountingbootcamp.models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by chrismeats on 5/3/18.
 */

@Dao
public interface AnswerDAO {
    @Query("SELECT * FROM answers WHERE quizId = :quizId AND questionId = :questionId")
    List<Answer> getAll(int quizId, int questionId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Answer... answers);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Answer> answers);
}
