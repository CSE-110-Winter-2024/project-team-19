package edu.ucsd.cse110.successorator.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TaskEntity.class}, version = 1)
public abstract class TasksDatabase extends RoomDatabase {
    public abstract TasksDao tasksDao();
}
