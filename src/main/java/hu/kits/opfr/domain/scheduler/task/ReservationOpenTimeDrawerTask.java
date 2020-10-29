package hu.kits.opfr.domain.scheduler.task;

import java.time.LocalTime;
import java.util.Optional;

import hu.kits.opfr.domain.reservation.ReservationService;
import hu.kits.opfr.domain.scheduler.Task;

public class ReservationOpenTimeDrawerTask implements Task {

    private final ReservationService reservationService;
    
    public ReservationOpenTimeDrawerTask(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public String name() {
        return "Reservation open time drawer task";
    }

    @Override
    public String run() {
        Optional<LocalTime> reservationOpeningTimeDrawn = reservationService.drawTodaysReservationOpeningTime();
        if(reservationOpeningTimeDrawn.isEmpty()) {
            return "Reservation opening time was already set";
        } else {
            return "Reservation opening time drawn: " + reservationOpeningTimeDrawn.get();
        }
    }
    
}
