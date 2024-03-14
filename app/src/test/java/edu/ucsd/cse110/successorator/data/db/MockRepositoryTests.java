package edu.ucsd.cse110.successorator.data.db;

import org.junit.Test;
import org.junit.runner.manipulation.Ordering;

import static org.junit.Assert.*;

import android.content.Context;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.MockLocalDate;
import edu.ucsd.cse110.successorator.lib.domain.SimpleTaskRepository;
import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.lib.domain.Tasks;
import edu.ucsd.cse110.successorator.ui.TaskListAdapter;

public class MockRepositoryTests {

    @Test
    public void US11BDDScenario() {
        // GIVEN we create a weekly Task Today from the today view
        var dataSource = new InMemoryDataSource();
        var repo = new SimpleTaskRepository(dataSource);
        MockLocalDate mockLocalDate = new MockLocalDate(LocalDate.now());
        var model = new MainViewModel(repo,mockLocalDate);

        Task recurWeekly = new Task(20,"Get 2 Groceries",1, true,
                mockLocalDate.now(), Frequency.WEEKLY, mockLocalDate.now().getDayOfWeek(), 1);

        List<Task> myTasks = new ArrayList<>();

        myTasks.add(recurWeekly);

        myTasks =  Tasks.updateTasks(myTasks);

        model.insertNewTask(myTasks.get(0));

        assertEquals(mockLocalDate.now().plusDays(7),myTasks.get(0).activeDate());

        //WHEN I move forward a week
        for (int i = 0; i < 7; i++){
        mockLocalDate.advanceDate();}




        //THEN The task should be set to display now
        LocalDate actual = model.getOrderedTasks().getValue().get(0).activeDate();
        assertEquals(mockLocalDate.now(),actual);

    }
    @Test
    public void US12BDDScenario(){
        //GIVEN I have a daily task that I can see on the today view
        var dataSource = new InMemoryDataSource();
        var repo = new SimpleTaskRepository(dataSource);
        MockLocalDate mockLocalDate = new MockLocalDate(LocalDate.now());
        var model = new MainViewModel(repo,mockLocalDate);


        Task recurDaily = new Task(20,"Get 2 Groceries",1, true,
                mockLocalDate.now(), Frequency.DAILY, mockLocalDate.now().getDayOfWeek(), 1);



        List<Task> myTasks = new ArrayList<>();
        myTasks.add(recurDaily);

        List<Task> tomorrowTasks = new ArrayList<>();

        tomorrowTasks.addAll(new ArrayList<>(myTasks).stream()
                    .filter(task -> task.activeDate().isAfter(mockLocalDate.now())
                            && task.activeDate().isBefore(mockLocalDate.now().plusDays(2))
                            || task.frequency().equals(Frequency.DAILY))
                    .collect(Collectors.toList()));

        //THEN it should also appear in the Tomorrow View
        assertTrue(tomorrowTasks.size() > 0);
        assertEquals(mockLocalDate.now(),tomorrowTasks.get(0).activeDate());


        model.insertNewTask(recurDaily);
        //AND WHEN I complete a task and move forward a day,
        model.completeTask(recurDaily);
        assertNotNull(model.getOrderedTasks().getValue());
        model.timeTravelForward();
        model.updateTasks();

        // THEN the task should appear uncompleted
        List<Task> result =  model.getOrderedTasks().getValue();
        assertEquals(false,result.get(0).complete());


    }

    //MS1 Tests Below

        // WHEN
//        model.insertNewTask(new Task(null, "New Task", 0, false));
//
//        var expectedList = List.of(new Task(0, "New Task", 0, false));
        // THEN
//        assertEquals(expectedList, model.getOrderedTasks().getValue());
//    }
/*
    @Test
    public void addNewTaskToNonEmpty(){
        var dataSource = InMemoryDataSource.fromDefault();
        var repo = new SimpleTaskRepository(dataSource);
        var model = new MainViewModel(repo);

        // WHEN
        model.insertNewTask(new Task(null, "Bake cookies", 2, false));

        var expectedList = List.of(
                new Task(0, "Get groceries", 0,  false),
                new Task(1, "Find cookie recipe", 1, false),
                new Task(4, "Bake cookies", 2, false),
                new Task(2, "Study", 3, true),
                new Task(3, "Sleep", 4, true)
        );

        // THEN
        assertEquals(new HashSet<>(expectedList), new HashSet<Task>(model.getOrderedTasks().getValue()));
    }

    @Test
    public void completeTask(){
        var dataSource = InMemoryDataSource.fromDefault();
        var repo = new SimpleTaskRepository(dataSource);
        var model = new MainViewModel(repo);

        model.completeTask(new Task(0, "Get groceries", 0, true));

        var expectedList = List.of(
                new Task(0, "Get groceries", 3,  true),
                new Task(1, "Find cookie recipe", 0, false),
                new Task(2, "Study", 1, true),
                new Task(3, "Sleep", 2, true)
        );

        assertEquals(new HashSet<>(expectedList), new HashSet<Task>(model.getOrderedTasks().getValue()));
    }

    @Test
    public void uncompleteTask(){
        var dataSource = InMemoryDataSource.fromDefault();
        var repo = new SimpleTaskRepository(dataSource);
        var model = new MainViewModel(repo);

        model.insertNewTask(new Task(2, "Study", 2, false));

        var expectedList = List.of(
                new Task(0, "Get groceries", 0,  false),
                new Task(1, "Find cookie recipe", 1, false),
                new Task(2, "Study", 2, false),
                new Task(3, "Sleep", 4, true)
        );

        assertEquals(new HashSet<>(expectedList), new HashSet<Task>(model.getOrderedTasks().getValue()));
    }

    @Test
    public void uncompleteTask2(){
        var dataSource = InMemoryDataSource.fromDefault();
        var repo = new SimpleTaskRepository(dataSource);
        var model = new MainViewModel(repo);

        model.insertNewTask(new Task(3, "Sleep", 2, false));

        var expectedList = List.of(
                new Task(0, "Get groceries", 0,  false),
                new Task(1, "Find cookie recipe", 1, false),
                new Task(2, "Study", 3, true),
                new Task(3, "Sleep", 2, false)
        );

        assertEquals(new HashSet<>(expectedList), new HashSet<Task>(model.getOrderedTasks().getValue()));
    }
 */
}
