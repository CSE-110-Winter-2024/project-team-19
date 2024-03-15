package edu.ucsd.cse110.successorator.data.db;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Converters {
    @TypeConverter
    public static LocalDate dateFromString(String string){
        return LocalDate.parse(string);
    }

    @TypeConverter
    public static String stringFromDate(LocalDate date){
        return date.toString();
    }

    @TypeConverter
    public static LocalDateTime dateTimeFromString(String string){
        return LocalDateTime.parse(string);
    }

    @TypeConverter
    public static String stringFromDateTime(LocalDateTime date){
        return date.toString();
    }
}
