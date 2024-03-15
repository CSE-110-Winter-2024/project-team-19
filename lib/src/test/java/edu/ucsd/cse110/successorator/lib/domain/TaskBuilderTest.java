package edu.ucsd.cse110.successorator.lib.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TaskBuilderTest {

    static {
        MockLocalDate.setDate(LocalDate.now());
    }


    @Test
    public void buildOneTime() {
        TaskBuilder oneTime = new TaskBuilder()
                .withTaskName("Hello")
                .withFrequency(Frequency.ONE_TIME);
        var actual = oneTime.build();
        var expected = new Task(1, "Hello", 0, false,
                MockLocalDate.now(), Frequency.ONE_TIME, MockLocalDate.now().getDayOfWeek(),
                Tasks.calculateOccurrence(MockLocalDate.now()),
                LocalDateTime.of(MockLocalDate.now(), LocalTime.now()).withNano(0),
                MockLocalDate.now().plusDays(1));
        assertEquals(expected, actual);
    }

    @Test
    public void buildWeekly() {
        TaskBuilder oneTime = new TaskBuilder()
                .withTaskName("Hello")
                .withFrequency(Frequency.WEEKLY);
        var actual = oneTime.build();
        var expected = new Task(1, "Hello", 0, false,
                MockLocalDate.now(), Frequency.WEEKLY, MockLocalDate.now().getDayOfWeek(),
                Tasks.calculateOccurrence(MockLocalDate.now()),
                LocalDateTime.of(MockLocalDate.now(), LocalTime.now()).withNano(0),
                MockLocalDate.now().plusWeeks(1));
        assertEquals(expected, actual);
    }

    @Test
    public void buildMonthly() {
        TaskBuilder oneTime = new TaskBuilder()
                .withTaskName("Hello")
                .withFrequency(Frequency.MONTHLY);
        var actual = oneTime.build();
        var expected = new Task(1, "Hello", 0, false,
                MockLocalDate.now(), Frequency.MONTHLY, MockLocalDate.now().getDayOfWeek(),
                Tasks.calculateOccurrence(MockLocalDate.now()),
                LocalDateTime.of(MockLocalDate.now(), LocalTime.now()).withNano(0),
                Tasks.nextRecurrenceDate(new Task(null,
                        "", 0, false, MockLocalDate.now(),
                        Frequency.MONTHLY, null,
                        Tasks.calculateOccurrence(MockLocalDate.now()),
                        null, null)));
        assertEquals(expected, actual);
    }

    @Test
    public void buildYearly() {
        TaskBuilder oneTime = new TaskBuilder()
                .withTaskName("Hello")
                .withFrequency(Frequency.YEARLY);
        var actual = oneTime.build();
        var expected = new Task(1, "Hello", 0, false,
                MockLocalDate.now(), Frequency.YEARLY, MockLocalDate.now().getDayOfWeek(),
                Tasks.calculateOccurrence(MockLocalDate.now()),
                LocalDateTime.of(MockLocalDate.now(), LocalTime.now()).withNano(0),
                MockLocalDate.now().plusYears(1));
        assertEquals(expected, actual);
    }
}