package hu.kits.opfr.domain.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface ReservationSettingsRepository {

    void saveReservationOpenTime(LocalDate date, LocalTime openAt);

    Optional<LocalTime> getReservationOpenTimeFor(LocalDate today);
}
