package hu.kits.tennis.infrastructure.database;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import hu.kits.tennis.common.DateRange;
import hu.kits.tennis.domain.reservation.Reservation;
import hu.kits.tennis.domain.reservation.ReservationRepository;
import hu.kits.tennis.domain.user.User;

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
                .filter(res -> res.user().id().equals(user.id()))
                .collect(toList());
    }

    @Override
    public List<Reservation> load(DateRange dateRange) {
        return reservations.stream()
                .filter(res -> dateRange.contains(res.dailyTimeRange().date()))
                .collect(toList());
    }

}
