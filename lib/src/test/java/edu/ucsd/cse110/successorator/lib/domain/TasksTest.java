package edu.ucsd.cse110.successorator.lib.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TasksTest {

    private Task cookies, waterDog, getGroceries, drinkMilk, petGoldfish, cutHair;

    private List<Task> emptyTasks, tasks;

    @Before
    public void setup(){
        cookies = new Task(null, "Bake cookies", 1, false,
                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), 1);
        waterDog = new Task(null, "Water dog", 2, false,
                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), 1);
        getGroceries = new Task(null, "Get groceries", 3, false,
                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), 1);
        drinkMilk = new Task(null, "Drink milk", 4, true,
                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), 1);
        cutHair = new Task(null, "Cut hair", 5, true,
                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), 1);
        petGoldfish = new Task(null, "Pet goldfish", 6, false,
                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), 1);


        emptyTasks = List.of();
        tasks = new ArrayList<>(Arrays.asList(cookies, waterDog, getGroceries, drinkMilk, cutHair));
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
    }

    @Test
    public void removeTask() {
        List<Task> tasks = List.of();
    }
}