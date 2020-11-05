package hu.kits.opfr.domain.common;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import hu.kits.opfr.common.Pair;

public record TimeRange(int startAt, int hours) {

    public TimeRange {
        
        if(hours < 1) {
            throw new IllegalArgumentException("hours must > 1");
        }
        
        if(! (0 <= startAt && startAt <= 23) ) {
            throw new IllegalArgumentException("starAt must be between 0 and 23");
        }
        
        if(! (startAt + hours <= 24) ) {
            throw new IllegalArgumentException("end must be < 24");
        }
        
    }
    
    public int endAt() {
        return startAt + hours;
    }
    
    public boolean intersectWith(TimeRange other) {
        
        if(endAt() <= other.startAt()) {
            return false;
        } else if(other.endAt() <= startAt()) {
            return false;
        } else {
            return true;            
        }
    }
    
    public boolean contains(TimeRange other) {
        return startAt <= other.startAt && other.endAt() <= endAt();
    }
    
    public List<TimeRange> minus(List<TimeRange> ranges) {
        List<TimeRange> result = new ArrayList<>();
        result.add(this);
        for(var range : ranges) {
            result = result.stream().flatMap(r -> r.minus(range).stream()).collect(toList());
        }
        return result;
    }
    
    public List<TimeRange> minus(TimeRange other) {
        if(!this.intersectWith(other)) {
            return List.of(this);
        } else if(other.contains(this)) {
            return List.of();
        } else {
            List<TimeRange> result = new ArrayList<>();
            if(startAt < other.startAt) {
                result.add(new TimeRange(startAt, other.startAt - startAt));
            }
            if(other.endAt() < endAt()) {
                result.add(new TimeRange(other.endAt(), endAt()-other.endAt()));
            }
            return result;
        } 
    }
    
    public TimeRange merge(TimeRange other) {
        if(startAt <= other.endAt() || other.startAt <= endAt()) {
            int mergedStartAt = Math.min(startAt, other.startAt);
            int mergedEndAt = Math.max(endAt(), other.endAt());
            return new TimeRange(mergedStartAt, mergedEndAt - mergedStartAt);
        } else {
            throw new IllegalArgumentException("Can not merge " + this + " with " + other);
        }
    }
    
    public static List<TimeRange> union(List<TimeRange> ranges) {
        List<TimeRange> result = new ArrayList<>();
        List<TimeRange> rangesSorted = ranges.stream().sorted(comparing(TimeRange::startAt)).collect(toList());
        
        int i=0;
        while(i<rangesSorted.size()) {
            TimeRange merged = rangesSorted.get(i);
            i++;
            while(i<rangesSorted.size()) {
                var range = rangesSorted.get(i);
                if(merged.endAt() >= range.startAt) {
                    merged = merged.merge(range);
                    i++;
                } else {
                    break;    
                }
            }
            result.add(merged);
        }
        
        return result;
    }
    
    public String format() {
        return startAt + ":00 - " + endAt() + ":00";
    }

}
