package edu.ucsd.cse110.successorator.util;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;


    public class DateRolloverMock {
        //this class takes in a DateTimeObject which is the current time, and a strictly
        //positive int which is the # of hours to move forward the current Time.
        //It also logs the current time and new current time for testing purposes
        LocalDateTime fakeTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        public DateRolloverMock(LocalDateTime input){fakeTime = input;}

        public void logTime(){
            Log.d("Current (fake) time: ",fakeTime.format(formatter));}

        public LocalDateTime getFakeTime(){return fakeTime;}

        public String getDateAsStringMock(){
            //I don't think we need to consider before/after 2 pm for mock
//            LocalTime rolloverTime = LocalTime.of(14, 0);
//            if (fakeTime.toLocalTime().isBefore(rolloverTime)) {
//                fakeTime = fakeTime.with(rolloverTime);
//            } else {
//                fakeTime = fakeTime.plusDays(1).with(rolloverTime);
//            }
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

            String StringOfDate = fakeTime.format(myFormatObj).toString();
            return StringOfDate;
        }

        public LocalDateTime addDay() {fakeTime = fakeTime.plusDays(1);
            return fakeTime;}
    }



