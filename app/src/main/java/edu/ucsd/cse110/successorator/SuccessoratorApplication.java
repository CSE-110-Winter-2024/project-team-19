package edu.ucsd.cse110.successorator;

import android.app.Application;

import androidx.room.Room;

import java.time.LocalDate;

import edu.ucsd.cse110.successorator.data.db.RoomTasksRepository;
import edu.ucsd.cse110.successorator.data.db.TasksDatabase;
import edu.ucsd.cse110.successorator.lib.domain.MockLocalDate;
import edu.ucsd.cse110.successorator.lib.domain.TaskRepository;

public class SuccessoratorApplication extends Application {
    private TaskRepository taskRepository;
    private MockLocalDate mockDate;

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
//        this.taskRepository = new SimpleTaskRepository(new InMemoryDataSource());

        // Idk if this is needed?
        var sharedPreferences = getSharedPreferences("successorator", MODE_PRIVATE);
        var isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        if(isFirstRun && database.tasksDao().count() == 0){
            sharedPreferences.edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }

        //MockLocalDate.setDate(LocalDate.now());
        mockDate = new MockLocalDate(LocalDate.now());
    }

    public TaskRepository getTaskRepository(){
        return taskRepository;
    }
    public MockLocalDate getMockLocalDate(){
        return mockDate;
    }
}
