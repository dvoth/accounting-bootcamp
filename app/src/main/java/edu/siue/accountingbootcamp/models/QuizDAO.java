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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Quiz... quizzes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Quiz> Quizzes);
}