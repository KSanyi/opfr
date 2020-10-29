package hu.kits.opfr.domain.scheduler;

import static java.util.Collections.singletonList;

import java.time.LocalDateTime;
import java.util.List;

public class DailyJob extends Job {

    public final List<DailyTime> scheduledAt;
    
    public DailyJob(String name, List<DailyTime> scheduledAt, Task ... tasks) {
        super(name, tasks);
        this.scheduledAt = scheduledAt;
    }
    
    public DailyJob(String name, DailyTime scheduledAt, Task ... tasks) {
        this(name, singletonList(scheduledAt), tasks);
    }
    
    @Override
    public LocalDateTime nextExecution() {
        return scheduledAt.stream().map(DailyTime::next).min(LocalDateTime::compareTo).get();
    }
    
    @Override
    public String toString() {
        return name + " " + scheduledAt;
    }
    
}
