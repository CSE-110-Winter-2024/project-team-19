package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.util.Subject;

public interface TaskRepository {
    Subject<Task> find(int id);
    Subject<List<Task>> findAll();
    void save(Task task);
    void remove(int id);
    void append(Task task);
    void prepend(Task task);
}