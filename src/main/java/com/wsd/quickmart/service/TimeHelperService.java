package com.wsd.quickmart.service;

import java.time.LocalDate;

public interface TimeHelperService {
    LocalDate getCurrentDate();

    LocalDate getFirstDayOfThePreviousMonth();
}
