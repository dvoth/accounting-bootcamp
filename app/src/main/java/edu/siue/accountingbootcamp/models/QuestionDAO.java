package edu.siue.accountingbootcamp.models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by chrismeats on 5/16/18.
 */

@Dao
public interface QuestionDAO {
    @Query("SELECT * FROM questions WHERE quizId = :quizId")
    List<Question> getAll(int quizId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Question... questions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Question> questions);
}
