package hu.kits.opfr.common;

import java.time.LocalDateTime;

public class DateTimeUtils {

    public static LocalDateTime closestRoundHour(LocalDateTime dateTime) {
        LocalDateTime normalizedDateTime = dateTime.withMinute(0).withSecond(0).withNano(0);
        int minute = dateTime.getMinute();
        if(minute < 30) {
            return normalizedDateTime;
        } else {
            return normalizedDateTime.plusHours(1);
        }
    }
    
}
