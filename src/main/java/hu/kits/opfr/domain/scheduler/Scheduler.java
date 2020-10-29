package hu.kits.opfr.domain.scheduler;

import java.lang.invoke.MethodHandles;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.opfr.common.Clock;

public class Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
    
    private final List<Job> jobs = new ArrayList<>();
    
    public Scheduler() {
        executor.scheduleAtFixedRate(() -> logScheduledJobs(), 3, 4 * 60 * 60, TimeUnit.SECONDS);
    }

    public void addJob(Job job) {
        schedule(job);
        jobs.add(job);
    }
    
    private void schedule(Job job) {
        long delay = Clock.now().until(job.nextExecution(), ChronoUnit.SECONDS);
        
        executor.schedule(() -> executeActionAndReschedule(job), delay, TimeUnit.SECONDS);
        
        logger.info("Job {} scheduled for next execution: {}", job, job.nextExecution());
    }
    
    private void logScheduledJobs() {
        logger.info("Scheduled jobs:");
        jobs.stream().forEach(job -> logger.info("{} scheduled for {} (in {})", job.name, job.nextExecution(), job.timeUntilNextExecution()));
    }
    
    private void executeActionAndReschedule(Job job) {
        try {
            logger.debug("Current time: {}", Clock.now());
            logger.info("Executing job '{}'", job.name);
            String result = job.execute();
            logger.info("Job '{}' executed with result: {}", job.name, result);
            reSchedule(job);
        } catch(Exception ex) {
            logger.info("Job {} failed: {}", job.name, ex.getMessage());
            reSchedule(job);
        }
    }
    
    private void reSchedule(Job job) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {}
        schedule(job);
    }
    
}
