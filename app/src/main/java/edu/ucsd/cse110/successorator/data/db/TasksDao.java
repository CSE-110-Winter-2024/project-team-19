package edu.ucsd.cse110.successorator.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

/*
This class was adapted from the FlashcardsDao class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
@Dao
public interface TasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(TaskEntity task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<TaskEntity> tasks);

    @Query("SELECT * FROM tasks WHERE id = :id")
    TaskEntity find(int id);

    @Query("SELECT * FROM tasks ORDER BY sort_order")
    List<TaskEntity> findAll();

    @Query("SELECT * FROM tasks WHERE id = :id")
    LiveData<TaskEntity> findAsLiveData(int id);

    @Query("SELECT * FROM tasks ORDER BY sort_order")
    LiveData<List<TaskEntity>> findAllAsLiveData();

    @Query("SELECT COUNT(*) FROM tasks")
    int count();

    @Query("SELECT MIN(sort_order) FROM tasks")
    int getMinSortOrder();

    @Query("SELECT MAX(sort_order) FROM tasks")
    int getMaxSortOrder();

    @Query("UPDATE tasks SET sort_order = sort_order + :by WHERE sort_order >= :from AND sort_order <= :to")
    void shiftSortOrders(int from, int to, int by);

    @Transaction
    default int append(TaskEntity task){
        var maxSortOrder = getMaxSortOrder();
        var newtask = new TaskEntity(
                task.taskName, maxSortOrder + 1, task.complete
        );
        return Math.toIntExact(insert(newtask));
    }

    @Transaction
    default int prepend(TaskEntity task){
        shiftSortOrders(getMinSortOrder(), getMaxSortOrder(), 1);
        var maxSortOrder = getMaxSortOrder();
        var newtask = new TaskEntity(
                task.taskName, maxSortOrder - 1, task.complete
        );
        return Math.toIntExact(insert(newtask));
    }

    @Query("DELETE FROM tasks where id = :id")
    void delete(int id);
}
