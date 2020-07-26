package hu.kits.opfr.domain.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TimeRangeTest {

    @Test
    void construction() {
        
        assertThrows(IllegalArgumentException.class, () -> {
            new TimeRange(24, 1);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new TimeRange(10, 15);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new TimeRange(10, -1);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new TimeRange(10, 0);
        });

        new TimeRange(0, 1);
        new TimeRange(22, 2);
    }
    
    @Test
    void intersection() {
        
        assertTrue(new TimeRange(10, 1).intersectWith(new TimeRange(10, 1)));
        assertTrue(new TimeRange(10, 2).intersectWith(new TimeRange(11, 1)));
        assertTrue(new TimeRange(11, 1).intersectWith(new TimeRange(10, 2)));
        assertTrue(new TimeRange(12, 1).intersectWith(new TimeRange(10, 4)));
        assertTrue(new TimeRange(10, 4).intersectWith(new TimeRange(12, 1)));
        
        assertFalse(new TimeRange(10, 2).intersectWith(new TimeRange(12, 1)));
        assertFalse(new TimeRange(12, 1).intersectWith(new TimeRange(10, 2)));
        assertFalse(new TimeRange(10, 2).intersectWith(new TimeRange(15, 2)));
        assertFalse(new TimeRange(15, 2).intersectWith(new TimeRange(10, 2)));
    }
    
}
