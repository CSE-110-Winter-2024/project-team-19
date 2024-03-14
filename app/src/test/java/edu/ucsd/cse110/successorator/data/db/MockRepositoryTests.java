package edu.ucsd.cse110.successorator.data.db;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.SimpleTaskRepository;
import edu.ucsd.cse110.successorator.lib.domain.Task;

public class MockRepositoryTests {
//
//    @Test
//    public void addNewTaskFromEmpty() {
//        // GIVEN
//        var dataSource = new InMemoryDataSource();
//        var repo = new SimpleTaskRepository(dataSource);
//        var model = new MainViewModel(repo);

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
