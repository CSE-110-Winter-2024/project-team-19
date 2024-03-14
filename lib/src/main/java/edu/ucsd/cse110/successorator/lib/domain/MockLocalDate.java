package edu.ucsd.cse110.successorator.lib.domain;

import java.io.Serializable;
import java.time.LocalDate;

public class MockLocalDate implements Serializable {

    private static LocalDate date;

    public static LocalDate now(){
        return date;
    }

    public static void setDate(LocalDate newDate){
        date = newDate;
    }

    public static void advanceDate(){
        date = date.plusDays(1);
    }
}

