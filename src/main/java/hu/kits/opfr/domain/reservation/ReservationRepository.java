package hu.kits.opfr.domain.reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import hu.kits.opfr.common.DateRange;

public interface ReservationRepository {

    Optional<Reservation> findReservation(String reservationId);
    
    void save(Reservation reservation);
    
    void delete(String reservationId);

    List<Reservation> load(LocalDate date, String courtId);

    List<Reservation> load(DateRange dateRange, String playerId);

    List<Reservation> load(DateRange dateRange);

}
