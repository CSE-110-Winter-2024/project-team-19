package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.util.Subject;

/*
This class was adapted from the FlashcardRepository class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public interface TaskRepository {
    Subject<Task> find(int id);
    Subject<List<Task>> findAll();
    void save(Task task);
    void save(List<Task> tasks);
    void complete(Task task);
    void uncomplete(Task task);
    void remove(Task task);
    void deleteCompletedTasks();
}
