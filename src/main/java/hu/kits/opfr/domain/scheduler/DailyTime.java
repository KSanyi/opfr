package hu.kits.opfr.domain.scheduler;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import hu.kits.opfr.common.Clock;

public class DailyTime {

    public static final DailyTime T_01_00 = DailyTime.of( 1, 0);
    public static final DailyTime T_02_00 = DailyTime.of( 2, 0);
    public static final DailyTime T_03_00 = DailyTime.of( 3, 0);
    public static final DailyTime T_04_00 = DailyTime.of( 4, 0);
    public static final DailyTime T_05_00 = DailyTime.of( 5, 0);
    public static final DailyTime T_06_00 = DailyTime.of( 6, 0);
    public static final DailyTime T_07_00 = DailyTime.of( 7, 0);
    public static final DailyTime T_08_00 = DailyTime.of( 8, 0);
    public static final DailyTime T_09_00 = DailyTime.of( 9, 0);
    public static final DailyTime T_10_00 = DailyTime.of(10, 0);
    public static final DailyTime T_11_00 = DailyTime.of(11, 0);
    public static final DailyTime T_12_00 = DailyTime.of(12, 0);
    public static final DailyTime T_13_00 = DailyTime.of(13, 0);
    public static final DailyTime T_14_00 = DailyTime.of(14, 0);
    public static final DailyTime T_15_00 = DailyTime.of(15, 0);
    public static final DailyTime T_16_00 = DailyTime.of(16, 0);
    public static final DailyTime T_17_00 = DailyTime.of(17, 0);
    public static final DailyTime T_18_00 = DailyTime.of(18, 0);
    public static final DailyTime T_19_00 = DailyTime.of(19, 0);
    public static final DailyTime T_20_00 = DailyTime.of(20, 0);
    public static final DailyTime T_21_00 = DailyTime.of(21, 0);
    public static final DailyTime T_22_00 = DailyTime.of(22, 0);
    public static final DailyTime T_23_00 = DailyTime.of(23, 0);
    public static final DailyTime[] EVERY_HOUR = IntStream.rangeClosed(0, 23).mapToObj(hour -> DailyTime.of(hour, 0)).toArray(DailyTime[]::new);
    
    public final int hour;
    
    public final int min;

    private DailyTime(int hour, int min) {
        
        if(hour < 0 || hour > 24) throw new IllegalArgumentException("Invalid hour:" + hour);
        if(min < 0 || min > 60) throw new IllegalArgumentException("Invalid hour:" + min);
        
        this.hour = hour;
        this.min = min;
    }
    
    public LocalDateTime next() {
        LocalDateTime now = Clock.now();
        LocalDateTime at =  LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), hour, min);
        if(at.isAfter(now)) {
            return at;
        } else {
            return at.plusDays(1);
        }
    }
    
    public static DailyTime of(int hour, int min) {
        return new DailyTime(hour, min);
    }
    
    @Override
    public String toString() {
        return String.format("%d:%02d", hour, min);
    }
    
}
