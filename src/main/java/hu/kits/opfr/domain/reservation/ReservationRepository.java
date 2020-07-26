package hu.kits.opfr.domain.reservation;

import java.time.LocalDate;
import java.util.List;

import hu.kits.opfr.common.DateRange;
import hu.kits.opfr.domain.user.UserData;

public interface ReservationRepository {

    void save(Reservation reservation);

    List<Reservation> load(LocalDate date, String courtId);

    List<Reservation> load(DateRange dateRange, UserData user);

    List<Reservation> load(DateRange dateRange);

}
