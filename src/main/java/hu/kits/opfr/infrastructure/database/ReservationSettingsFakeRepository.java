package hu.kits.opfr.infrastructure.database;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import hu.kits.opfr.domain.reservation.ReservationSettingsRepository;

public class ReservationSettingsFakeRepository implements ReservationSettingsRepository {

    private final Map<LocalDate, LocalTime> reservationOpenTimes = new TreeMap<>();
    
    @Override
    public void saveReservationOpenTime(LocalDate date, LocalTime openAt) {
        reservationOpenTimes.put(date, openAt);
    }

    @Override
    public Optional<LocalTime> getReservationOpenTimeFor(LocalDate today) {
        return Optional.ofNullable(reservationOpenTimes.get(today));
    }

}
