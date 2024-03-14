package edu.ucsd.cse110.successorator.lib;

import static org.junit.Assert.*;

import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.Task;
import org.junit.Test;

import java.time.LocalDate;

public class TaskTest {

//    @Test
//    public void id() {
//        Task task1 = new Task(20,"Get 2 Groceries",3, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//        long idResult = task1.id();
//        assertEquals(20, idResult);
//    }
//
//    @Test
//    public void taskName() {
//        Task task1 = new Task(20,"Get 2 Groceries",3, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//        String taskResult = task1.taskName();
//        assertEquals("Get 2 Groceries", taskResult);
//    }
//
//    @Test
//    public void sortOrder() {
//        Task task1 = new Task(10,"Get 2 Groceries",5, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//        int sortOrderResult = task1.sortOrder();
//        assertEquals(5, sortOrderResult);
//    }
//
//    @Test
//    public void complete() {
//        Task task1 = new Task(10,"Get 2 Groceries",5, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//        assertFalse(task1.complete());
//    }
//
//    @Test
//    public void activeDate(){
//        Task task1 = new Task(10,"Get 2 Groceries",5, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//        assertEquals(LocalDate.now(), task1.activeDate());
//    }
//
//    @Test
//    public void frequency(){
//        Task task1 = new Task(10,"Get 2 Groceries",5, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//        assertEquals(Frequency.ONE_TIME, task1.frequency());
//    }
//
//    @Test
//    public void dayOfWeek(){
//        Task task1 = new Task(10,"Get 2 Groceries",5, false,
//                LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(),
//                null);
//        assertEquals(LocalDate.now().getDayOfWeek(), task1.dayOfWeek());
//    }
//
//    @Test
//    public void dayOccurrence(){
//        Task task1 = new Task(10,"Get 2 Groceries",5, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, 1);
//        assertEquals(1, task1.dayOccurrence());
//    }
//
//    @Test
//    public void withId() {
//        Task task = new Task(20,"Get 2 Groceries",3, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//        task = task.withId(5);
//        Task expected = new Task(5, "Get 2 Groceries",3, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//
//        boolean taskEquality = expected.equals(task);
//        assertTrue(taskEquality);
//    }
//
//    @Test
//    public void withSortOrder() {
//        Task task = new Task(20,"Get 2 Groceries",3, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//        task = task.withSortOrder(5);
//        Task expected = new Task(20, "Get 2 Groceries",5, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//
//
//        boolean taskEquality = expected.equals(task);
//        assertTrue(taskEquality);
//
//    }
//
//    @Test
//    public void withComplete(){
//        Task task = new Task(20,"Get 2 Groceries",3, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//        task = task.withComplete(true);
//        Task expected = new Task(20, "Get 2 Groceries",3, true,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//
//
//        boolean taskEquality = expected.equals(task);
//        assertTrue(taskEquality);
//    }
//
//    @Test
//    public void withActiveDate(){
//        Task task = new Task(20,"Get 2 Groceries",3, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//        task = task.withActiveDate(LocalDate.now().plusDays(1));
//        Task expected = new Task(20, "Get 2 Groceries",3, false,
//                LocalDate.now().plusDays(1), Frequency.ONE_TIME, null, null);
//
//
//        boolean taskEquality = expected.equals(task);
//        assertTrue(taskEquality);
//    }
//
//    @Test
//    public void withFrequency(){
//        Task task = new Task(20,"Get 2 Groceries",3, false,
//                LocalDate.now(), Frequency.PENDING, null, null);
//        task = task.withFrequency(Frequency.ONE_TIME);
//        Task expected = new Task(20, "Get 2 Groceries",3, false,
//                LocalDate.now(), Frequency.ONE_TIME, null, null);
//
//
//        boolean taskEquality = expected.equals(task);
//        assertTrue(taskEquality);
//    }
//
//    @Test
//    public void withDayOccurrence(){
//        Task task = new Task(20,"Get 2 Groceries",3, false,
//                LocalDate.now(), Frequency.PENDING, null, 3);
//        task = task.withDayOccurrence(4);
//        Task expected = new Task(20, "Get 2 Groceries",3, false,
//                LocalDate.now(), Frequency.PENDING, null, 4);
//
//
//        boolean taskEquality = expected.equals(task);
//        assertTrue(taskEquality);
//    }
}