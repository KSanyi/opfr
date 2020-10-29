package hu.kits.opfr.domain.common;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyTimeRange(LocalDate date, TimeRange timeRange) {

    public DailyTimeRange(LocalDate date, int startAt, int hours) {
        this(date, new TimeRange(startAt, hours));
    }
    
    public DailyTimeRange(String dateString, int startAt, int hours) {
        this(LocalDate.parse(dateString), new TimeRange(startAt, hours));
    }
    
    public boolean intersectWith(DailyTimeRange other) {
        return date.equals(other.date) && timeRange.intersectWith(other.timeRange);
    }

    public LocalDateTime startDateTime() {
        return date.atTime(timeRange.startAt(), 0);
    }

    public LocalDateTime endDateTime() {
        return date.atTime(timeRange.endAt(), 0);
    }
    
}
