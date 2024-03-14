package edu.ucsd.cse110.successorator.lib.domain;

import java.io.Serializable;
import java.time.LocalDate;

public class MockLocalDate implements Serializable {

    private LocalDate date;

    public MockLocalDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate now(){
        return date;
    }

    public void setDate(LocalDate newDate){
        date = newDate;
    }

    public void advanceDate(){
        date = date.plusDays(1);
    }
}

