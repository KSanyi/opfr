package hu.kits.tennis.domain.reservation;

import java.time.LocalDate;
import java.util.List;

import hu.kits.tennis.common.DateRange;
import hu.kits.tennis.domain.user.User;

public interface ReservationRepository {

    void save(Reservation reservation);

    List<Reservation> load(LocalDate date, String courtId);

    List<Reservation> load(DateRange dateRange, User user);

    List<Reservation> load(DateRange dateRange);

}
