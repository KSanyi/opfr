package hu.kits.opfr.infrastructure.database;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import hu.kits.opfr.common.DateRange;
import hu.kits.opfr.domain.reservation.Reservation;
import hu.kits.opfr.domain.reservation.ReservationRepository;
import hu.kits.opfr.domain.user.User;

public class ReservationFakeRepository implements ReservationRepository {

    private final List<Reservation> reservations = new ArrayList<>();
    
    @Override
    public void save(Reservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public List<Reservation> load(LocalDate date, String courtId) {
        return reservations.stream()
            .filter(res -> res.courtId().equals(courtId))
            .filter(res -> res.dailyTimeRange().date().equals(date))
            .collect(toList());
    }

    @Override
    public List<Reservation> load(DateRange dateRange, User user) {
        return reservations.stream()
                .filter(res -> dateRange.contains(res.dailyTimeRange().date()))
                .filter(res -> res.user().userId().equals(user.userId()))
                .collect(toList());
    }

    @Override
    public List<Reservation> load(DateRange dateRange) {
        return reservations.stream()
                .filter(res -> dateRange.contains(res.dailyTimeRange().date()))
                .collect(toList());
    }

}
