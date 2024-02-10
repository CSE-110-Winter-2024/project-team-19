package edu.ucsd.cse110.successorator.util;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import edu.ucsd.cse110.successorator.lib.util.Observer;
import edu.ucsd.cse110.successorator.lib.util.Subject;

/*
This class was provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class LiveDataSubjectAdapter<T> implements Subject<T> {
    private final LiveData<T> adaptee;

    public LiveDataSubjectAdapter(LiveData<T> adaptee){
        this.adaptee = adaptee;
    }

    @Nullable
    @Override
    public T getValue(){
        return adaptee.getValue();
    }

    @Override
    public void observe(Observer<T> observer){
        adaptee.observeForever(observer::onChanged);
    }

    @Override
    public void removeObserver(Observer<T> observer){
        adaptee.removeObserver(observer::onChanged);
    }
}
