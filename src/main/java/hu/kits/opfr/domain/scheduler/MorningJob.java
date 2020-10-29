package hu.kits.opfr.domain.scheduler;

import java.util.List;

import hu.kits.opfr.domain.reservation.ReservationService;
import hu.kits.opfr.domain.scheduler.task.ReservationOpenTimeDrawerTask;

public class MorningJob extends DailyJob {

    public MorningJob(ReservationService reservationService) {
        super("Morning job", List.of(DailyTime.T_05_00, DailyTime.of(9, 21)), new ReservationOpenTimeDrawerTask(reservationService));
    }
    
}
