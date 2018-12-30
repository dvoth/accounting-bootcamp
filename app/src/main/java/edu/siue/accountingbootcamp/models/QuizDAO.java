package edu.siue.accountingbootcamp.models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface QuizDAO {
    @Query("SELECT * FROM quizzes")
    List<Quiz> getAll();

    @Query("SELECT * FROM quizzes WHERE id = :id")
    Quiz getQuiz(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Quiz... quizzes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Quiz> Quizzes);

    @Query("UPDATE quizzes SET name = :name, quizOrder = :quizOrder WHERE id = :id")
    void update(int id, String name, int quizOrder);

    @Query("UPDATE quizzes SET locked = :locked WHERE id = :id")
    void updateLocked(int id, boolean locked);

}
