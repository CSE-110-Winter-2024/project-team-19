package edu.ucsd.cse110.successorator.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.time.LocalDate;

public class MockLocalDateTest {

    @Test
    public void advanceDate() {
        MockLocalDate.setDate(LocalDate.now());
        assertEquals(MockLocalDate.now(), LocalDate.now());
    }
}