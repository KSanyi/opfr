package hu.kits.opfr.infrastructure.database;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import hu.kits.opfr.common.DateRange;
import hu.kits.opfr.domain.reservation.Reservation;
import hu.kits.opfr.domain.reservation.ReservationRepository;

public class ReservationsFakeRepository implements ReservationRepository {

    private final List<Reservation> reservations = new ArrayList<>();
    
    @Override
    public Optional<Reservation> findReservation(String reservationId) {
        return reservations.stream().filter(res -> res.id().equals(reservationId)).findAny();
    }
    
    @Override
    public void save(Reservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public void delete(String reservationId) {
        reservations.removeIf(res -> res.id().equals(reservationId));
    }
    
    @Override
    public List<Reservation> load(LocalDate date, String courtId) {
        return reservations.stream()
            .filter(res -> res.courtId().equals(courtId))
            .filter(res -> res.dailyTimeRange().date().equals(date))
            .collect(toList());
    }

    @Override
    public List<Reservation> load(DateRange dateRange, String userId) {
        return reservations.stream()
                .filter(res -> dateRange.contains(res.dailyTimeRange().date()))
                .filter(res -> res.user().userId().equals(userId))
                .collect(toList());
    }

    @Override
    public List<Reservation> load(DateRange dateRange) {
        return reservations.stream()
                .filter(res -> dateRange.contains(res.dailyTimeRange().date()))
                .collect(toList());
    }

}
