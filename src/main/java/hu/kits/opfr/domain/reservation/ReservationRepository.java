package hu.kits.opfr.domain.reservation;

import java.time.LocalDate;
import java.util.List;

import hu.kits.opfr.common.DateRange;
import hu.kits.opfr.domain.user.User;

public interface ReservationRepository {

    void save(Reservation reservation);

    List<Reservation> load(LocalDate date, String courtId);

    List<Reservation> load(DateRange dateRange, User user);

    List<Reservation> load(DateRange dateRange);

}
