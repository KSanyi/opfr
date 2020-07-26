package hu.kits.opfr.domain.common;

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
    
}
