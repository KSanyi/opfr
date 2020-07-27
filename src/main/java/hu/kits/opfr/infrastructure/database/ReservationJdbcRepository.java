package hu.kits.opfr.infrastructure.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;

import hu.kits.opfr.common.DateRange;
import hu.kits.opfr.domain.common.DailyTimeRange;
import hu.kits.opfr.domain.common.TimeRange;
import hu.kits.opfr.domain.reservation.Reservation;
import hu.kits.opfr.domain.reservation.ReservationRepository;
import hu.kits.opfr.domain.user.UserData;
import hu.kits.opfr.domain.user.UserRepository;
import hu.kits.opfr.domain.user.Users;

public class ReservationJdbcRepository implements ReservationRepository {

    private static final String TABLE_RESERVATION = "RESERVATION";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_USERID = "USERID";
    private static final String COLUMN_COURT_ID = "COURT_ID";
    private static final String COLUMN_DATE = "DATE";
    private static final String COLUMN_START_AT = "START_AT";
    private static final String COLUMN_HOURS = "HOURS";
    private static final String COLUMN_TIMESTAMP = "TIMESTAMP";
    private static final String COLUMN_COMMENT = "COMMENT";
    
    private final Jdbi jdbi;
    private final UserRepository userRepository;
    
    public ReservationJdbcRepository(DataSource dataSource, UserRepository userRepository) {
        jdbi = Jdbi.create(dataSource);
        this.userRepository = userRepository;
    }
    
    @Override
    public void save(Reservation reservation) {
        jdbi.withHandle(handle -> JdbiUtil.createInsertStatement(handle, TABLE_RESERVATION, createMap(reservation)).execute());    
    }

    private static Map<String, Object> createMap(Reservation reservation) {
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put(COLUMN_ID, reservation.id());
        valuesMap.put(COLUMN_USERID, reservation.user().userId());
        valuesMap.put(COLUMN_COURT_ID, reservation.courtId());
        valuesMap.put(COLUMN_DATE, reservation.dailyTimeRange().date());
        valuesMap.put(COLUMN_START_AT, reservation.dailyTimeRange().timeRange().startAt());
        valuesMap.put(COLUMN_HOURS, reservation.dailyTimeRange().timeRange().hours());
        valuesMap.put(COLUMN_COMMENT, reservation.comment());
        
        return valuesMap;
    }

    @Override
    public List<Reservation> load(LocalDate date, String courtId) {
        
        Users users = userRepository.loadAllUsers();
        
        String sql = String.format("SELECT * FROM %s WHERE %s = :date AND %s = :courtId", TABLE_RESERVATION, COLUMN_DATE, COLUMN_COURT_ID);
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql)
            .bind("courtId", courtId)
            .bind("date", date)
            .map((rs, ctx) -> mapToReservation(rs, users)).list());
    }

    @Override
    public List<Reservation> load(DateRange dateRange, UserData user) {
        
        Users users = userRepository.loadAllUsers();
        
        String sql = String.format("SELECT * FROM %s WHERE %s = :userId AND %s BETWEEN :from AND :to", TABLE_RESERVATION, COLUMN_USERID, COLUMN_DATE);
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql)
            .bind("userId", user.userId())
            .bind("from", dateRange.from())
            .bind("to", dateRange.to())
            .map((rs, ctx) -> mapToReservation(rs, users)).list());
    }

    @Override
    public List<Reservation> load(DateRange dateRange) {
        
        Users users = userRepository.loadAllUsers();
        
        String sql = String.format("SELECT * FROM %s WHERE %s BETWEEN :from AND :to", TABLE_RESERVATION, COLUMN_DATE);
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql)
            .bind("from", dateRange.from())
            .bind("to", dateRange.to())
            .map((rs, ctx) -> mapToReservation(rs, users)).list());
    }
    
    private static Reservation mapToReservation(ResultSet rs, Users users) throws SQLException {
        
        return new Reservation(rs.getString(COLUMN_ID), 
                users.getUser(rs.getString(COLUMN_USERID)),
                rs.getString(COLUMN_COURT_ID),
                new DailyTimeRange(rs.getDate(COLUMN_DATE).toLocalDate(), 
                        new TimeRange(rs.getInt(COLUMN_START_AT), rs.getInt(COLUMN_HOURS))), 
                rs.getTimestamp(COLUMN_TIMESTAMP).toLocalDateTime(), 
                rs.getString(COLUMN_COMMENT));
    }
    

}
