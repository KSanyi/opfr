package hu.kits.opfr.domain.common;

import java.time.LocalDate;

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
    
}
