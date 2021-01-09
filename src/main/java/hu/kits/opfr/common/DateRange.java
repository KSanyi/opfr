package hu.kits.opfr.common;

import java.time.LocalDate;
import java.time.Month;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

public record DateRange(LocalDate from, LocalDate to) implements Comparable<DateRange>, Iterable<LocalDate> {

    public static final LocalDate MIN = LocalDate.of(1970,01,01);
    public static final LocalDate MAX = LocalDate.of(2049,12,31);
    
    public static final DateRange FULL = new DateRange(null,  null);
    
    public static DateRange of(LocalDate from, LocalDate to) {
        return new DateRange(from, to);
    }
    
    public static DateRange of(String from, String to) {
        return new DateRange(LocalDate.parse(from), LocalDate.parse(to));
    }
    
    public static DateRange of(Month month) {
        LocalDate from = Clock.today().withMonth(month.getValue()).withDayOfMonth(1);
        return new DateRange(from, from.plusMonths(1).minusDays(1));
    }
    
    public static DateRange of(int year) {
        LocalDate from = LocalDate.of(year, 1, 1);
        return new DateRange(from, from.plusYears(1).minusDays(1));
    }
    
    public static DateRange singleDay(LocalDate date) {
        return new DateRange(date, date);
    }
    
    public DateRange {
        if(from == null) from = MIN;
        if(to == null) to = MAX;
        if(to.isBefore(from)) {
            throw new IllegalArgumentException("Invalid interval: " + from + " - " + to);
        }
    }
    
    public boolean contains(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }
    
    public Stream<LocalDate> days() {
        return from.datesUntil(to.plusDays(1));
    }
    
    public int length() {
        return (int)days().count();
    }
    
    public Optional<DateRange> interSection(DateRange other) {
        if(from.isAfter(other.to) || other.from.isAfter(to)) {
            return Optional.empty();
        } else {
            return Optional.of(DateRange.of(max(from, other.from), min(to, other.to)));
        }
    }
    
    private static LocalDate max(LocalDate date1, LocalDate date2) {
        return date1.isAfter(date2) ? date1 : date2;
    }
    
    private static LocalDate min(LocalDate date1, LocalDate date2) {
        return date1.isBefore(date2) ? date1 : date2;
    }
    
    @Override
    public String toString() {
        String fromString = from .equals(LocalDate.MIN) ? "" : from.toString();
        String toString = from .equals(MAX) ? "" : to.toString();
        return "[" + fromString + " - " + toString + "]";
    }
    
    public String toUrlString() {
        String fromString = from .equals(LocalDate.MIN) ? "" : from.toString();
        String toString = from .equals(MAX) ? "" : to.toString();
        return fromString + "_" + toString;
    }
    
    public static DateRange parseFromUrl(String dateIntervalString) {
        String[] parts = dateIntervalString.split("_");
               
        return new DateRange(LocalDate.parse(parts[0]), LocalDate.parse(parts[1]));
    }
    
    @Override
    public int compareTo(DateRange other) {
        return from.compareTo(other.from);
    }

    @Override
    public Iterator<LocalDate> iterator() {
        return new Iterator<LocalDate>() {
            
            private LocalDate date = from.minusDays(1);
            
            @Override
            public boolean hasNext() {
                return !date.equals(to);
            }

            @Override
            public LocalDate next() {
                date = date.plusDays(1);
                return date;
            }
        };
    }

}
    
