package edu.ucsd.cse110.successorator.lib;

import static org.junit.Assert.*;

import edu.ucsd.cse110.successorator.lib.domain.Task;
import org.junit.Test;

public class TaskTest {

    @Test
    public void id() {
        Task task1 = new Task(20,"Get 2 Groceries",3);
        long idResult = task1.id();
        assertEquals(idResult,20);
    }

    @Test
    public void taskName() {
        Task task1 = new Task(20,"Get 2 Groceries",3);
        String taskResult = task1.taskName();
        assertEquals(taskResult,"Get 2 Groceries");
    }

    @Test
    public void sortOrder() {
        Task task1 = new Task(10,"Get 2 Groceries",5);
        int sortOrderResult = task1.sortOrder();

    }

    @Test
    public void withId() {
        Task task = new Task(20,"Get 2 Groceries",3);
        task = task.withId(5);
        Task expected = new Task(5, "Get 2 Groceries",3);


        boolean taskEquality = expected.equals(task);
        assertTrue(taskEquality);
    }

    @Test
    public void withSortOder() {
        Task task = new Task(20,"Get 2 Groceries",3);
        task = task.withSortOrder(5);
        Task expected = new Task(20, "Get 2 Groceries",5);


        boolean taskEquality = expected.equals(task);
        assertTrue(taskEquality);

    }
}