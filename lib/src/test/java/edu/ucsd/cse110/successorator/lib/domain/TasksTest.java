package edu.ucsd.cse110.successorator.lib.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TasksTest {

    private Task cookies, waterDog, getGroceries, drinkMilk, petGoldfish, cutHair,
            watchPaintDryDaily, watchPaintDryWeekly, watchPaintDryMonthly, watchPaintDryYearly;

    private List<Task> emptyTasks, tasks, tasksDaily, tasksWeekly, tasksMonthly, tasksYearly;

    @Before
    public void setup(){
        cookies = new Task(null, "Bake cookies", 1, false,
                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), 1,
                LocalDateTime.now().withNano(0), LocalDate.now(), Context.HOME);
        waterDog = new Task(null, "Water dog", 2, false,
                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), 1,
                LocalDateTime.now().withNano(0), LocalDate.now(), Context.HOME);
        getGroceries = new Task(null, "Get groceries", 3, false,
                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), 1,
                LocalDateTime.now().withNano(0), LocalDate.now(), Context.HOME);
        drinkMilk = new Task(null, "Drink milk", 4, true,
                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), 1,
                LocalDateTime.now().withNano(0), LocalDate.now(), Context.HOME);
        cutHair = new Task(null, "Cut hair", 5, true,
                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), 1,
                LocalDateTime.now().withNano(0), LocalDate.now(), Context.HOME);
        petGoldfish = new Task(null, "Pet goldfish", 6, false,
                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), 1,
                LocalDateTime.now().withNano(0), LocalDate.now(), Context.HOME);
        watchPaintDryDaily = new Task(null, "Watch paint dry", 6, true,
                LocalDate.now(), Frequency.DAILY, LocalDate.now().getDayOfWeek(), 1,
                LocalDateTime.now().withNano(0), LocalDate.now().plusDays(1), Context.HOME);
        watchPaintDryWeekly = new Task(null, "Watch paint dry", 6, true,
                LocalDate.now(), Frequency.WEEKLY, LocalDate.now().getDayOfWeek(), 1,
                LocalDateTime.now().withNano(0), LocalDate.now().plusWeeks(1), Context.HOME);

        LocalDate placeholder = LocalDate.of(2024, 3, 14);

        watchPaintDryMonthly = new Task(null, "Watch paint dry", 6, true,
                placeholder, Frequency.MONTHLY, placeholder.getDayOfWeek(), 2,
                LocalDateTime.now().withNano(0), LocalDate.of(2024, 4, 11), Context.HOME);

        watchPaintDryYearly = new Task(null, "Watch paint dry", 6, true,
                LocalDate.now(), Frequency.YEARLY, LocalDate.now().getDayOfWeek(), 1,
                LocalDateTime.now().withNano(0), LocalDate.now().plusYears(1), Context.HOME);


        emptyTasks = List.of();
        tasks = new ArrayList<>(Arrays.asList(cookies, waterDog, getGroceries, drinkMilk, cutHair));
        tasksDaily = new ArrayList<>(Arrays.asList(cookies, waterDog, getGroceries, drinkMilk,
                cutHair, watchPaintDryDaily));
        tasksWeekly = new ArrayList<>(Arrays.asList(cookies, waterDog, getGroceries, drinkMilk,
                cutHair, watchPaintDryWeekly));
        tasksMonthly = new ArrayList<>(Arrays.asList(cookies, waterDog, getGroceries, drinkMilk,
                cutHair, watchPaintDryMonthly));
        tasksYearly = new ArrayList<>(Arrays.asList(cookies, waterDog, getGroceries, drinkMilk,
                cutHair, watchPaintDryYearly));
    }

    @Test
    public void insertTaskFromEmpty() {
        var actual = Tasks.insertTask(emptyTasks, cookies);

        var expected = List.of(cookies);

        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
    }

    @Test
    public void insertNewTask() {
        var actual = Tasks.insertTask(tasks, petGoldfish);

        List<Task> expected = List.of(cookies, waterDog, getGroceries, drinkMilk.withSortOrder(5),
                petGoldfish.withSortOrder(4), cutHair.withSortOrder(6));

        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
    }

    @Test
    public void completeTask() {
        var actual = Tasks.completeTask(tasks, waterDog);

        List<Task> expected = List.of(cookies, getGroceries.withSortOrder(2),
                drinkMilk.withSortOrder(3), waterDog.withComplete(true).withSortOrder(5),
                cutHair.withSortOrder(4));

        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
    }

    @Test
    public void uncompleteTask() {
        var actual = Tasks.uncompleteTask(tasks, drinkMilk);

        var expected = List.of(cookies, waterDog, getGroceries, drinkMilk.withComplete(false),
                cutHair);

        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
    }

    @Test
    public void uncompleteTask2(){
        var actual = Tasks.uncompleteTask(tasks, cutHair);

        var expected = List.of(cookies, waterDog, getGroceries,
                cutHair.withSortOrder(4).withComplete(false), drinkMilk.withSortOrder(5));

        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
    }

    @Test
    public void recurrDailyTask(){
        var actual = Tasks.updateTasks(tasksDaily);

        var expected = List.of(cookies, waterDog, getGroceries, watchPaintDryDaily.withActiveDate(LocalDate.now().plusDays(1)).withExpirationDate(LocalDate.now().plusDays(2)).withComplete(false).withSortOrder(4));

        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
    }

    @Test
    public void recurrWeeklyTask(){
        var actual = Tasks.updateTasks(tasksWeekly);

        var expected = List.of(cookies, waterDog, getGroceries, watchPaintDryWeekly.withActiveDate(LocalDate.now().plusDays(7)).withExpirationDate(LocalDate.now().plusDays(14)).withComplete(false).withSortOrder(4));

        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
    }

    @Test
    public void recurrYearlyTask(){
        var actual = Tasks.updateTasks(tasksYearly);

        var expected = List.of(cookies, waterDog, getGroceries, watchPaintDryYearly.withActiveDate(LocalDate.now().plusYears(1)).withExpirationDate(LocalDate.now().plusYears(2)).withComplete(false).withSortOrder(4));

        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
    }

    @Test
    public void recurrMonthlyTask(){
        var actual = Tasks.updateTasks(tasksMonthly);

        var expected = List.of(cookies, waterDog, getGroceries, watchPaintDryMonthly.withActiveDate(watchPaintDryMonthly.expirationDate()).withExpirationDate(LocalDate.of(2024, 5, 9)).withComplete(false).withSortOrder(4));

        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
    }
}