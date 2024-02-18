package edu.ucsd.cse110.successorator;

import android.app.Application;

import androidx.room.Room;

import edu.ucsd.cse110.successorator.data.db.RoomTasksRepository;
import edu.ucsd.cse110.successorator.data.db.TasksDatabase;
import edu.ucsd.cse110.successorator.lib.domain.TaskRepository;

public class SuccessoratorApplication extends Application {
    private TaskRepository taskRepository;

    @Override
    public void onCreate(){
        super.onCreate();

        var database = Room.databaseBuilder(
                        getApplicationContext(),
                        TasksDatabase.class,
                        "tasks-database"
                )
                .allowMainThreadQueries()
                .build();

        this.taskRepository = new RoomTasksRepository(database.tasksDao());

        // Idk if this is needed?
        var sharedPreferences = getSharedPreferences("successorator", MODE_PRIVATE);
        var isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        if(isFirstRun && database.tasksDao().count() == 0){
            sharedPreferences.edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    public TaskRepository getTaskRepository(){
        return taskRepository;
    }
}
