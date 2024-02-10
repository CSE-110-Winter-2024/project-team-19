package edu.ucsd.cse110.successorator.util;

import androidx.lifecycle.MutableLiveData;

import edu.ucsd.cse110.successorator.lib.util.MutableSubject;

/*
This class was provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class MutableLiveDataSubjectAdapter <T>
    extends LiveDataSubjectAdapter<T>
    implements MutableSubject<T>
{
    private final MutableLiveData<T> mutableAdaptee;

    public MutableLiveDataSubjectAdapter(MutableLiveData<T> adaptee){
        super(adaptee);
        this.mutableAdaptee = adaptee;
    }

    @Override
    public void setValue(T value){
        mutableAdaptee.setValue(value);
    }
}
