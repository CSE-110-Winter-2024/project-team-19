package edu.ucsd.cse110.successorator.lib.data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

/*
Class adapted from the InMemoryDataSource class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */

/**
 * Class used as a sort of "database" of tasks that exist. This
 * will be replaced with a real database in the future, but can also be used
 * for testing.
 */
public class InMemoryDataSource {
    private int nextId = 0;

    private int minSortOrder = Integer.MAX_VALUE;
    private int maxSortOrder = Integer.MIN_VALUE;
    private int minCompletedSortOrder = 0;

    private final Map<Integer, Task> tasks
            = new HashMap<>();
    private final Map<Integer, MutableSubject<Task>> taskSubjects
            = new HashMap<>();
    private final MutableSubject<List<Task>> allTasksSubject
            = new SimpleSubject<>();

    public InMemoryDataSource() {
    }

    public final static List<Task> DEFAULT_TASKS = List.of(
            new Task(2001, "Get 2 Groceries",1, false,
                    LocalDate.now(), Frequency.ONE_TIME, LocalDate.now().getDayOfWeek(), null),

           new Task(2002, "Weekly Recurring ",2, false,
                    LocalDate.now(), Frequency.WEEKLY, LocalDate.now().getDayOfWeek(), null)



    );

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
//        data.putTasks(DEFAULT_TASKS);
        return data;
    }

    public List<Task> getTasks() {
        return List.copyOf(tasks.values());
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subject<Task> getTaskSubject(int id) {
        if (!taskSubjects.containsKey(id)) {
            var subject = new SimpleSubject<Task>();
            subject.setValue(getTask(id));
            taskSubjects.put(id, subject);
        }
        return taskSubjects.get(id);
    }

    public Subject<List<Task>> getAllTasksSubject() {
        return allTasksSubject;
    }

    public int getMinSortOrder() {
        return minSortOrder;
    }

    public int getMaxSortOrder() {
        return maxSortOrder;
    }

    public int getMinCompletedSortOrder(){
        return minCompletedSortOrder;
    }

    public void putTask(Task task) {
        var fixedTask = preInsert(task);

        tasks.put(fixedTask.id(), fixedTask);
        postInsert();
        assertSortOrderConstraints();

        if (taskSubjects.containsKey(fixedTask.id())) {
            taskSubjects.get(fixedTask.id()).setValue(fixedTask);
        }
        allTasksSubject.setValue(getTasks());
    }

    public void putTasks(List<Task> cards) {
        var fixedTasks = cards.stream()
                .map(this::preInsert)
                .collect(Collectors.toList());

        fixedTasks.forEach(card -> tasks.put(card.id(), card));
        postInsert();
        assertSortOrderConstraints();

        fixedTasks.forEach(card -> {
            if (taskSubjects.containsKey(card.id())) {
                taskSubjects.get(card.id()).setValue(card);
            }
        });
        allTasksSubject.setValue(getTasks());
    }

    public void removeTask(int id) {
        var task = tasks.get(id);
        var sortOrder = task.sortOrder();

        tasks.remove(id);
        shiftSortOrders(sortOrder, maxSortOrder, -1);

        if(taskSubjects.containsKey(id)){
            taskSubjects.get(id).setValue(null);
        }
        allTasksSubject.setValue(getTasks());
    }

    public void sendPendingtoToday(int id){
        Task task = tasks.get(id);
        tasks.remove(id);
        List<Task> mytasks = new ArrayList<>();
        mytasks.add(task.withActiveDate(LocalDate.now()).withComplete(true)
                .withFrequency(Frequency.ONE_TIME));
        putTasks(mytasks);
    }
    public void completeTask(int id){
        Task task = tasks.get(id);

        //var sortOrder = task.sortOrder();

        tasks.remove(id);
        //shiftSortOrders(sortOrder, maxSortOrder, -1);

        putTask(task.withComplete(true).withSortOrder(getMaxSortOrder() + 1));
    }


    public void clear(){tasks.clear();}
    public void uncompleteTask(int id){
        var task = tasks.get(id);
        var sortOrder = task.sortOrder();

        tasks.remove(id);
        shiftSortOrders(minCompletedSortOrder, task.sortOrder(), 1);

        putTask(task.withComplete(false).withSortOrder(minCompletedSortOrder));
    }

    public void shiftSortOrders(int from, int to, int by) {
        var tasks = this.tasks.values().stream()
                .filter(card -> card.sortOrder() >= from && card.sortOrder() <= to)
                .map(card -> card.withSortOrder(card.sortOrder() + by))
                .collect(Collectors.toList());

        putTasks(tasks);
    }

    /**
     * Private utility method to maintain state of the fake DB: ensures that new
     * cards inserted have an id, and updates the nextId if necessary.
     */
    private Task preInsert(Task task) {
        var id = task.id();
        if (id == null) {
            // If the task has no id, give it one.
            task = task.withId(nextId++);
        }
        else if (id > nextId) {
            // If the task has an id, update nextId if necessary to avoid giving out the same
            // one. This is important for when we pre-load cards like in fromDefault().
            nextId = id + 1;
        }

        return task;
    }

    /**
     * Private utility method to maintain state of the fake DB: ensures that the
     * min and max sort orders are up to date after an insert.
     */
    private void postInsert() {
        // Keep the min and max sort orders up to date.
        minSortOrder = tasks.values().stream()
                .map(Task::sortOrder)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);

        maxSortOrder = tasks.values().stream()
                .map(Task::sortOrder)
                .max(Integer::compareTo)
                .orElse(Integer.MIN_VALUE);

        minCompletedSortOrder = tasks.values().stream()
                .filter(Task::complete)
                .map(Task::sortOrder)
                .min(Integer::compareTo)
                .orElse(maxSortOrder + 1);
    }

    /**
     * Safety checks to ensure the sort order constraints are maintained.
     * <p></p>
     * Will throw an AssertionError if any of the constraints are violated,
     * which should never happen. Mostly here to make sure I (Dylan) don't
     * write incorrect code by accident!
     */
    private void assertSortOrderConstraints() {
        // Get all the sort orders...
        var sortOrders = tasks.values().stream()
                .map(Task::sortOrder)
                .collect(Collectors.toList());

        // Non-negative...
        assert sortOrders.stream().allMatch(i -> i >= 0);

        // Unique...
        assert sortOrders.size() == sortOrders.stream().distinct().count();

        // Between min and max...
        assert sortOrders.stream().allMatch(i -> i >= minSortOrder);
        assert sortOrders.stream().allMatch(i -> i <= maxSortOrder);
    }
}
