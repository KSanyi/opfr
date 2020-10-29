package hu.kits.opfr.infrastructure.database;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;

import hu.kits.opfr.domain.reservation.ReservationSettingsRepository;

public class ReservationSettingsJdbcRepository implements ReservationSettingsRepository {

    private static final String TABLE_RESERVATION_OPEN = "RESERVATION_OPEN";
    private static final String COLUMN_DATE = "DATE";
    private static final String COLUMN_OPEN_AT = "OPEN_AT";
    
    private final Jdbi jdbi;
    
    public ReservationSettingsJdbcRepository(DataSource dataSource) {
        jdbi = Jdbi.create(dataSource);
    }
    
    @Override
    public void saveReservationOpenTime(LocalDate date, LocalTime openAt) {
        
        var dataMap = Map.of(
                COLUMN_DATE, date,
                COLUMN_OPEN_AT, openAt);
        
        jdbi.withHandle(handle -> JdbiUtil.createInsertStatement(handle, TABLE_RESERVATION_OPEN, dataMap).execute()); 
    }

    @Override
    public Optional<LocalTime> getReservationOpenTimeFor(LocalDate date) {

        String sql = String.format("SELECT %s FROM %s WHERE %s = :date", COLUMN_OPEN_AT, TABLE_RESERVATION_OPEN, COLUMN_DATE);
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql)
            .bind("date", date)
            .map((rs, ctx) -> rs.getTime(COLUMN_OPEN_AT)).findOne()).map(Time::toLocalTime);
    }

}
