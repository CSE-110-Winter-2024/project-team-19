package edu.ucsd.cse110.successorator.data.db;

import org.junit.Test;
import org.junit.runner.manipulation.Ordering;

import static org.junit.Assert.*;

import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.MainViewModelMock;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Context;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.MockLocalDate;
import edu.ucsd.cse110.successorator.lib.domain.SimpleTaskRepository;
import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.lib.domain.TaskBuilder;
import edu.ucsd.cse110.successorator.lib.domain.Tasks;
import edu.ucsd.cse110.successorator.ui.TaskListAdapter;

public class MockRepositoryTests {

    @Test
    public void US11BDDScenario() {
        // GIVEN we create a weekly Task Today from the today view
        MockLocalDate.setDate(LocalDate.now());
        var dataSource = new InMemoryDataSource();
        var repo = new SimpleTaskRepository(dataSource);
        var model = new MainViewModelMock(repo);

        Task recurWeekly = new TaskBuilder().withTaskName("Get 2 Groceries").withFrequency(Frequency.WEEKLY).build();

        List<Task> myTasks = new ArrayList<>();

        myTasks.add(recurWeekly);

        myTasks = Tasks.updateTasks(myTasks);

        model.insertNewTask(myTasks.get(0));

        //WHEN I move forward a week
        for (int i = 0; i < 7; i++){
        model.timeTravelForward();}

        //THEN The task should be set to display now
        LocalDate actual = model.getOrderedTasks().getValue().get(0).activeDate();
        assertEquals(MockLocalDate.now(), actual);

    }
    @Test
    public void US12BDDScenario(){
        //GIVEN I have a daily task that I can see on the today view
        MockLocalDate.setDate(LocalDate.now());
        var dataSource = new InMemoryDataSource();
        var repo = new SimpleTaskRepository(dataSource);
        var model = new MainViewModelMock(repo);

        Task recurDaily = new TaskBuilder().withID(4).withTaskName("Get 2 Groceries").withFrequency(Frequency.DAILY).
        build();


        dataSource.putTask(recurDaily);

        List<Task> myTasks = dataSource.getTasks();

        List<Task> tomorrowTasks = new ArrayList<>();

        tomorrowTasks.addAll(new ArrayList<>(myTasks).stream()
                    .filter(task -> task.activeDate().isAfter(MockLocalDate.now())
                            && task.activeDate().isBefore(MockLocalDate.now().plusDays(2))
                            || task.frequency().equals(Frequency.DAILY))
                    .collect(Collectors.toList()));

        //THEN it should also appear in the Tomorrow View
        assertTrue(tomorrowTasks.size() > 0);
        assertEquals(MockLocalDate.now(),tomorrowTasks.get(0).activeDate());


        model.insertNewTask(recurDaily);
        //AND WHEN I complete a task and move forward a day,
        model.completeTask(recurDaily);
        assertNotNull(model.getOrderedTasks().getValue());
        model.timeTravelForward();
        model.updateTasks();

        // THEN the task should appear uncompleted
        List<Task> result =  model.getOrderedTasks().getValue();
        assertEquals(false, result.get(0).complete());


    }
    @Test
    public void US13BDDScenario(){
        //Given Im in recurring view and I create a daily recurring task
        MockLocalDate.setDate(LocalDate.now());
        var dataSource = new InMemoryDataSource();
        var repo = new SimpleTaskRepository(dataSource);
        var model = new MainViewModelMock(repo);

        Task recurDaily = new TaskBuilder().withTaskName("Get 2 Groceries").withFrequency(Frequency.DAILY).build();


        List<Task> myTasks = new ArrayList<>();
        myTasks.add(recurDaily);

        myTasks = Tasks.updateTasks(myTasks);
        model.insertNewTask(myTasks.get(0));



        //When I fast forward 1 day
        model.timeTravelForward();

        //The task should have its active date be today
        LocalDate actual = model.getOrderedTasks().getValue().get(0).activeDate();
        assertEquals(MockLocalDate.now(),actual);

    }
    @Test
    public void US14BDDScenario(){
        //Given I have a school task “study for midterm” in my Today list,
        MockLocalDate.setDate(LocalDate.now());
        var dataSource = new InMemoryDataSource();
        var repo = new SimpleTaskRepository(dataSource);
        var model = new MainViewModelMock(repo);

        Task studyMidterm = new TaskBuilder().withID(4).withTaskName("Study Midterm").withFrequency(Frequency.DAILY).
                withContext(Context.SCHOOL).build();


        List<Task> myTasks = new ArrayList<>();
        myTasks.add(studyMidterm);

        myTasks = Tasks.updateTasks(myTasks);
        model.insertNewTask(myTasks.get(0));

        //When I create a one-time task with the work context, “Fire employees”,
        Task fireEmployees = new TaskBuilder().withID(4).withTaskName("Fire Employees").withFrequency(Frequency.DAILY).
                withContext(Context.WORK).build();


        myTasks.add(studyMidterm);

        myTasks = Tasks.updateTasks(myTasks);
        model.insertNewTask(myTasks.get(0));

        //Then it should be in my list of tasks above the task “Study for midterm”.
        List<Task> result = model.getOrderedTasks().getValue();
        result = result.stream()
                .sorted((task1, task2) -> task1.context().compareTo(task2.context()))
                .collect(Collectors.toList());

        assertEquals(Context.SCHOOL,result.get(0).context());


    }
    @Test
    public void US15BDDScenario(){
//        Given I have no tasks in the “today” view,
//                When I open the menu for selecting a context for focus mode,
//        AND select the  context “work”,
//        Then nothing should change in the UI,
//                When I add a task called “Write email” with the work context,
//        Then it should show up on the UI,
//        When I add a task called “Study” with the school context,
//        Then it should not visible,
//                When I exit focus mode,
//        Then both tasks, “Write email” and “Study” are now visible.

    }
    @Test
    public void US16BDDScenario(){
        //        Given Sara has the context menu open for the pending task “Walk the plant”
        MockLocalDate.setDate(LocalDate.now());
        var dataSource = new InMemoryDataSource();
        var repo = new SimpleTaskRepository(dataSource);
        var model = new MainViewModelMock(repo);

        Task pending1 = new TaskBuilder().withID(4).withTaskName("Get 2 Groceries").withFrequency(Frequency.PENDING).build();

        model.insertNewTask(pending1);



//        When Sara longpresses the task, and selectes "finish" from the context menu
//        Then “Walk the plant” is removed from Pending
//        And “Walk the plant” appears as a slashed through item under today’s finished tasks


        dataSource.sendPendingtoToday(pending1.id());


        Task resultingTask = model.getOrderedTasks().getValue().get(0);
        assertNotNull(resultingTask);
        assertEquals(resultingTask.frequency() , Frequency.ONE_TIME);
        assertEquals(resultingTask.activeDate() , MockLocalDate.now());

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
