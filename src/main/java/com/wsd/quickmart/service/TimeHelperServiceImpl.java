package com.wsd.quickmart.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
final public class TimeHelperServiceImpl implements TimeHelperService {
    
    @Override
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    @Override
    public LocalDate getFirstDayOfThePreviousMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1);
    }
}
