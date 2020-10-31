package hu.kits.opfr.domain.scheduler;

import hu.kits.opfr.domain.reservation.ReservationService;
import hu.kits.opfr.domain.scheduler.task.ReservationOpenTimeDrawerTask;
import hu.kits.opfr.domain.scheduler.task.ReservationReminderEmailSenderTask;

public class Jobs {

    public static class MorningJob extends DailyJob {

        public MorningJob(ReservationService reservationService) {
            super("Morning job", DailyTime.T_05_00, new ReservationOpenTimeDrawerTask(reservationService));
        }
        
    }
    
    public static class ReservationReminderSenderJob extends DailyJob {

        public ReservationReminderSenderJob(ReservationService reservationService) {
            super("Reservation reminder sender job", DailyTime.EVERY_HOUR, new ReservationReminderEmailSenderTask(reservationService));
        }
        
    }
    
}
