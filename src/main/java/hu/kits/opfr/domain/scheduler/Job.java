package hu.kits.opfr.domain.scheduler;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.opfr.common.Clock;

public abstract class Job {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public final String name;
    
    public final List<Task> tasks;

    public Job(String name, Task ... tasks) {
        this.name = name;
        this.tasks = Arrays.asList(tasks);
    }
    
    public abstract LocalDateTime nextExecution();
    
    public String execute() {
        List<String> results = new ArrayList<>();
        for(Task task : tasks) {
            String result = runTask(task);
            results.add(task.name() + ": " + result);
        }
        return String.join(", ", results);
    }
    
    private static String runTask(Task task) {
        try {
            log.info("Running task {}", task.name());
            String result = task.run();
            log.info("Task executed successfully with result: {}", result);
            return result;
        } catch (Exception ex) {
            log.error("Error running task {}", task.name(), ex);
            return ex.getMessage();
        }
    }
    
    public String timeUntilNextExecution() {
        long hoursLeft = Clock.now().until(nextExecution(), ChronoUnit.HOURS);
        
        return (hoursLeft < 30 ? hoursLeft + " hours" : hoursLeft/24+1 + " days"); 
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
