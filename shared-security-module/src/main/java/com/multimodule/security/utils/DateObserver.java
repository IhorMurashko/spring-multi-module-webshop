package com.multimodule.security.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateObserver {

    public static Date dateExpirationGenerator(Date from, long validityPeriodInSeconds) {
        return new Date(from.getTime() + validityPeriodInSeconds);
    }


}
