package hu.kits.opfr.domain.scheduler.task;

import hu.kits.opfr.domain.reservation.ReservationService;
import hu.kits.opfr.domain.scheduler.Task;

public class ReservationReminderEmailSenderTask implements Task {

    private final ReservationService reservationService;
    
    public ReservationReminderEmailSenderTask(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public String name() {
        return "Reminder email sender task";
    }

    @Override
    public String run() {
        int emailsSent = reservationService.sendReminderEmailsForUpcomingReservations();
        return emailsSent + " emails sent";
    }
    
}
