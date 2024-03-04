package edu.ucsd.cse110.successorator.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/*
This class was adapted from the FlashcardsDatabase class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
@Database(entities = {TaskEntity.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class TasksDatabase extends RoomDatabase {
    public abstract TasksDao tasksDao();
}
