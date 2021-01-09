package hu.kits.opfr.common;

import java.time.LocalDateTime;

import hu.kits.opfr.domain.common.DailyTimeRange;

public record DateTimeRange(LocalDateTime from, LocalDateTime to) implements Comparable<DateTimeRange> {

    public static final LocalDateTime MIN = LocalDateTime.of(1970,01,01,  0, 1);
    public static final LocalDateTime MAX = LocalDateTime.of(2049,12,31, 23,59);
    
    public static final DateTimeRange FULL = new DateTimeRange(null, null);
    
    public static DateTimeRange of(LocalDateTime from, LocalDateTime to) {
        return new DateTimeRange(from, to);
    }
    
    public static DateTimeRange of(String from, String to) {
        return new DateTimeRange(LocalDateTime.parse(from), LocalDateTime.parse(to));
    }
    
    public DateTimeRange {
        if(from == null) from = MIN;
        if(to == null) to = MAX;
        if(to.isBefore(from)) {
            throw new IllegalArgumentException("Invalid interval: " + from + " - " + to);
        }
    }
    
    public boolean contains(LocalDateTime date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }
    
    public boolean contains(DailyTimeRange dailyTimeRange) {
        return contains(dailyTimeRange.startDateTime()) && contains(dailyTimeRange.endDateTime());
    }
    
    @Override
    public String toString() {
        String fromString = from.equals(LocalDateTime.MIN) ? "" : from.toString();
        String toString = from.equals(MAX) ? "" : to.toString();
        return "[" + fromString + " - " + toString + "]";
    }
    
    @Override
    public int compareTo(DateTimeRange other) {
        return from.compareTo(other.from);
    }

}
    
